package de.viadee.vpw.analyzer.dto.typelist;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum ProcessVariableType {

    TEXT("text"), NUMERIC("numeric"), BOOLEAN("boolean");

    private final String value;

    ProcessVariableType(String value) {
        this.value = value;
    }

    @JsonCreator
    public static ProcessVariableType fromValue(String value) {
        for (ProcessVariableType type : ProcessVariableType.values()) {
            if (type.getValue().equalsIgnoreCase(value)) {
                return type;
            }
        }
        return null;
    }

    @JsonValue
    public String getValue() {
        return value;
    }
}