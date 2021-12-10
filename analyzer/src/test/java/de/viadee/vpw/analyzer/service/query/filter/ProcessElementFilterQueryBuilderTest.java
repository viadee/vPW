package de.viadee.vpw.analyzer.service.query.filter;

import java.util.List;

import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.TermQueryBuilder;
import org.elasticsearch.join.query.HasChildQueryBuilder;

import de.viadee.vpw.analyzer.dto.entity.filter.ProcessElementFilter;
import de.viadee.vpw.analyzer.util.ESConstants;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class ProcessElementFilterQueryBuilderTest {

    private ProcessElementFilterQueryBuilder builder;

    @BeforeEach
    public void setUp() {
        builder = new ProcessElementFilterQueryBuilder();
    }

    @Test
    public void testBuildQuery() {
        ProcessElementFilter filter = new ProcessElementFilter("serviceTask1", "serviceTask2");

        BoolQueryBuilder bool = assertBoolQueryBuilder(builder.buildQuery(filter));

        List<QueryBuilder> must = bool.must();
        assertEquals(2, must.size());
        HasChildQueryBuilder hasChild1 = assertHasChildQueryBuilder(must.get(0));
        HasChildQueryBuilder hasChild2 = assertHasChildQueryBuilder(must.get(1));
        validate(hasChild1, "serviceTask1");
        validate(hasChild2, "serviceTask2");
    }

    private void validate(HasChildQueryBuilder hasChild, String value) {
        assertEquals(ESConstants.TYPE_ACTIVITY, hasChild.childType());
        TermQueryBuilder term = assertTermQueryBuilder(hasChild.query());
        assertEquals(ESConstants.FIELD_ACTIVITY_ID, term.fieldName());
        assertEquals(value, term.value());
    }

    private BoolQueryBuilder assertBoolQueryBuilder(QueryBuilder builder) {
        assertTrue(builder instanceof BoolQueryBuilder);
        return (BoolQueryBuilder) builder;
    }

    private HasChildQueryBuilder assertHasChildQueryBuilder(QueryBuilder builder) {
        assertTrue(builder instanceof HasChildQueryBuilder);
        return (HasChildQueryBuilder) builder;
    }

    private TermQueryBuilder assertTermQueryBuilder(QueryBuilder builder) {
        assertTrue(builder instanceof TermQueryBuilder);
        return (TermQueryBuilder) builder;
    }
}