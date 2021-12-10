package de.viadee.vpw.shared.config.elasticsearch;

import org.elasticsearch.client.RestHighLevelClient;

import org.junit.jupiter.api.Test;

import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.elasticsearch.RestClientBuilderCustomizer;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@ActiveProfiles("Test")
@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = ElasticsearchAutoConfiguration.class)
@SpringBootApplication
public class ElasticsearchAutoConfigurationTest {

    @Autowired
    private RestClientBuilderCustomizer restClientBuilderCustomizer;

    @Autowired
    private RestHighLevelClient client;

    @Test
    public void test() {
        assertNotNull(restClientBuilderCustomizer);
        assertNotNull(client);
    }
}
