package de.viadee.vpw.pipeline.config;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.retry.annotation.EnableRetry;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.ObjectMapper;

import de.viadee.vpw.pipeline.config.properties.ApplicationProperties;
import de.viadee.vpw.pipeline.config.properties.PipelineElasticsearchProperties;

@Configuration
@EnableConfigurationProperties({ ApplicationProperties.class, PipelineElasticsearchProperties.class })
@EnableRetry
public class ApplicationConfiguration {

    @Bean
    public ObjectMapper objectMapper() {
        return new ObjectMapper();
    }

    @Bean
    public RestTemplate restTemplate(RestTemplateBuilder builder) {
        return builder.build();
    }
}
