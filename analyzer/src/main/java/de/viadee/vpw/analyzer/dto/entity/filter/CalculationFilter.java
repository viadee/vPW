package de.viadee.vpw.analyzer.dto.entity.filter;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "type")
// @formatter:off
@JsonSubTypes({
    @JsonSubTypes.Type(value = CombinedFilter.class),
    @JsonSubTypes.Type(value = DateFilter.class),
    @JsonSubTypes.Type(value = ProcessElementFilter.class),
    @JsonSubTypes.Type(value = VariableFilter.class)
})
// @formatter:on
public interface CalculationFilter {

}
