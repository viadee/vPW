package de.viadee.vpw.analyzer.service.query.filter;

import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import de.viadee.vpw.analyzer.dto.entity.filter.CombinedFilter;
import de.viadee.vpw.analyzer.dto.typelist.LogicalOperator;
import de.viadee.vpw.analyzer.service.query.ESQueryBuilder;

@Component
public class CombinedFilterQueryBuilder implements CalculationFilterQueryBuilder<CombinedFilter> {

    @Autowired
    private ESQueryBuilder esQueryBuilder;

    @Override
    public QueryBuilder buildQuery(CombinedFilter combinedFilter) {

        BoolQueryBuilder query = QueryBuilders.boolQuery();

        combinedFilter.getFilters().forEach(filter -> {
            QueryBuilder innerQuery = esQueryBuilder.buildFilterQuery(filter);
            if (combinedFilter.getOperator() == LogicalOperator.AND) {
                query.must(innerQuery);
            } else if (combinedFilter.getOperator() == LogicalOperator.OR) {
                query.should(innerQuery);
            }
        });

        return query;
    }
}
