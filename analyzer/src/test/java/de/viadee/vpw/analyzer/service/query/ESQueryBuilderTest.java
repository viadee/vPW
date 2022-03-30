package de.viadee.vpw.analyzer.service.query;



import java.util.Date;
import java.util.List;

import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.RangeQueryBuilder;
import org.elasticsearch.index.query.TermQueryBuilder;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.fasterxml.jackson.databind.ObjectMapper;

import de.viadee.vpw.analyzer.dto.entity.filter.CalculationFilter;
import de.viadee.vpw.analyzer.dto.entity.filter.DateFilter;
import de.viadee.vpw.analyzer.util.ESConstants;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@SpringBootTest
public class ESQueryBuilderTest {

   /* Junit4 style to express a rule
   * @Rule
   * public ExpectedException thrown = ExpectedException.none();
   */

    @Lazy
    @Autowired
    private ESQueryBuilder builder;

    @Test
    public void testBuild_withoutFilter() {
        QueryBuilder queryBuilder = builder.build(null);
        assertTypeProcessQuery(queryBuilder);
    }

    @Test
    public void testBuild_withFilter() {
        DateFilter filter = new DateFilter(new Date(), null);

        BoolQueryBuilder queryBuilder = (BoolQueryBuilder) builder.build(filter);

        List<QueryBuilder> must = queryBuilder.must();
        assertEquals(2, must.size());
        assertTypeProcessQuery(must.get(0));
        assertTrue(must.get(1) instanceof RangeQueryBuilder);
    }

    @Test
    public void testBuild_filterNotSupported() {
        /* Junit4 style
         *   thrown.expect(IllegalArgumentException.class);
         *   thrown.expectMessage("TestFilter is not supported");
         *   builder.build(new TestFilter());
         */

        // Junit5
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            builder.build(new TestFilter());
        });
        String expectedMessage = "TestFilter is not supported";
        String actualMessage = exception.getMessage();
        assertTrue(actualMessage.contains(expectedMessage));
    }

    private void assertTypeProcessQuery(QueryBuilder queryBuilder) {
        assertTrue(queryBuilder instanceof TermQueryBuilder);
        TermQueryBuilder termQueryBuilder = (TermQueryBuilder) queryBuilder;
        assertEquals(ESConstants.FIELD_TYPE, termQueryBuilder.fieldName());
        assertEquals(ESConstants.TYPE_PROCESS, termQueryBuilder.value());
    }

    // Minimale Spring-Konfiguration für diese Test-Klasse
    //@org.springframework.boot.test.context.TestConfiguration
    @Configuration
    @ComponentScan("de.viadee.vpw.analyzer.service.query")
    static class TestConfiguration {

        @Bean
        ObjectMapper objectMapper() {
            return new ObjectMapper();
        }
    }

    private class TestFilter implements CalculationFilter {

    }
}