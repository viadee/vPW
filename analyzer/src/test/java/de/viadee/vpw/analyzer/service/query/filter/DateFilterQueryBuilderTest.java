package de.viadee.vpw.analyzer.service.query.filter;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;

import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.RangeQueryBuilder;

import de.viadee.vpw.analyzer.dto.entity.filter.DateFilter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class DateFilterQueryBuilderTest {

    private DateFilterQueryBuilder builder;

    @BeforeEach
    public void setUp() {
        builder = new DateFilterQueryBuilder();
    }

    @Test
    public void testBuildQuery() {
        Instant now = Instant.now();
        Date from = Date.from(now.minus(30, ChronoUnit.DAYS));
        Date to = Date.from(now);
        DateFilter filter = new DateFilter(from, to);

        RangeQueryBuilder range = assertRangeQueryBuilder(builder.buildQuery(filter));

        assertEquals("startTime", range.fieldName());
        assertEquals(from.getTime(), range.from());
        assertEquals(to.getTime(), range.to());
        assertTrue(range.includeLower());
        assertTrue(range.includeUpper());
    }

    @Test
    public void testBuildQuery_toIsNull() {
        DateFilter filter = new DateFilter(new Date(), null);
        RangeQueryBuilder range = assertRangeQueryBuilder(builder.buildQuery(filter));
        assertNull(range.to());
    }

    @Test
    public void testBuildQuery_fromIsNull() {
        DateFilter filter = new DateFilter(null, new Date());
        RangeQueryBuilder range = assertRangeQueryBuilder(builder.buildQuery(filter));
        assertNull(range.from());
    }

    private RangeQueryBuilder assertRangeQueryBuilder(QueryBuilder builder) {
        assertTrue(builder instanceof RangeQueryBuilder);
        return (RangeQueryBuilder) builder;
    }
}