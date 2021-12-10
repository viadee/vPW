package de.viadee.vpw.analyzer.service.processVariable.elasticsearch;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.TermQueryBuilder;
import org.elasticsearch.search.aggregations.AggregationBuilder;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.bucket.filter.Filter;
import org.elasticsearch.search.aggregations.bucket.filter.FilterAggregationBuilder;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import de.viadee.vpw.analyzer.dto.entity.ProcessVariable;
import de.viadee.vpw.analyzer.dto.typelist.ProcessVariableType;
import de.viadee.vpw.analyzer.service.ESSearchClient;
import de.viadee.vpw.analyzer.service.ServiceException;
import de.viadee.vpw.analyzer.service.processVariable.ProcessVariableService;
import de.viadee.vpw.analyzer.util.ESConstants;

@Component
public class ESProcessVariableServiceImpl implements ProcessVariableService {

    private static final String LONG_VARIABLES = "longVariables";

    private static final String DOUBLE_VARIABLES = "doubleVariables";

    private static final String TEXT_VARIABLES = "textVariables";

    private static final String VARIABLES = "variables";
    private static final String VARIABLE_NAME = "variableName";
    private static final String VARIABLE_VALUES = "variableValues";

    private final ESSearchClient esSearchClient;

    @Autowired
    public ESProcessVariableServiceImpl(ESSearchClient esSearchClient) {
        this.esSearchClient = esSearchClient;
    }

    @Override
    public List<ProcessVariable> findByProcessDefinition(String processDefinitionId) {
        return findByProcessDefinition(processDefinitionId, null);
    }

    @Override
    public List<ProcessVariable> findByProcessDefinition(String processDefinitionId, ProcessVariableType type) {
        try {
            SearchResponse response = esSearchClient.search(processDefinitionId, createSearchSourceBuilder());
            return getVariableList(response, type);
        } catch (Exception e) {
            throw new ServiceException(e, e.getMessage());
        }
    }

    @Override
    public List<String> findByProcessDefinitionAndVariableName(String processDefinitionId, String processVariableName) {
        try {
            SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
            searchSourceBuilder.size(0);
            searchSourceBuilder.query(QueryBuilders.termQuery(ESConstants.FIELD_VARIABLE_NAME, processVariableName));
            searchSourceBuilder.aggregation(AggregationBuilders.terms(VARIABLE_NAME).size(1).field(ESConstants.FIELD_VARIABLE_NAME).subAggregation(AggregationBuilders.terms(VARIABLE_VALUES).field(ESConstants.FIELD_TEXT_VALUE_KEYWORD)));
            SearchResponse response = esSearchClient.search(processDefinitionId, searchSourceBuilder);
            return getVariableValueList(response);
        } catch (Exception e) {
            throw new ServiceException(e, e.getMessage());
        }
    }

    private List<String> getVariableValueList(SearchResponse response) {
        Terms variableNames = response.getAggregations().get(VARIABLE_NAME);
        Terms variableValues = variableNames.getBuckets().get(0).getAggregations().get(VARIABLE_VALUES);
        return variableValues.getBuckets().stream().map(v -> v.getKeyAsString()).collect(Collectors.toList());
    }

    private SearchSourceBuilder createSearchSourceBuilder() {
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        searchSourceBuilder.size(0);
        searchSourceBuilder.query(createQueryBuilder());
        searchSourceBuilder.aggregation(createAggregationBuilder());
        return searchSourceBuilder;
    }

    private TermQueryBuilder createQueryBuilder() {
        return QueryBuilders.termQuery(ESConstants.FIELD_TYPE, ESConstants.TYPE_VARIABLE);
    }

    private AggregationBuilder createAggregationBuilder() {
        AggregationBuilder aggregationBuilder = AggregationBuilders.terms(VARIABLES).size(250)
                .field(ESConstants.FIELD_VARIABLE_NAME);
        addSubAggregation(aggregationBuilder, LONG_VARIABLES, ESConstants.FIELD_LONG_VALUE);
        addSubAggregation(aggregationBuilder, DOUBLE_VARIABLES, ESConstants.FIELD_DOUBLE_VALUE);
        addSubAggregation(aggregationBuilder, TEXT_VARIABLES, ESConstants.FIELD_TEXT_VALUE);
        return aggregationBuilder;
    }

    private void addSubAggregation(AggregationBuilder aggregationBuilder, String filterName, String fieldName) {
        FilterAggregationBuilder filter = AggregationBuilders.filter(filterName, QueryBuilders.existsQuery(fieldName));
        aggregationBuilder.subAggregation(filter);
    }

    private List<ProcessVariable> getVariableList(SearchResponse response, ProcessVariableType type) {
        Terms variables = response.getAggregations().get(VARIABLES);
        return variables.getBuckets().stream() //
                .map(this::mapToProcessVariable) //
                .filter(v -> type == null || type == v.getType()) // filter by type (if filter is set)
                .sorted(Comparator.comparing(v -> v.getName().toLowerCase())) // sort by name
                .collect(Collectors.toList());
    }

    private ProcessVariable mapToProcessVariable(Terms.Bucket bucket) {
        ProcessVariable variable = new ProcessVariable();
        variable.setName(bucket.getKeyAsString());

        Filter longVariablesResponse = bucket.getAggregations().get(LONG_VARIABLES);
        if (longVariablesResponse.getDocCount() > 0) {
            variable.setType(ProcessVariableType.NUMERIC);
            return variable;
        }

        Filter doubleVariablesResponse = bucket.getAggregations().get(DOUBLE_VARIABLES);
        if (doubleVariablesResponse.getDocCount() > 0) {
            variable.setType(ProcessVariableType.NUMERIC);
            return variable;
        }

        Filter textVariablesResponse = bucket.getAggregations().get(TEXT_VARIABLES);
        if (textVariablesResponse.getDocCount() > 0) {
            variable.setType(ProcessVariableType.TEXT);
        }

        return variable;
    }
}
