package de.viadee.vpw.analyzer.service.histogram.elastic.step;

import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import org.elasticsearch.ElasticsearchStatusException;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.rest.RestStatus;
import org.elasticsearch.search.aggregations.AggregationBuilder;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.bucket.histogram.Histogram;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import de.viadee.vpw.analyzer.dto.entity.HistogramBucket;
import de.viadee.vpw.analyzer.dto.entity.filter.CalculationFilter;
import de.viadee.vpw.analyzer.service.ESSearchClient;
import de.viadee.vpw.analyzer.util.ESConstants;
import de.viadee.vpw.analyzer.service.ServiceException;
import de.viadee.vpw.analyzer.service.histogram.elastic.dto.Bounds;
import de.viadee.vpw.analyzer.service.query.ESQueryBuilder;

@Component
public class ESHistogramBucketAggregator {

    private static final Logger LOGGER = LoggerFactory.getLogger(ESHistogramBucketAggregator.class);

    private static final String AGGREGATION_PROCESS_COUNT = "processCount";

    @Lazy
    private final ESQueryBuilder esQueryBuilder;

    private final ESSearchClient esSearchClient;

    @Autowired
    public ESHistogramBucketAggregator(ESQueryBuilder esQueryBuilder, ESSearchClient esSearchClient) {
        this.esQueryBuilder = esQueryBuilder;
        this.esSearchClient = esSearchClient;
    }

    public List<HistogramBucket> bucketForBounds(String processDefinitionId, CalculationFilter filter, Bounds bounds,
            int numberOfBuckets) {
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        searchSourceBuilder.size(0);

        QueryBuilder queryBuilder = esQueryBuilder.build(filter);
        if (queryBuilder != null) {
            searchSourceBuilder.query(queryBuilder);
        }

        double interval = Math.floor((bounds.getMax() - bounds.getMin()) / numberOfBuckets);

        AggregationBuilder aggregationBuilder = AggregationBuilders.histogram(AGGREGATION_PROCESS_COUNT)
                .field(ESConstants.FIELD_START_TIME)
                .interval(interval);
        searchSourceBuilder.aggregation(aggregationBuilder);

        try {
            SearchResponse response = esSearchClient.search(processDefinitionId, searchSourceBuilder);

            Histogram histogram = response.getAggregations().get(AGGREGATION_PROCESS_COUNT);

            return histogram.getBuckets().stream().map(bucket -> {
                long timestamp = Double.valueOf(bucket.getKeyAsString()).longValue();
                return new HistogramBucket(new Date(timestamp), bucket.getDocCount());
            }).collect(Collectors.toList());

        } catch (ElasticsearchStatusException e) {
            if (e.getRootCause() instanceof ElasticsearchStatusException
                    && ((ElasticsearchStatusException) e.getRootCause()).status() == RestStatus.NOT_FOUND) {
                return Collections.emptyList();
            }
            LOGGER.error("Calculation for indicator " + processDefinitionId + " failed", e);
            throw new ServiceException(e, "Calculation for indicator " + processDefinitionId + " failed");
        } catch (Exception e) {
            LOGGER.error("Calculation for indicator " + processDefinitionId + " failed", e);
            throw new ServiceException(e, "Calculation for indicator " + processDefinitionId + " failed");
        }
    }
}
