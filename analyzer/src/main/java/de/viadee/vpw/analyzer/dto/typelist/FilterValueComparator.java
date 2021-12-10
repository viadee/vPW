package de.viadee.vpw.analyzer.dto.typelist;

import com.fasterxml.jackson.annotation.JsonCreator;

public enum FilterValueComparator {

    GT(">", true),
    GTE(">=", true),
    LT("<", true),
    LTE("<=", true),
    EQ("=", true),
    NEQ("!=", true),
    NULL("is null", false),
    NOT_NULL("is not null", false),
    EMPTY("is empty", false),
    NOT_EMPTY("is not empty", false);

    private final String label;

    private final boolean valueRequired;

    FilterValueComparator(String label, boolean valueRequired) {
        this.label = label;
        this.valueRequired = valueRequired;
    }

    @JsonCreator
    public static FilterValueComparator forValue(String v) {
        if (v.trim().isEmpty()) {
            return null;
        }
        return FilterValueComparator.valueOf(v);
    }

    public String getLabel() {
        return label;
    }

    public boolean isValueRequired() {
        return valueRequired;
    }
}