package de.viadee.vpw.shared.config.elasticsearch;

import org.elasticsearch.client.RestClientBuilder;
import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.elasticsearch.ElasticsearchRestClientAutoConfiguration;
import org.springframework.boot.autoconfigure.elasticsearch.RestClientBuilderCustomizer;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration of the {@link RestClientBuilder}. The builder is used in the Spring {@link ElasticsearchRestClientAutoConfiguration}
 * to create a {@link RestHighLevelClient}, which in turn is required by Analyzer and Pipeline.
 */
@Configuration
@EnableConfigurationProperties({ElasticsearchProperties.class, })
public class ElasticsearchAutoConfiguration {

    @Bean
    public RestClientBuilderCustomizer restClientBuilderCustomizer(@Qualifier("vpw.elasticsearch-de.viadee.vpw.shared.config.elasticsearch.ElasticsearchProperties") ElasticsearchProperties properties) {
        RestClientBuilder.RequestConfigCallback callback = builder -> builder
                .setConnectTimeout(properties.getConnectionTimeoutMillis())
                .setSocketTimeout(properties.getSocketTimeoutMillis());
        return builder -> builder
                .setRequestConfigCallback(callback);
        // TODO retry timeout checken
    }
}
