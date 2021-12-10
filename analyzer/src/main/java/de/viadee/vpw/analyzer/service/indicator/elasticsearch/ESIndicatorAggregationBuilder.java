package de.viadee.vpw.analyzer.service.indicator.elasticsearch;

import java.util.Optional;

import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.join.aggregations.ChildrenAggregationBuilder;
import org.elasticsearch.search.aggregations.AggregationBuilder;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.bucket.filter.FilterAggregationBuilder;
import org.elasticsearch.search.aggregations.bucket.terms.TermsAggregationBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import de.viadee.vpw.analyzer.data.entity.Indicator;
import de.viadee.vpw.analyzer.data.entity.ProcessDefinition;
import de.viadee.vpw.analyzer.data.repository.ProcessDefinitionRepository;
import de.viadee.vpw.analyzer.data.typelist.IndicatorSubtype;
import de.viadee.vpw.analyzer.service.ServiceException;
import de.viadee.vpw.analyzer.util.ESConstants;

/**
 * Build aggregation for Elasticsearch request
 */
@Component
public class ESIndicatorAggregationBuilder {

    static final String DOUBLE_VALUE_AGGREGATION_NAME = "doubleValue";

    static final String LONG_VALUE_AGGREGATION_NAME = "longValue";

    static final String DURATION_AGGREGATION_NAME = "duration";

    static final String ACTIVITY_CHILDREN_AGGREGATION_NAME = "activityInstances";

    static final String ACTIVITY_FILTER_AGGREGATION_NAME = "noMultiInstanceBody";

    static final String ACTIVITY_TERM_AGGREGATION_NAME = "resultsByActivityId";

    static final String VARIABLE_CHILDREN_AGGREGATION_NAME = "variables";

    private final ProcessDefinitionRepository processDefinitionRepository;

    @Autowired
    public ESIndicatorAggregationBuilder(ProcessDefinitionRepository processDefinitionRepository) {
        this.processDefinitionRepository = processDefinitionRepository;
    }

    /**
     * Build aggregation builder for indicator
     *
     * @param indicator indicator definition
     * @return aggregation builder
     */
    public AggregationBuilder build(Indicator indicator) {
        switch (indicator.getType()) {
            case PROCESS:
                return buildProcessAggregation(indicator);
            case ACTIVITY:
                return buildActivityAggregation(indicator);
            default:
                return null;
        }
    }

    /**
     * Build process aggregation
     *
     * @param indicator indicator definition
     * @return aggregation builder for process duration
     */
    private AggregationBuilder buildProcessAggregation(Indicator indicator) {
        IndicatorSubtype subtype = indicator.getSubtype();
        switch (subtype) {
            case COUNT:
                return null;
            case DURATION:
                return buildDurationAggregation(indicator);
            default:
                throw new IllegalArgumentException("Invalid subtype for process aggregation: " + subtype);
        }
    }

    /**
     * Build activity aggregation
     *
     * @param indicator indicator definition
     * @return aggregation builder
     */
    private AggregationBuilder buildActivityAggregation(Indicator indicator) {
        // Children aggregation on all activity events
        ChildrenAggregationBuilder aggregationBuilder = new ChildrenAggregationBuilder(
                ACTIVITY_CHILDREN_AGGREGATION_NAME, ESConstants.TYPE_ACTIVITY);

        // Create buckets for each process element in process definition
        // Limit number of buckets to number of process elements from process definition
        Optional<ProcessDefinition> processDefinition = processDefinitionRepository
                .findById(indicator.getProcessDefinitionId());
        if (processDefinition.isPresent()) {

            TermsAggregationBuilder aggregateByActivityId = AggregationBuilders.terms(ACTIVITY_TERM_AGGREGATION_NAME)
                    .field(ESConstants.FIELD_ACTIVITY_ID).size(processDefinition.get().getElements().size());

            // Add subtype aggregation to aggregate each bucket
            AggregationBuilder subTypeAggregation = buildSubtypeAggregation(indicator);
            if (subTypeAggregation != null) {
                aggregateByActivityId.subAggregation(subTypeAggregation);
            }

            FilterAggregationBuilder filter = AggregationBuilders.filter(ACTIVITY_FILTER_AGGREGATION_NAME,
                    QueryBuilders.boolQuery().mustNot(
                            QueryBuilders.termQuery(ESConstants.FIELD_ACTIVITY_TYPE_KEYWORD, "multiInstanceBody")))
                    .subAggregation(aggregateByActivityId);

            aggregationBuilder.subAggregation(filter);

            return aggregationBuilder;
        }

        throw new ServiceException("Could not find process definition '" + indicator.getProcessDefinitionId() + "'.");
    }

    /**
     * Build aggregation for subtype
     *
     * @param indicator indicator definition
     * @return aggregation builder
     */
    private AggregationBuilder buildSubtypeAggregation(Indicator indicator) {
        switch (indicator.getSubtype()) {
            case DURATION:
                return buildDurationAggregation(indicator);
            case VARIABLE:
                return buildVariableAggregation(indicator);
            default:
                return null;
        }
    }

    /**
     * Build aggregation for duration
     *
     * @param indicator indicator definition
     * @return aggregation builder
     */
    private AggregationBuilder buildDurationAggregation(Indicator indicator) {
        return buildOperationAggregation(indicator, DURATION_AGGREGATION_NAME, ESConstants.FIELD_DURATION_IN_MILLIS);
    }

    /**
     * Build aggregation for subtype variable
     *
     * @param indicator indicator definition
     * @return aggregation builder
     */
    private AggregationBuilder buildVariableAggregation(Indicator indicator) {
        // Children aggregation on all variable events
        AggregationBuilder aggregationBuilder = new ChildrenAggregationBuilder(VARIABLE_CHILDREN_AGGREGATION_NAME,
                ESConstants.TYPE_VARIABLE);

        // Limit variable events to events with variableName
        AggregationBuilder filterAggregation = AggregationBuilders
                .filter(indicator.getVariable(),
                        QueryBuilders.termQuery(ESConstants.FIELD_VARIABLE_NAME, indicator.getVariable()));
        aggregationBuilder.subAggregation(filterAggregation);

        // Add aggregation to doubleValue field
        AggregationBuilder doubleOperationAggregation = buildOperationAggregation(indicator,
                DOUBLE_VALUE_AGGREGATION_NAME,
                ESConstants.FIELD_DOUBLE_VALUE);
        if (doubleOperationAggregation != null) {
            filterAggregation.subAggregation(doubleOperationAggregation);
        }

        // Add aggregation to longValue field
        AggregationBuilder longOperationAggregation = buildOperationAggregation(indicator, LONG_VALUE_AGGREGATION_NAME,
                ESConstants.FIELD_LONG_VALUE);
        if (longOperationAggregation != null) {
            filterAggregation.subAggregation(longOperationAggregation);
        }

        return aggregationBuilder;
    }

    /**
     * Build aggregation for operator
     *
     * @param indicator indicator definition
     * @param name      aggregation name
     * @param field     variable name
     * @return aggregation builder
     */
    private AggregationBuilder buildOperationAggregation(Indicator indicator, String name, String field) {
        switch (indicator.getOperator()) {
            case SUM:
                return AggregationBuilders.sum(name).field(field);
            case AVG:
                return AggregationBuilders.avg(name).field(field);
            case MAX:
                return AggregationBuilders.max(name).field(field);
            case MIN:
                return AggregationBuilders.min(name).field(field);
            default:
                return null;
        }
    }
}
