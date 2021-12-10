package de.viadee.vpw.analyzer.data.typelist;

import java.util.Arrays;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum IndicatorType {

    PROCESS("process"), ACTIVITY("activity");

    private final String value;

    IndicatorType(String value) {
        this.value = value;
    }

    @JsonCreator
    public static IndicatorType fromValue(String value) {
        return Arrays.stream(values()).filter(t -> t.value.equalsIgnoreCase(value)).findFirst().orElse(null);
    }

    @JsonValue
    public String getValue() {
        return value;
    }
}
