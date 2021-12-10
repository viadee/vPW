package de.viadee.vpw.analyzer.service.query.filter;

import java.util.Date;

import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.springframework.stereotype.Component;

import de.viadee.vpw.analyzer.dto.entity.filter.DateFilter;
import de.viadee.vpw.analyzer.util.ESConstants;

@Component
public class DateFilterQueryBuilder implements CalculationFilterQueryBuilder<DateFilter> {

    @Override
    public QueryBuilder buildQuery(DateFilter filter) {
        return QueryBuilders.rangeQuery(ESConstants.FIELD_START_TIME).gte(getTime(filter.getFrom()))
                .lte(getTime(filter.getTo()));
    }

    private Long getTime(Date date) {
        return date == null ? null : date.getTime();
    }
}
