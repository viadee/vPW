package de.viadee.vpw.analyzer.service.query.filter;

import static de.viadee.vpw.analyzer.dto.typelist.FilterValueComparator.*;

import org.apache.lucene.search.join.ScoreMode;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.join.query.JoinQueryBuilders;
import org.springframework.stereotype.Component;

import de.viadee.vpw.analyzer.dto.entity.filter.VariableFilter;
import de.viadee.vpw.analyzer.dto.typelist.FilterValueComparator;
import de.viadee.vpw.analyzer.util.ESConstants;

@Component
public class VariableFilterQueryBuilder implements CalculationFilterQueryBuilder<VariableFilter> {

    @Override
    public QueryBuilder buildQuery(VariableFilter filter) {
        FilterValueComparator comparator = filter.getComparator();
        QueryBuilder nameQuery = buildVariableNameQuery(filter);
        QueryBuilder valueQuery = buildVariableValueQuery(filter);
        QueryBuilder variableQuery = buildVariableQuery(comparator, nameQuery, valueQuery);
        QueryBuilder childQuery = buildVariableChildQuery(variableQuery);
        return comparator == NULL ? buildIsNullQuery(childQuery, nameQuery) : childQuery;
    }

    private QueryBuilder buildVariableNameQuery(VariableFilter filter) {
        return QueryBuilders.termQuery(ESConstants.FIELD_VARIABLE_NAME, filter.getKey());
    }

    private QueryBuilder buildVariableValueQuery(VariableFilter filter) {
        FilterValueComparator comparator = filter.getComparator();
        switch (comparator) {
            case NEQ:
            case EQ:
                return buildEqualsQuery(filter);

            case LT:
                return buildLessThanQuery(filter);

            case LTE:
                return buildLessThanOrEqualsQuery(filter);

            case GT:
                return buildGreaterThanQuery(filter);

            case GTE:
                return buildGreaterThanOrEqualsQuery(filter);

            case NULL:
            case NOT_NULL:
                return buildExistsQuery();

            case EMPTY:
            case NOT_EMPTY:
                return buildEmptyStringQuery();

            default:
                throw new IllegalArgumentException("Invalid comparator: " + comparator);
        }
    }

    private QueryBuilder buildVariableQuery(FilterValueComparator comparator, QueryBuilder nameQuery,
            QueryBuilder valueQuery) {
        BoolQueryBuilder query = QueryBuilders.boolQuery();
        query.must(nameQuery);
        return (comparator == NEQ || comparator == NULL || comparator == NOT_EMPTY) ?
                query.mustNot(valueQuery) :
                query.must(valueQuery);
    }

    private QueryBuilder buildIsNullQuery(QueryBuilder childQuery, QueryBuilder nameQuery) {
        return QueryBuilders.boolQuery()
                // activity that sets the variable to NULL explicitly
                .should(childQuery)
                // or: no activity that sets the variable at all
                .should(QueryBuilders.boolQuery().mustNot(buildVariableChildQuery(nameQuery)));
    }

    private QueryBuilder buildVariableChildQuery(QueryBuilder query) {
        QueryBuilder hasVariableChild = JoinQueryBuilders
                .hasChildQuery(ESConstants.TYPE_VARIABLE, query, ScoreMode.Total);
        return JoinQueryBuilders.hasChildQuery(ESConstants.TYPE_ACTIVITY, hasVariableChild, ScoreMode.Total);
    }

    private QueryBuilder buildEqualsQuery(VariableFilter filter) {
        BoolQueryBuilder queryBuilder = QueryBuilders.boolQuery();

        // Variable value can have different types
        Object value = filter.getValue();
        queryBuilder.should(QueryBuilders.termQuery(ESConstants.FIELD_TEXT_VALUE_KEYWORD, value));

        try {
            long longValue = Long.parseLong(value.toString());
            queryBuilder.should(QueryBuilders.termQuery(ESConstants.FIELD_LONG_VALUE, longValue));
        } catch (Exception ignored) {
        }
        try {
            double doubleValue = Double.parseDouble(value.toString());
            queryBuilder.should(QueryBuilders.termQuery(ESConstants.FIELD_DOUBLE_VALUE, doubleValue));
        } catch (Exception ignored) {
        }

        return queryBuilder;
    }

    private QueryBuilder buildLessThanQuery(VariableFilter filter) {
        return QueryBuilders.boolQuery()
                .should(QueryBuilders.rangeQuery(ESConstants.FIELD_LONG_VALUE).lt(filter.getValue()))
                .should(QueryBuilders.rangeQuery(ESConstants.FIELD_DOUBLE_VALUE).lt(filter.getValue()));
    }

    private QueryBuilder buildLessThanOrEqualsQuery(VariableFilter filter) {
        return QueryBuilders.boolQuery()
                .should(QueryBuilders.rangeQuery(ESConstants.FIELD_LONG_VALUE).lte(filter.getValue()))
                .should(QueryBuilders.rangeQuery(ESConstants.FIELD_DOUBLE_VALUE).lte(filter.getValue()));
    }

    private QueryBuilder buildGreaterThanQuery(VariableFilter filter) {
        return QueryBuilders.boolQuery()
                .should(QueryBuilders.rangeQuery(ESConstants.FIELD_LONG_VALUE).gt(filter.getValue()))
                .should(QueryBuilders.rangeQuery(ESConstants.FIELD_DOUBLE_VALUE).gt(filter.getValue()));
    }

    private QueryBuilder buildGreaterThanOrEqualsQuery(VariableFilter filter) {
        return QueryBuilders.boolQuery()
                .should(QueryBuilders.rangeQuery(ESConstants.FIELD_LONG_VALUE).gte(filter.getValue()))
                .should(QueryBuilders.rangeQuery(ESConstants.FIELD_DOUBLE_VALUE).gte(filter.getValue()));
    }

    private QueryBuilder buildExistsQuery() {
        return QueryBuilders.boolQuery()
                .should(QueryBuilders.existsQuery(ESConstants.FIELD_TEXT_VALUE_KEYWORD))
                .should(QueryBuilders.existsQuery(ESConstants.FIELD_LONG_VALUE))
                .should(QueryBuilders.existsQuery(ESConstants.FIELD_DOUBLE_VALUE));
    }

    private QueryBuilder buildEmptyStringQuery() {
        return QueryBuilders.regexpQuery(ESConstants.FIELD_TEXT_VALUE_KEYWORD, "( )*");
    }
}
