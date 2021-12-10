package de.viadee.vpw.analyzer.dto.entity.filter;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;

@JsonTypeName("date")
public class DateFilter implements CalculationFilter {

    private final Date from;

    private final Date to;

    @JsonCreator
    public DateFilter(@JsonProperty("from") Date from, @JsonProperty("to") Date to) {
        this.from = from;
        this.to = to;
    }

    public Date getTo() {
        return to;
    }

    public Date getFrom() {
        return from;
    }
}
