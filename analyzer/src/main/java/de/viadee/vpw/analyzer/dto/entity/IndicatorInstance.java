package de.viadee.vpw.analyzer.dto.entity;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import de.viadee.vpw.analyzer.data.typelist.AggregationOperator;

public class IndicatorInstance {

    private final UUID indicatorId;

    private Map<AggregationOperator, Double> results = new HashMap<>();

    private Map<AggregationOperator, List<IndicatorInstanceElement>> elements = new HashMap<>();

    public IndicatorInstance(UUID indicatorId) {
        this.indicatorId = indicatorId;
    }

    public UUID getIndicatorId() {
        return indicatorId;
    }

    public Map<AggregationOperator, Double> getResults() {
        return results;
    }

    public void addResult(AggregationOperator operator, Double value) {
        this.results.put(operator, value);
    }

    public Map<AggregationOperator, List<IndicatorInstanceElement>> getElements() {
        return elements;
    }

    public void addElementResults(AggregationOperator aggregationOperator, List<IndicatorInstanceElement> instances) {
        this.elements.put(aggregationOperator, instances);
    }
}
