package de.viadee.vpw.analyzer.service.query;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;

import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.TermQueryBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import de.viadee.vpw.analyzer.dto.entity.filter.CalculationFilter;
import de.viadee.vpw.analyzer.service.query.filter.CalculationFilterQueryBuilder;
import de.viadee.vpw.analyzer.util.ESConstants;

/**
 * Helper to setup an Elasticsearch filter query used to setup a search request.
 */
@Component
public class ESQueryBuilder {

    private final Logger logger = LoggerFactory.getLogger(ESQueryBuilder.class);

    @Autowired
    private List<CalculationFilterQueryBuilder> builders;

    @Autowired
    private ObjectMapper objectMapper;

    private Map<Class<? extends CalculationFilter>, CalculationFilterQueryBuilder> buildersByFilterClass;

    @PostConstruct
    @SuppressWarnings("unchecked")
    public void setBuildersByFilterClass() {
        buildersByFilterClass = builders.stream().collect(Collectors.toMap(b -> b.getFilterClass(), b -> b));
    }

    /**
     * Build a new Elasticsearch query based on the DTO.
     *
     * @param filter dto to setup the query
     * @return a query builder that can be send to Elasticsearch api
     */
    public QueryBuilder build(CalculationFilter filter) {
        logFilter(filter);

        // Limit to process instance events on the top level
        TermQueryBuilder processQuery = QueryBuilders.termQuery(ESConstants.FIELD_TYPE, ESConstants.TYPE_PROCESS);
        return filter == null ? processQuery : build(processQuery, filter);
    }

    private QueryBuilder build(TermQueryBuilder processQuery, CalculationFilter filter) {
        // Setup bool query builder that links sub query as AND
        BoolQueryBuilder queryBuilder = QueryBuilders.boolQuery();
        queryBuilder.must(processQuery);
        queryBuilder.must(buildFilterQuery(filter));
        return queryBuilder;
    }

    /**
     * Build query builder for a filter filter dto
     *
     * @param filter Filter filter dto too filter on variable values
     * @return a query builder based on the filter filter
     */
    @SuppressWarnings("unchecked")
    public QueryBuilder buildFilterQuery(CalculationFilter filter) {
        CalculationFilterQueryBuilder builder = findQueryBuilder(filter);
        return builder.buildQuery(filter);
    }

    /**
     * Finds the correct QueryBuilder for a concrete CalculationFilter class.
     */
    private CalculationFilterQueryBuilder findQueryBuilder(CalculationFilter filter) {
        Class<? extends CalculationFilter> filterClass = filter.getClass();
        return Optional.ofNullable(buildersByFilterClass.get(filterClass))
                .orElseThrow(() -> new IllegalArgumentException(filterClass.getSimpleName() + " is not supported"));
    }

    private void logFilter(CalculationFilter filter) {
        try {
            logger.debug("build query for filter={}", objectMapper.writeValueAsString(filter));
        } catch (JsonProcessingException ignored) {
        }
    }
}
