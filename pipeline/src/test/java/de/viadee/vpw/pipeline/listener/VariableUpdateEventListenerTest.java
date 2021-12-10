package de.viadee.vpw.pipeline.listener;


import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.elasticsearch.action.index.IndexRequest;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;


import com.fasterxml.jackson.databind.ObjectMapper;

import de.viadee.vpw.pipeline.model.JoinModel;
import de.viadee.vpw.pipeline.model.VariableUpdateEvent;
import de.viadee.vpw.pipeline.service.elastic.ElasticsearchBulkIndexer;
import de.viadee.vpw.pipeline.service.elastic.ElasticsearchRequestBuilder;
import de.viadee.vpw.pipeline.service.elastic.KafkaAcknowledgeableIndexRequest;
import de.viadee.vpw.pipeline.service.json.JsonMapper;
import de.viadee.vpw.shared.config.elasticsearch.ElasticsearchProperties;

@ActiveProfiles("test")
@ExtendWith(SpringExtension.class)
public class VariableUpdateEventListenerTest {

    private static final String PROPERTY_FIRST_NAME = "firstName";

    private static final String PROPERTY_LAST_NAME = "lastName";

    private static final String PROPERTY_INT_VALUE = "intValue";

    private static final String PROPERTY_LONG_VALUE = "longValue";

    private static final String PROPERTY_DOUBLE_VALUE = "doubleValue";

    @MockBean
    private ElasticsearchBulkIndexer bulkIndexer;

    @Autowired
    private JsonMapper jsonMapper;

    @Autowired
    private VariableUpdateEventListener variableUpdateEventListener;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        doNothing().when(bulkIndexer).add(any());
    }

    @Test
    public void testHandleTextValue() {
        de.viadee.camunda.kafka.event.VariableUpdateEvent event = createVariableUpdateEvent("test");
        event.setTextValue("test");
        variableUpdateEventListener.listen(createConsumerRecord(event), mock(Acknowledgment.class));
        VariableUpdateEvent capturedEvent = getCapturedEvent();
        validateEventProperties(event, capturedEvent);
        validateIdNameValue(event.getId(), event.getVariableName(), event.getTextValue(), capturedEvent);
    }

    @Test
    public void testHandleLongValue() {
        de.viadee.camunda.kafka.event.VariableUpdateEvent event = createVariableUpdateEvent("test");
        event.setLongValue(123L);
        variableUpdateEventListener.listen(createConsumerRecord(event), mock(Acknowledgment.class));
        VariableUpdateEvent capturedEvent = getCapturedEvent();
        validateEventProperties(event, capturedEvent);
        validateIdNameValue(event.getId(), event.getVariableName(), event.getLongValue(), capturedEvent);
    }

    @Test
    public void testHandleDoubleValue() {
        de.viadee.camunda.kafka.event.VariableUpdateEvent event = createVariableUpdateEvent("test");
        event.setDoubleValue(123.456);
        variableUpdateEventListener.listen(createConsumerRecord(event), mock(Acknowledgment.class));
        VariableUpdateEvent capturedEvent = getCapturedEvent();
        validateEventProperties(event, capturedEvent);
        validateIdNameValue(event.getId(), event.getVariableName(), event.getDoubleValue(), capturedEvent);
    }

    @Test
    public void testHandleComplexValue_map_numbers() {
        String variableName = "numbers";
        int intValue = 123;
        long longValue = System.currentTimeMillis();
        double doubleValue = 456.789;

        de.viadee.camunda.kafka.event.VariableUpdateEvent event = createVariableUpdateEvent(variableName,
                new NumberValues(intValue, longValue, doubleValue));

        variableUpdateEventListener.listen(createConsumerRecord(event), mock(Acknowledgment.class));

        List<VariableUpdateEvent> capturedEvents = getCapturedEvents(3);
        capturedEvents.forEach(e -> validateEventProperties(event, e));

        validateIdNameValue(
                DigestUtils.sha256Hex(event.getId() + PROPERTY_INT_VALUE),
                variableName + "." + PROPERTY_INT_VALUE,
                intValue,
                capturedEvents.get(0));
        validateIdNameValue(
                DigestUtils.sha256Hex(event.getId() + PROPERTY_LONG_VALUE),
                variableName + "." + PROPERTY_LONG_VALUE,
                longValue,
                capturedEvents.get(1));
        validateIdNameValue(
                DigestUtils.sha256Hex(event.getId() + PROPERTY_DOUBLE_VALUE),
                variableName + "." + PROPERTY_DOUBLE_VALUE,
                doubleValue,
                capturedEvents.get(2));
    }

    @Test
    public void testHandleComplexValue_map_strings() {

        String variableName = "customer";
        String firstName = "Markus";
        String lastName = "Mustermann";
        de.viadee.camunda.kafka.event.VariableUpdateEvent event = createVariableUpdateEvent(variableName,
                new Person(firstName, lastName));

        variableUpdateEventListener.listen(createConsumerRecord(event), mock(Acknowledgment.class));

        List<VariableUpdateEvent> capturedEvents = getCapturedEvents(2);
        capturedEvents.forEach(e -> validateEventProperties(event, e));

        validateIdNameValue(
                DigestUtils.sha256Hex(event.getId() + PROPERTY_FIRST_NAME),
                variableName + "." + PROPERTY_FIRST_NAME,
                firstName,
                capturedEvents.get(0));
        validateIdNameValue(
                DigestUtils.sha256Hex(event.getId() + PROPERTY_LAST_NAME),
                variableName + "." + PROPERTY_LAST_NAME,
                lastName,
                capturedEvents.get(1));
    }

    @Test
    public void testHandleComplexValue_nestedMap() {

        String variableName = "customer";
        String firstName = "Markus";
        String lastName = "Mustermann";
        String street = "Musterweg 1";
        String postalCode = "12345";
        String city = "Musterstadt";
        de.viadee.camunda.kafka.event.VariableUpdateEvent event = createVariableUpdateEvent(variableName,
                new PersonWithAddress(firstName, lastName, new Address(street, postalCode, city)));

        variableUpdateEventListener.listen(createConsumerRecord(event), mock(Acknowledgment.class));

        List<VariableUpdateEvent> capturedEvents = getCapturedEvents(5);
        capturedEvents.forEach(e -> validateEventProperties(event, e));

        validateIdNameValue(
                DigestUtils.sha256Hex(event.getId() + PROPERTY_FIRST_NAME),
                variableName + "." + PROPERTY_FIRST_NAME,
                firstName,
                capturedEvents.get(0));
        validateIdNameValue(
                DigestUtils.sha256Hex(event.getId() + PROPERTY_LAST_NAME),
                variableName + "." + PROPERTY_LAST_NAME,
                lastName,
                capturedEvents.get(1));
        validateIdNameValue(
                DigestUtils.sha256Hex(DigestUtils.sha256Hex(event.getId() + "address") + "street"),
                variableName + ".address.street",
                street,
                capturedEvents.get(2));
        validateIdNameValue(
                DigestUtils.sha256Hex(DigestUtils.sha256Hex(event.getId() + "address") + "postalCode"),
                variableName + ".address.postalCode",
                postalCode,
                capturedEvents.get(3));
        validateIdNameValue(
                DigestUtils.sha256Hex(DigestUtils.sha256Hex(event.getId() + "address") + "city"),
                variableName + ".address.city",
                city,
                capturedEvents.get(4));
    }

    @Test
    public void testHandleComplexValue_list() {

        String variableName = "names";
        List<String> names = Arrays.asList("Müller", "Meier", "Schmidt");
        de.viadee.camunda.kafka.event.VariableUpdateEvent event = createVariableUpdateEvent(variableName, names);

        variableUpdateEventListener.listen(createConsumerRecord(event), mock(Acknowledgment.class));

        List<VariableUpdateEvent> capturedEvents = getCapturedEvents(3);
        capturedEvents.forEach(e -> validateEventProperties(event, e));
        IntStream.range(0, capturedEvents.size()).forEach(i -> validateIdNameValue(
                event.getId() + "-" + i,
                variableName,
                names.get(i),
                capturedEvents.get(i))
        );
    }

    @Test
    public void testHandleComplexValue_listOfMaps() {
        List<Person> names = Arrays.asList(new Person("Markus", "Mustermann"), new Person("Maria", "Musterfrau"));

        de.viadee.camunda.kafka.event.VariableUpdateEvent event = createVariableUpdateEvent("customers", names);
        variableUpdateEventListener.listen(createConsumerRecord(event), mock(Acknowledgment.class));

        List<VariableUpdateEvent> events = getCapturedEvents(4);

        // TODO
    }

    private de.viadee.camunda.kafka.event.VariableUpdateEvent createVariableUpdateEvent(String variableName,
            Object complexValue) {
        de.viadee.camunda.kafka.event.VariableUpdateEvent event = createVariableUpdateEvent(variableName);
        event.setComplexValue(complexValue);
        return event;
    }

    private de.viadee.camunda.kafka.event.VariableUpdateEvent createVariableUpdateEvent(String variableName) {
        de.viadee.camunda.kafka.event.VariableUpdateEvent event = new de.viadee.camunda.kafka.event.VariableUpdateEvent();
        event.setProcessDefinitionId("test-process:1");
        event.setProcessDefinitionKey("test-process");
        event.setId(UUID.randomUUID().toString());
        event.setExecutionId(UUID.randomUUID().toString());
        event.setProcessInstanceId(UUID.randomUUID().toString());
        event.setVariableInstanceId(UUID.randomUUID().toString());
        event.setActivityInstanceId(UUID.randomUUID().toString());
        event.setTaskId(UUID.randomUUID().toString());
        event.setUserOperationId(UUID.randomUUID().toString());
        event.setVariableName(variableName);
        event.setRevision(1);
        event.setSerializerName("spin://application/json");
        event.setTimestamp(new Date());
        event.setTenantId("tenant-1");
        return event;
    }

    private ConsumerRecord<String, String> createConsumerRecord(
            de.viadee.camunda.kafka.event.VariableUpdateEvent event) {
        return new ConsumerRecord<>("variableUpdate", 0, 0, UUID.randomUUID().toString(), jsonMapper.toJson(event));
    }

    private VariableUpdateEvent getCapturedEvent() {
        return getCapturedEvents(1).get(0);
    }

    private List<VariableUpdateEvent> getCapturedEvents(int expectedNrOfEvents) {
        ArgumentCaptor<IndexRequest> captor = ArgumentCaptor.forClass(IndexRequest.class);
        verify(bulkIndexer, times(expectedNrOfEvents)).add(captor.capture());
        List<IndexRequest> requests = captor.getAllValues();
        assertThatOnlyLastRequestIsAcknowledgeable(requests);
        return requests.stream().map(this::convertToEvent).collect(Collectors.toList());
    }

    private void assertThatOnlyLastRequestIsAcknowledgeable(List<IndexRequest> requests) {
        int last = requests.size() - 1;
        IntStream.range(0, last).forEach(i -> assertFalse(requests.get(i) instanceof KafkaAcknowledgeableIndexRequest));
        assertTrue(requests.get(last) instanceof KafkaAcknowledgeableIndexRequest);
    }

    private VariableUpdateEvent convertToEvent(IndexRequest request) {
        return jsonMapper.fromJson(request.source().utf8ToString(), VariableUpdateEvent.class);
    }

    private void validateIdNameValue(String expectedId, String expectedVariableName, String expectedTextValue,
            VariableUpdateEvent actualEvent) {
        assertEquals(expectedId, actualEvent.getId());
        assertEquals(expectedVariableName, actualEvent.getVariableName());
        assertEquals(expectedTextValue, actualEvent.getTextValue());
        assertNull(actualEvent.getComplexValue());
        assertNull(actualEvent.getLongValue());
        assertNull(actualEvent.getDoubleValue());
    }

    private void validateIdNameValue(String expectedId, String expectedVariableName, long expectedLongValue,
            VariableUpdateEvent actualEvent) {
        assertEquals(expectedId, actualEvent.getId());
        assertEquals(expectedVariableName, actualEvent.getVariableName());
        assertEquals(expectedLongValue, actualEvent.getLongValue().longValue());
        assertNull(actualEvent.getComplexValue());
        assertNull(actualEvent.getTextValue());
        assertNull(actualEvent.getDoubleValue());
    }

    private void validateIdNameValue(String expectedId, String expectedVariableName, double expectedDoubleValue,
            VariableUpdateEvent actualEvent) {
        assertEquals(expectedId, actualEvent.getId());
        assertEquals(expectedVariableName, actualEvent.getVariableName());
        assertEquals(expectedDoubleValue, actualEvent.getDoubleValue(), 0);
        assertNull(actualEvent.getComplexValue());
        assertNull(actualEvent.getTextValue());
        assertNull(actualEvent.getLongValue());
    }

    private void validateEventProperties(de.viadee.camunda.kafka.event.VariableUpdateEvent expected,
            VariableUpdateEvent actual) {

        // Elastic
        assertEquals("variable", actual.getType());
        JoinModel joinModel = actual.getParentJoin();
        assertEquals("variable", joinModel.getName());
        assertEquals(expected.getActivityInstanceId(), joinModel.getParent());

        // aus VariableUpdateEvent
        assertEquals(expected.getRevision(), actual.getRevision());
        assertEquals(expected.getVariableInstanceId(), actual.getVariableInstanceId());
        assertEquals(expected.getSerializerName(), actual.getSerializerName());

        // aus DetailEvent
        assertEquals(expected.getActivityInstanceId(), actual.getActivityInstanceId());
        assertEquals(expected.getTaskId(), actual.getTaskId());
        assertEquals(expected.getTimestamp(), actual.getTimestamp());
        assertEquals(expected.getTenantId(), actual.getTenantId());
        assertEquals(expected.getUserOperationId(), actual.getUserOperationId());

        // aus HistoryEvent
        assertEquals(expected.getProcessInstanceId(), actual.getProcessInstanceId());
        assertEquals(expected.getExecutionId(), actual.getExecutionId());
        assertEquals(expected.getProcessDefinitionId(), actual.getProcessDefinitionId());
        assertEquals(expected.getProcessDefinitionKey(), actual.getProcessDefinitionKey());
    }

    @TestConfiguration
    static class TestConfig {

        @Bean
        public JsonMapper jsonMapper() {
            return new JsonMapper(new ObjectMapper());
        }

        @Bean
        public ElasticsearchProperties elasticsearchProperties() {
            ElasticsearchProperties properties = new ElasticsearchProperties();
            properties.setIndexPrefix("test");
            properties.setMappingType("doc");
            return properties;
        }

        @Bean
        public ElasticsearchRequestBuilder elasticsearchRequestBuilder(@Qualifier("elasticsearchProperties") ElasticsearchProperties properties, JsonMapper jsonMapper) {
            return new ElasticsearchRequestBuilder(properties, jsonMapper);
        }

        @Bean
        public VariableUpdateEventListener variableUpdateEventListener(ElasticsearchRequestBuilder builder,
                ElasticsearchBulkIndexer indexer, JsonMapper jsonMapper) {
            return new VariableUpdateEventListener(builder, indexer, jsonMapper);
        }
    }

    @SuppressWarnings("unused")
    private class Person {

        private final String firstName;

        private final String lastName;

        Person(String firstName, String lastName) {
            this.firstName = firstName;
            this.lastName = lastName;
        }

        public String getFirstName() {
            return firstName;
        }

        public String getLastName() {
            return lastName;
        }
    }

    @SuppressWarnings("unused")
    private class PersonWithAddress extends Person {

        private final Address address;

        PersonWithAddress(String firstName, String lastName, Address address) {
            super(firstName, lastName);
            this.address = address;
        }

        public Address getAddress() {
            return address;
        }
    }

    @SuppressWarnings("unused")
    private class Address {

        private final String street;

        private final String postalCode;

        private final String city;

        Address(String street, String postalCode, String city) {
            this.street = street;
            this.postalCode = postalCode;
            this.city = city;
        }

        public String getStreet() {
            return street;
        }

        public String getPostalCode() {
            return postalCode;
        }

        public String getCity() {
            return city;
        }
    }

    @SuppressWarnings("unused")
    private class NumberValues {

        private final int intValue;

        private final long longValue;

        private final double doubleValue;

        private NumberValues(int intValue, long longValue, double doubleValue) {
            this.intValue = intValue;
            this.longValue = longValue;
            this.doubleValue = doubleValue;
        }

        public int getIntValue() {
            return intValue;
        }

        public long getLongValue() {
            return longValue;
        }

        public double getDoubleValue() {
            return doubleValue;
        }
    }
}
