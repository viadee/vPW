package de.viadee.vpw.analyzer.service;

import java.io.IOException;

import org.apache.commons.codec.digest.DigestUtils;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.Requests;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import de.viadee.vpw.shared.config.elasticsearch.ElasticsearchProperties;

@Component
public class ESSearchClient {

    private final Logger logger = LoggerFactory.getLogger(ESSearchClient.class);

    private final ElasticsearchProperties elasticsearchProperties;

    private final RestHighLevelClient restHighLevelClient;

    @Autowired
    public ESSearchClient(ElasticsearchProperties elasticsearchProperties, RestHighLevelClient restHighLevelClient) {
        this.elasticsearchProperties = elasticsearchProperties;
        this.restHighLevelClient = restHighLevelClient;
    }

    public SearchResponse search(String processDefinitionId, SearchSourceBuilder searchSourceBuilder)
            throws IOException {
        SearchRequest request = createSearchRequest(processDefinitionId, searchSourceBuilder);
        int hashCode = request.hashCode();
        logger.debug("search request #{}: indices={}, source={}", hashCode, request.indices(), request.source());
        long start = System.currentTimeMillis();
        SearchResponse searchResponse = restHighLevelClient.search(request, RequestOptions.DEFAULT);
        logger.debug("search request #{} finished in {}ms", hashCode, System.currentTimeMillis() - start);
        return searchResponse;
    }

    private SearchRequest createSearchRequest(String processDefinitionId, SearchSourceBuilder searchSourceBuilder) {
        return Requests.searchRequest(getIndexName(processDefinitionId)).source(searchSourceBuilder);
    }

    private String getIndexName(String processDefinitionId) {
        return elasticsearchProperties.getIndexPrefix() + DigestUtils.sha256Hex(processDefinitionId);
    }
}
