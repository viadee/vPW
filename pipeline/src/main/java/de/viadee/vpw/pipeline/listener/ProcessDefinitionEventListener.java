package de.viadee.vpw.pipeline.listener;

import de.viadee.vpw.pipeline.PipelineApplication;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Recover;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.JsonNode;

import de.viadee.camunda.kafka.event.ProcessDefinitionEvent;
import de.viadee.vpw.pipeline.config.properties.ApplicationProperties;
import de.viadee.vpw.pipeline.service.json.JsonMapper;

@Component
public class ProcessDefinitionEventListener {

    private final Logger logger = LoggerFactory.getLogger(ProcessDefinitionEventListener.class);

    private final JsonMapper jsonMapper;

    private final RestTemplate restTemplate;

    private final ApplicationProperties properties;

    @Autowired
    ProcessDefinitionEventListener selfReference;

    @Autowired
    public ProcessDefinitionEventListener(JsonMapper jsonMapper, RestTemplate restTemplate,
            ApplicationProperties properties) {
        this.jsonMapper = jsonMapper;
        this.restTemplate = restTemplate;
        this.properties = properties;
    }

    @KafkaListener(topics = "${vpw.pipeline.kafka.topics.process-definition}", clientIdPrefix = "${spring.kafka.consumer.client-id}-process-definition")
    public void listen(ConsumerRecord<String, String> record, Acknowledgment acknowledgment) {
        logger.trace("Received record: {}", record);
        ProcessDefinitionEvent event = jsonMapper.fromJson(record.value(), ProcessDefinitionEvent.class);
        this.selfReference.importProcessDefinition(event, acknowledgment);
    }

    @Retryable(recover = "shutdown", value = RestClientException.class,
            maxAttempts = 12, backoff = @Backoff(delay = 5000))
    public void importProcessDefinition(ProcessDefinitionEvent event, Acknowledgment acknowledgment) {

        String processDefinitionId = event.getId();
        logger.info("Importing process definition '{}'", processDefinitionId);
            HttpStatus status = postProcessDefinition(event);
            if (status.is2xxSuccessful()) {
                logger.info("Import of process definition '{}' finished successfully", processDefinitionId);
                acknowledgment.acknowledge();
            } else {
                logger.warn("Import of process definition '{}' returned status {}", processDefinitionId, status);
            }
    }

    @Recover
    public void shutdown(RestClientException e) {
        logger.error("Shutdown Application caused by", e);
        PipelineApplication.exitApplication(PipelineApplication.getCtx());
    }

    private HttpStatus postProcessDefinition(ProcessDefinitionEvent event) {
        return restTemplate.postForEntity(properties.getProcessDefinitionRestUrl(), event, JsonNode.class)
                .getStatusCode();
    }
}
