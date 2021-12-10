package de.viadee.vpw.analyzer.service.query.filter;

import org.elasticsearch.index.query.QueryBuilder;
import org.springframework.core.GenericTypeResolver;

import de.viadee.vpw.analyzer.dto.entity.filter.CalculationFilter;

public interface CalculationFilterQueryBuilder<T extends CalculationFilter> {

    QueryBuilder buildQuery(T filter);

    default Class<?> getFilterClass() {
        return GenericTypeResolver.resolveTypeArgument(getClass(), CalculationFilterQueryBuilder.class);
    }
}
