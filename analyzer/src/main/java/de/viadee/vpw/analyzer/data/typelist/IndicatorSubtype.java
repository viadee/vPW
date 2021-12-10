package de.viadee.vpw.analyzer.data.typelist;

import java.util.Arrays;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum IndicatorSubtype {

    DURATION("duration"), COUNT("count"), VARIABLE("variable");

    private final String value;

    IndicatorSubtype(String value) {
        this.value = value;
    }

    @JsonCreator
    public static IndicatorSubtype fromValue(String value) {
        return Arrays.stream(values()).filter(t -> t.value.equalsIgnoreCase(value)).findFirst().orElse(null);
    }

    @JsonValue
    public String getValue() {
        return value;
    }
}
