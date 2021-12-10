package de.viadee.vpw.analyzer.service.query.filter;



import org.elasticsearch.index.query.QueryBuilder;


import de.viadee.vpw.analyzer.dto.entity.filter.CalculationFilter;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class CalculationFilterQueryBuilderTest {

    @Test
    public void testGetFilterClass() {
        TestFilterQueryBuilder builder = new TestFilterQueryBuilder();
        assertEquals(TestFilter.class, builder.getFilterClass());
    }

    private interface TestInterface1 {

    }

    private interface TestInterface2<T> {

    }

    private class TestFilter implements CalculationFilter {

    }

    private class TestFilterQueryBuilder
            implements TestInterface1, TestInterface2<String>, CalculationFilterQueryBuilder<TestFilter> {

        @Override
        public QueryBuilder buildQuery(TestFilter filter) {
            return null;
        }
    }
}