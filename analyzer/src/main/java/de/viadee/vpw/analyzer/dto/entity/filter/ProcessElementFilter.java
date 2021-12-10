package de.viadee.vpw.analyzer.dto.entity.filter;

import java.util.Arrays;
import java.util.List;


import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import jakarta.validation.constraints.NotEmpty;

@JsonTypeName("element")
public class ProcessElementFilter implements CalculationFilter {

    @NotEmpty
    private final List<String> processElementIds;

    @JsonCreator
    public ProcessElementFilter(@JsonProperty("processElementIds") String... processElementIds) {
        this.processElementIds = Arrays.asList(processElementIds);
    }

    public List<String> getProcessElementIds() {
        return processElementIds;
    }
}
