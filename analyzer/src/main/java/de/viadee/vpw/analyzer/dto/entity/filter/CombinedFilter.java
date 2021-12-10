package de.viadee.vpw.analyzer.dto.entity.filter;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;

import de.viadee.vpw.analyzer.dto.typelist.LogicalOperator;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

@JsonTypeName("combined")
public class CombinedFilter implements CalculationFilter {

    @NotNull
    private final LogicalOperator operator;

    @Valid
    @NotEmpty
    private final List<CalculationFilter> filters;

    @JsonCreator
    public CombinedFilter(@JsonProperty("operator") LogicalOperator operator,
            @JsonProperty("filters") List<CalculationFilter> filters) {
        this.operator = operator;
        this.filters = filters;
    }

    public List<CalculationFilter> getFilters() {
        return this.filters;
    }

    public LogicalOperator getOperator() {
        return this.operator;
    }
}