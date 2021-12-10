package de.viadee.vpw.analyzer.service.indicator.elasticsearch;

import java.util.List;
import java.util.stream.Collectors;

import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.join.aggregations.Children;
import org.elasticsearch.search.aggregations.Aggregations;
import org.elasticsearch.search.aggregations.bucket.filter.Filter;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;
import org.elasticsearch.search.aggregations.metrics.NumericMetricsAggregation;
import org.springframework.stereotype.Component;

import de.viadee.vpw.analyzer.data.entity.Indicator;
import de.viadee.vpw.analyzer.data.typelist.IndicatorSubtype;
import de.viadee.vpw.analyzer.dto.entity.IndicatorInstance;
import de.viadee.vpw.analyzer.dto.entity.IndicatorInstanceElement;

@Component
public class ESIndicatorResponseExtractor {

    /**
     * Extract indicator instances from search response
     *
     * @param indicator indicator definition dto
     * @param response  search response from elastic
     * @return search response mapped to indicator instance
     */
    public IndicatorInstance extract(Indicator indicator, SearchResponse response) {
        switch (indicator.getType()) {
            case PROCESS:
                return extractProcessResponse(indicator, response);
            case ACTIVITY:
                return extractActivityResponse(indicator, response.getAggregations());
            default:
                return null;
        }
    }

    /**
     * Extract response for process aggregation
     *
     * @return search response mapped to indicator instance
     */
    private IndicatorInstance extractProcessResponse(Indicator indicator, SearchResponse response) {
        IndicatorInstance indicatorInstance = new IndicatorInstance(indicator.getId());

        IndicatorSubtype subtype = indicator.getSubtype();
        if (subtype == IndicatorSubtype.DURATION) {
            indicatorInstance.addResult(indicator.getOperator(), getDurationValue(response.getAggregations()));

        } else if (subtype == IndicatorSubtype.COUNT) {
            indicatorInstance.addResult(indicator.getOperator(), (double) response.getHits().getTotalHits().value);
        }

        return indicatorInstance;
    }

    /**
     * Extract response for activity aggregation
     *
     * @param indicator    indicator definition dto
     * @param aggregations aggregation result extracted from search response
     * @return search response mapped to indicator instance
     */
    private IndicatorInstance extractActivityResponse(Indicator indicator, Aggregations aggregations) {
        IndicatorInstance instance = new IndicatorInstance(indicator.getId());
        instance.addElementResults(indicator.getOperator(), createIndicatorInstanceElements(indicator, aggregations));
        return instance;
    }

    private List<IndicatorInstanceElement> createIndicatorInstanceElements(Indicator indicator,
            Aggregations aggregations) {
        Terms results = getAggregationResults(aggregations);
        return results.getBuckets()
                .stream()
                .map(bucket -> createIndicatorInstanceElement(indicator, bucket))
                .collect(Collectors.toList());
    }

    private Terms getAggregationResults(Aggregations aggregations) {
        Children children = aggregations.get(ESIndicatorAggregationBuilder.ACTIVITY_CHILDREN_AGGREGATION_NAME);
        Filter filter = children.getAggregations().get(ESIndicatorAggregationBuilder.ACTIVITY_FILTER_AGGREGATION_NAME);
        return filter.getAggregations().get(ESIndicatorAggregationBuilder.ACTIVITY_TERM_AGGREGATION_NAME);
    }

    private IndicatorInstanceElement createIndicatorInstanceElement(Indicator indicator, Terms.Bucket bucket) {
        return new IndicatorInstanceElement(bucket.getKeyAsString(), extractSubtypeResponse(indicator, bucket));
    }

    /**
     * Extract subtype response from process element bucket
     *
     * @param indicator indicator definition dto
     * @param bucket    single process element bucket
     * @return value from aggregation
     */
    private Double extractSubtypeResponse(Indicator indicator, Terms.Bucket bucket) {
        switch (indicator.getSubtype()) {
            case COUNT:
                return (double) bucket.getDocCount();
            case DURATION:
                return getDurationValue(bucket.getAggregations());
            case VARIABLE:
                return extractVariableResponse(indicator, bucket);
            default:
                return null;
        }
    }

    /**
     * Extract variable response from bucket
     *
     * @param indicator indicator definition dto
     * @param bucket    single process element bucket
     * @return value from aggregation
     */
    private Double extractVariableResponse(Indicator indicator, Terms.Bucket bucket) {
        Children variables = bucket.getAggregations()
                .get(ESIndicatorAggregationBuilder.VARIABLE_CHILDREN_AGGREGATION_NAME);
        Filter variable = variables.getAggregations().get(indicator.getVariable());
        NumericMetricsAggregation.SingleValue doubleValue = variable.getAggregations()
                .get(ESIndicatorAggregationBuilder.DOUBLE_VALUE_AGGREGATION_NAME);
        NumericMetricsAggregation.SingleValue longValue = variable.getAggregations()
                .get(ESIndicatorAggregationBuilder.LONG_VALUE_AGGREGATION_NAME);

        if (!Double.isInfinite(doubleValue.value())) {
            return doubleValue.value();
        } else if (!Double.isInfinite(longValue.value())) {
            return longValue.value();
        }

        return null;
    }

    private Double getDurationValue(Aggregations aggregations) {
        NumericMetricsAggregation.SingleValue duration = aggregations
                .get(ESIndicatorAggregationBuilder.DURATION_AGGREGATION_NAME);
        double value = duration.value();
        return Double.isInfinite(value) ? null : value;
    }
}
