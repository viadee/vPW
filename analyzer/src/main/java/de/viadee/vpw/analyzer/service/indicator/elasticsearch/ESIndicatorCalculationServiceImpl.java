package de.viadee.vpw.analyzer.service.indicator.elasticsearch;

import java.util.Optional;
import java.util.UUID;

import org.elasticsearch.ElasticsearchStatusException;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.rest.RestStatus;
import org.elasticsearch.search.aggregations.AggregationBuilder;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import de.viadee.vpw.analyzer.data.entity.Indicator;
import de.viadee.vpw.analyzer.data.repository.IndicatorRepository;
import de.viadee.vpw.analyzer.dto.entity.IndicatorInstance;
import de.viadee.vpw.analyzer.dto.entity.filter.CalculationFilter;
import de.viadee.vpw.analyzer.service.ESSearchClient;
import de.viadee.vpw.analyzer.service.ServiceException;
import de.viadee.vpw.analyzer.service.indicator.IndicatorCalculationService;
import de.viadee.vpw.analyzer.service.query.ESQueryBuilder;

/**
 * Implementation of the indicator calculation for Elasticsearch endpoint
 *
 * @see IndicatorCalculationService
 */
@Component
public class ESIndicatorCalculationServiceImpl implements IndicatorCalculationService {

    private static final Logger LOGGER = LoggerFactory.getLogger(ESIndicatorCalculationServiceImpl.class);

    private final IndicatorRepository indicatorRepository;

    private final ESSearchClient esSearchClient;

    private final ESQueryBuilder queryBuilder;

    private final ESIndicatorAggregationBuilder aggregationBuilder;

    private final ESIndicatorResponseExtractor responseExtractor;

    @Autowired
    public ESIndicatorCalculationServiceImpl(IndicatorRepository indicatorRepository, ESSearchClient esSearchClient,
            ESQueryBuilder queryBuilder, ESIndicatorAggregationBuilder aggregationBuilder,
            ESIndicatorResponseExtractor responseExtractor) {
        this.indicatorRepository = indicatorRepository;
        this.esSearchClient = esSearchClient;
        this.queryBuilder = queryBuilder;
        this.aggregationBuilder = aggregationBuilder;
        this.responseExtractor = responseExtractor;
    }

    /**
     * Calculate indicator instances based on the process engine events
     *
     * @param processDefinitionId process id to calculate
     * @param indicatorId         indicator id to calculate
     * @param filter              filter to limit the calculation result
     * @return calculation result can be null if no data is available
     * @see IndicatorCalculationService
     */
    @Override
    public IndicatorInstance calculate(String processDefinitionId, UUID indicatorId, CalculationFilter filter) {
        Optional<Indicator> indicator = this.indicatorRepository.findById(indicatorId);
        if (indicator.isPresent()) {
            if (indicator.get().getProcessDefinitionId() == null) {
                indicator.get().setProcessDefinitionId(processDefinitionId);
            }
            return this.calculate(indicator.get(), filter);
        }
        throw new ServiceException("Could not find indicator with id '" + indicatorId + "'");
    }

    /**
     * Calculate indicator instances based on the process engine events
     *
     * @param indicator indicator to calculate
     * @param filter    filter to limit the calculation result
     * @return calculation result can be null if no data is available
     * @see IndicatorCalculationService
     */
    @Override
    public IndicatorInstance calculate(Indicator indicator, CalculationFilter filter) {
        // Setup elasticsearch request builder
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();

        // Disable search result size to zero. We only want the aggregations results
        // https://www.elastic.co/guide/en/elasticsearch/reference/current/returning-only-agg-results.html
        searchSourceBuilder.size(0);

        // Build elasticsearch query to filter the aggregation data
        QueryBuilder queryBuilder = this.queryBuilder.build(filter);
        if (queryBuilder != null) {
            searchSourceBuilder.query(queryBuilder);
        }

        // Build elasticsearch aggregation to calculate the indicator
        AggregationBuilder aggregationBuilder = this.aggregationBuilder.build(indicator);
        if (aggregationBuilder != null) {
            searchSourceBuilder.aggregation(aggregationBuilder);
        }else{
            // If there are no aggregations, the number of total hits will be relevant (process count)
            searchSourceBuilder.trackTotalHits(true);
        }

        try {
            // Send sync request to elasticsearch
            SearchResponse response = esSearchClient.search(indicator.getProcessDefinitionId(), searchSourceBuilder);

            // Extract and map response to dto
            return this.responseExtractor.extract(indicator, response);

        } catch (ElasticsearchStatusException e) {
            if (e.getRootCause() instanceof ElasticsearchStatusException
                    && ((ElasticsearchStatusException) e.getRootCause()).status() == RestStatus.NOT_FOUND) {
                return new IndicatorInstance(indicator.getId());
            }
            LOGGER.error("Calculation for indicator " + indicator.getId().toString() + " failed", e);
            throw new ServiceException(e, "Calculation for indicator " + indicator.getId().toString() + " failed");
        } catch (Exception e) {
            LOGGER.error("Calculation for indicator " + indicator.getId().toString() + " failed", e);
            throw new ServiceException(e, "Calculation for indicator " + indicator.getId().toString() + " failed");
        }
    }
}
