package de.viadee.vpw.analyzer.service.histogram.elastic.step;

import org.elasticsearch.ElasticsearchStatusException;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.rest.RestStatus;
import org.elasticsearch.search.aggregations.AggregationBuilder;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.metrics.Max;
import org.elasticsearch.search.aggregations.metrics.Min;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import de.viadee.vpw.analyzer.dto.entity.filter.CalculationFilter;
import de.viadee.vpw.analyzer.service.ESSearchClient;
import de.viadee.vpw.analyzer.util.ESConstants;
import de.viadee.vpw.analyzer.service.ServiceException;
import de.viadee.vpw.analyzer.service.histogram.elastic.dto.Bounds;
import de.viadee.vpw.analyzer.service.query.ESQueryBuilder;

@Component
public class ESHistogramMinMaxAggregator {

    private static final Logger LOGGER = LoggerFactory.getLogger(ESHistogramMinMaxAggregator.class);

    private static final String AGGREGATION_MIN_START_TIME = "minStartTime";

    private static final String AGGREGATION_MAX_START_TIME = "maxStartTime";

    private final ESQueryBuilder esQueryBuilder;

    private final ESSearchClient esSearchClient;

    @Autowired
    public ESHistogramMinMaxAggregator(ESQueryBuilder esQueryBuilder, ESSearchClient esSearchClient) {
        this.esQueryBuilder = esQueryBuilder;
        this.esSearchClient = esSearchClient;
    }

    public Bounds bounds(String processDefinitionId, CalculationFilter filter) {

        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        searchSourceBuilder.size(0);

        QueryBuilder queryBuilder = esQueryBuilder.build(filter);
        if (queryBuilder != null) {
            searchSourceBuilder.query(queryBuilder);
        }

        AggregationBuilder minAggregation = AggregationBuilders.min(AGGREGATION_MIN_START_TIME)
                .field(ESConstants.FIELD_START_TIME);
        searchSourceBuilder.aggregation(minAggregation);

        AggregationBuilder maxAggregation = AggregationBuilders.max(AGGREGATION_MAX_START_TIME)
                .field(ESConstants.FIELD_START_TIME);
        searchSourceBuilder.aggregation(maxAggregation);

        try {
            SearchResponse response = esSearchClient.search(processDefinitionId, searchSourceBuilder);

            Min min = response.getAggregations().get(AGGREGATION_MIN_START_TIME);
            Max max = response.getAggregations().get(AGGREGATION_MAX_START_TIME);

            return new Bounds((long) min.getValue(), (long) max.getValue());

        } catch (ElasticsearchStatusException e) {
            if (e.getRootCause() instanceof ElasticsearchStatusException
                    && ((ElasticsearchStatusException) e.getRootCause()).status() == RestStatus.NOT_FOUND) {
                return null;
            }
            LOGGER.error("Calculation for indicator " + processDefinitionId + " failed", e);
            throw new ServiceException(e, "Calculation for indicator " + processDefinitionId + " failed");
        } catch (Exception e) {
            LOGGER.error("Calculation for indicator " + processDefinitionId + " failed", e);
            throw new ServiceException(e, "Calculation for indicator " + processDefinitionId + " failed");
        }
    }
}
