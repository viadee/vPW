package de.viadee.vpw.pipeline;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.util.AssertionErrors.*;

import java.util.Date;
import java.util.UUID;

import org.apache.commons.codec.digest.DigestUtils;
import org.elasticsearch.action.index.IndexRequest;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.kafka.config.KafkaListenerEndpointRegistry;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.listener.MessageListenerContainer;
import org.springframework.kafka.test.EmbeddedKafkaBroker;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.kafka.test.utils.ContainerTestUtils;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.fasterxml.jackson.databind.ObjectMapper;

import de.viadee.vpw.pipeline.config.ApplicationConfiguration;
import de.viadee.vpw.pipeline.config.KafkaConfiguration;
import de.viadee.vpw.pipeline.config.KafkaTestConfiguration;
import de.viadee.vpw.pipeline.model.ActivityInstanceEvent;
import de.viadee.vpw.pipeline.model.ProcessInstanceEvent;
import de.viadee.vpw.pipeline.service.elastic.ElasticsearchBulkIndexer;

@ActiveProfiles("test")
@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = { ApplicationConfiguration.class, KafkaConfiguration.class,
        KafkaTestConfiguration.class })
@SpringBootTest(classes = PipelineApplication.class, properties = "spring.main.allow-bean-definition-overriding=true")
@DirtiesContext
@EmbeddedKafka(
        partitions = 1,
        topics = {
                "processInstance",
                "activityInstance",
                "variableUpdate"
        },
        brokerPropertiesLocation = "classpath:/${broker.filename:broker}.properties"
)
public class PipelineApplicationTests {

    @Autowired
    @Qualifier("kafkaProcessInstanceEventTemplate")
    private KafkaTemplate<String, String> kafkaProcessInstanceEventTemplate;

    @Autowired
    @Qualifier("kafkaActivityInstanceEventTemplate")
    private KafkaTemplate<String, String> kafkaActivityInstanceEventTemplate;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ElasticsearchBulkIndexer indexer;

    @Autowired
    private KafkaListenerEndpointRegistry kafkaListenerEndpointRegistry;

    @SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
    @Autowired
    private EmbeddedKafkaBroker kafkaEmbedded;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        doNothing().when(this.indexer).add(any());

        for (MessageListenerContainer messageListenerContainer : this.kafkaListenerEndpointRegistry
                .getListenerContainers()) {
            ContainerTestUtils.waitForAssignment(messageListenerContainer, this.kafkaEmbedded.getPartitionsPerTopic());
        }
    }

    @Test
    public void processInstanceStartEventTest() throws Exception {
        ProcessInstanceEvent processInstanceEvent = new ProcessInstanceEvent();
        processInstanceEvent.setProcessDefinitionId("test-proc-id");
        processInstanceEvent.setProcessInstanceId(UUID.randomUUID().toString());
        processInstanceEvent.setStartTime(new Date());

        this.kafkaProcessInstanceEventTemplate.sendDefault(
                UUID.randomUUID().toString(),
                this.objectMapper.writeValueAsString(processInstanceEvent)
        );
        this.kafkaProcessInstanceEventTemplate.flush();

        // Wait a few seconds, till the message is processed
        Thread.sleep(2500);

        final ArgumentCaptor<IndexRequest> captor = ArgumentCaptor.forClass(IndexRequest.class);
        Mockito.verify(this.indexer, times(1)).add(captor.capture());
        IndexRequest indexRequest = captor.getValue();

        assertEquals("Version check", indexRequest.version(), 1L);
    }

    @Test
    public void processInstanceEndEventTest() throws Exception {
        ProcessInstanceEvent processInstanceEvent = new ProcessInstanceEvent();
        processInstanceEvent.setProcessDefinitionId("test-proc-id");
        processInstanceEvent.setProcessInstanceId(UUID.randomUUID().toString());
        processInstanceEvent.setStartTime(new Date());
        processInstanceEvent.setEndTime(new Date());

        this.kafkaProcessInstanceEventTemplate.sendDefault(
                UUID.randomUUID().toString(),
                this.objectMapper.writeValueAsString(processInstanceEvent)
        );
        this.kafkaProcessInstanceEventTemplate.flush();

        // Wait a few seconds, till the message is processed
        Thread.sleep(2500);

        final ArgumentCaptor<IndexRequest> captor = ArgumentCaptor.forClass(IndexRequest.class);
        Mockito.verify(this.indexer, times(1)).add(captor.capture());
        IndexRequest indexRequest = captor.getValue();

        assertEquals("Version check", indexRequest.version(), 2L);
    }

    @Test
    public void activityInstanceStartEventTest() throws Exception {
        ActivityInstanceEvent event = new ActivityInstanceEvent();
        event.setProcessDefinitionId("test-proc-id");
        event.setProcessInstanceId(UUID.randomUUID().toString());
        event.setActivityInstanceId(UUID.randomUUID().toString());
        event.setActivityId("my-activity");
        event.setActivityType("serviceTask");
        event.setStartTime(new Date());

        this.kafkaActivityInstanceEventTemplate.sendDefault(
                UUID.randomUUID().toString(),
                this.objectMapper.writeValueAsString(event)
        );
        this.kafkaActivityInstanceEventTemplate.flush();

        // Wait a few seconds, till the message is processed
        Thread.sleep(2500);

        final ArgumentCaptor<IndexRequest> captor = ArgumentCaptor.forClass(IndexRequest.class);
        Mockito.verify(this.indexer, times(1)).add(captor.capture());
        IndexRequest indexRequest = captor.getValue();

        assertEquals("Version check", indexRequest.version(), 1L);
        assertEquals("Parent routing", indexRequest.routing(), event.getProcessInstanceId());
    }

    @Test
    public void activityInstanceEndEventTest() throws Exception {
        ActivityInstanceEvent event = new ActivityInstanceEvent();
        event.setProcessDefinitionId("test-proc-id");
        event.setProcessInstanceId(UUID.randomUUID().toString());
        event.setActivityInstanceId(UUID.randomUUID().toString());
        event.setActivityId("my-activity");
        event.setActivityType("serviceTask");
        event.setStartTime(new Date());
        event.setEndTime(new Date());

        this.kafkaActivityInstanceEventTemplate.sendDefault(
                UUID.randomUUID().toString(),
                this.objectMapper.writeValueAsString(event)
        );
        this.kafkaActivityInstanceEventTemplate.flush();

        // Wait a few seconds, till the message is processed
        Thread.sleep(2500);

        final ArgumentCaptor<IndexRequest> captor = ArgumentCaptor.forClass(IndexRequest.class);
        Mockito.verify(this.indexer, times(1)).add(captor.capture());
        IndexRequest indexRequest = captor.getValue();

        assertEquals("Version check", indexRequest.version(), 2L);
        assertEquals("Parent routing", indexRequest.routing(), event.getProcessInstanceId());
    }

    @Test
    public void parallelGatewayInstanceEndEventTest() throws Exception {
        ActivityInstanceEvent event = new ActivityInstanceEvent();
        event.setProcessDefinitionId("test-proc-id");
        event.setProcessInstanceId(UUID.randomUUID().toString());
        event.setActivityInstanceId(UUID.randomUUID().toString());
        event.setActivityId("my-activity");
        event.setActivityType("parallelGateway");
        event.setStartTime(new Date());
        event.setEndTime(new Date());

        this.kafkaActivityInstanceEventTemplate.sendDefault(
                UUID.randomUUID().toString(),
                this.objectMapper.writeValueAsString(event)
        );
        this.kafkaActivityInstanceEventTemplate.flush();

        // Wait a few seconds, till the message is processed
        Thread.sleep(2500);

        final ArgumentCaptor<IndexRequest> captor = ArgumentCaptor.forClass(IndexRequest.class);
        Mockito.verify(this.indexer, times(1)).add(captor.capture());
        IndexRequest indexRequest = captor.getValue();

        assertEquals("Version check", indexRequest.version(), event.getEndTime().getTime());
        assertEquals("Parent routing", indexRequest.routing(), event.getProcessInstanceId());
        assertEquals("Event-Id", indexRequest.id(), DigestUtils.sha256Hex(
                event.getProcessInstanceId() + event.getActivityId()
        ));
    }

}
