package de.viadee.vpw.analyzer.dto.entity.filter;


import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;

import de.viadee.vpw.analyzer.dto.typelist.FilterValueComparator;
import de.viadee.vpw.analyzer.dto.validation.VariableFilterConstraint;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@JsonTypeName("variable")
@VariableFilterConstraint
public class VariableFilter implements CalculationFilter {

    @NotBlank
    private final String key;

    // TODO variableType

    @NotNull
    private final FilterValueComparator comparator;

    private final Object value;

    @JsonCreator
    public VariableFilter(@JsonProperty("key") String key, @JsonProperty("comparator") FilterValueComparator comparator,
            @JsonProperty("value") Object value) {
        this.key = key;
        this.comparator = comparator;
        this.value = value;
    }

    public String getKey() {
        return key;
    }

    public FilterValueComparator getComparator() {
        return comparator;
    }

    public Object getValue() {
        return value;
    }
}