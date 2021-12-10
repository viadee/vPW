package de.viadee.vpw.analyzer.data.typelist;

import java.util.Arrays;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

/**
 * Aggregation operators for indicators.
 */
public enum AggregationOperator {

    SUM("SUM"), AVG("AVG"), MIN("MIN"), MAX("MAX");

    private final String value;

    AggregationOperator(String value) {
        this.value = value;
    }

    @JsonCreator
    public static AggregationOperator fromValue(String value) {
        return Arrays.stream(values()).filter(t -> t.value.equalsIgnoreCase(value)).findFirst().orElse(null);
    }

    @JsonValue
    public String getValue() {
        return value;
    }
}
