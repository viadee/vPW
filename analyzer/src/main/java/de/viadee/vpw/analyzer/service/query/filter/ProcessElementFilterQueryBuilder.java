package de.viadee.vpw.analyzer.service.query.filter;

import org.apache.lucene.search.join.ScoreMode;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.join.query.JoinQueryBuilders;
import org.springframework.stereotype.Component;

import de.viadee.vpw.analyzer.dto.entity.filter.ProcessElementFilter;
import de.viadee.vpw.analyzer.util.ESConstants;

@Component
public class ProcessElementFilterQueryBuilder implements CalculationFilterQueryBuilder<ProcessElementFilter> {

    @Override
    public QueryBuilder buildQuery(ProcessElementFilter filter) {

        // Search for all process instance that passed all selected process elements.
        // The process instance needs a child with the activity id of the process element id for every process element id
        BoolQueryBuilder query = QueryBuilders.boolQuery();
        filter.getProcessElementIds().forEach(id -> query.must(JoinQueryBuilders
                .hasChildQuery(ESConstants.TYPE_ACTIVITY, QueryBuilders.termQuery(ESConstants.FIELD_ACTIVITY_ID, id),
                        ScoreMode.Total)));

        return query;
    }
}
