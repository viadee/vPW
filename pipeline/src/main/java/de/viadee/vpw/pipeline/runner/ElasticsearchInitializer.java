package de.viadee.vpw.pipeline.runner;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;

import org.elasticsearch.action.admin.indices.template.put.PutIndexTemplateRequest;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.client.indices.IndexTemplatesExistRequest;
import org.elasticsearch.xcontent.XContentType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import de.viadee.vpw.pipeline.config.properties.PipelineElasticsearchProperties;
import de.viadee.vpw.shared.config.elasticsearch.ElasticsearchProperties;

@Profile("!test")
@Component
public class ElasticsearchInitializer implements ApplicationRunner {

    private static final Logger LOGGER = LoggerFactory.getLogger(ElasticsearchInitializer.class);

    private static final String INDEX_TEMPLATE_NAME = "vpw";

    private final PipelineElasticsearchProperties pipelineElasticsearchProperties;

    private final RestHighLevelClient elasticsearchClient;

    private final ObjectMapper objectMapper;

    private final String indexNamePattern;

    @Autowired
    public ElasticsearchInitializer(ElasticsearchProperties elasticsearchProperties,
            PipelineElasticsearchProperties pipelineElasticsearchProperties, RestHighLevelClient elasticsearchClient,
            ObjectMapper objectMapper) {
        this.pipelineElasticsearchProperties = pipelineElasticsearchProperties;
        this.elasticsearchClient = elasticsearchClient;
        this.objectMapper = objectMapper;
        indexNamePattern = elasticsearchProperties.getIndexPrefix() + "*";
    }

    @Override
    public void run(ApplicationArguments args) throws Exception {
        if (!indexTemplateExists()) {
            createIndexTemplate();
        }
    }

    private void createIndexTemplate() throws IOException {
        LOGGER.info("Creating Elasticsearch index template '{}'", INDEX_TEMPLATE_NAME);
        PutIndexTemplateRequest request = new PutIndexTemplateRequest(INDEX_TEMPLATE_NAME);
        request.source(createIndexTemplateJson(), XContentType.JSON);
        elasticsearchClient.indices().putTemplate(request, RequestOptions.DEFAULT);
    }

    private boolean indexTemplateExists() throws IOException {
        IndexTemplatesExistRequest request = new IndexTemplatesExistRequest(INDEX_TEMPLATE_NAME);
        return elasticsearchClient.indices().existsTemplate(request, RequestOptions.DEFAULT);
    }

    private String createIndexTemplateJson() throws IOException {
        JsonNode template = readIndexTemplateFile();
        ArrayNode indexPatterns = (ArrayNode) template.get("index_patterns");
        indexPatterns.add(indexNamePattern);
        ObjectNode settings = (ObjectNode) template.get("settings");
        settings.put("number_of_shards", pipelineElasticsearchProperties.getNumberOfShards());
        settings.put("number_of_replicas", pipelineElasticsearchProperties.getNumberOfReplicas());
        return template.toString();
    }

    private JsonNode readIndexTemplateFile() throws IOException {
        ClassPathResource file = new ClassPathResource("/elasticsearch/index-template.json");
        Reader reader = new BufferedReader(new InputStreamReader(file.getInputStream(), StandardCharsets.UTF_8));
        return objectMapper.readValue(reader, JsonNode.class);
    }
}
