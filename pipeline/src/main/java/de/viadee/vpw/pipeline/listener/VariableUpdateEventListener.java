package de.viadee.vpw.pipeline.listener;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.List;
import java.util.Map;
import java.util.stream.IntStream;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.elasticsearch.action.index.IndexRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

import de.viadee.vpw.pipeline.kafka.KafkaAcknowledgment;
import de.viadee.vpw.pipeline.model.VariableUpdateEvent;
import de.viadee.vpw.pipeline.service.elastic.ElasticsearchBulkIndexer;
import de.viadee.vpw.pipeline.service.elastic.ElasticsearchRequestBuilder;
import de.viadee.vpw.pipeline.service.json.JsonMapper;

@Component
public class VariableUpdateEventListener {

    private final Logger logger = LoggerFactory.getLogger(VariableUpdateEventListener.class);

    private final ElasticsearchRequestBuilder requestBuilder;

    private final ElasticsearchBulkIndexer bulkIndexer;

    private final JsonMapper jsonMapper;

    @Autowired
    public VariableUpdateEventListener(ElasticsearchRequestBuilder requestBuilder, ElasticsearchBulkIndexer bulkIndexer,
            JsonMapper jsonMapper) {
        this.requestBuilder = requestBuilder;
        this.bulkIndexer = bulkIndexer;
        this.jsonMapper = jsonMapper;
    }

    @KafkaListener(topics = "${vpw.pipeline.kafka.topics.variable-update}", clientIdPrefix = "${spring.kafka.consumer.client-id}-variable-update")
    public void listen(ConsumerRecord<String, String> record, Acknowledgment acknowledgment) {
        logger.trace("Received record: {}", record);
        RecordHandler handler = new RecordHandler(record, acknowledgment);
        handler.handleRecord();
    }

    private class RecordHandler {

        private final ConsumerRecord<String, String> record;

        private final Acknowledgment acknowledgment;

        private final Deque<VariableUpdateEvent> events = new ArrayDeque<>();

        private VariableUpdateEvent current;

        private RecordHandler(ConsumerRecord<String, String> record, Acknowledgment acknowledgment) {
            this.record = record;
            this.acknowledgment = acknowledgment;
        }

        private void handleRecord() {
            VariableUpdateEvent event = jsonMapper.fromJson(record.value(), VariableUpdateEvent.class);
            handleEvent(event);
        }

        private void handleEvent(VariableUpdateEvent event) {
            events.add(event);
            while (!events.isEmpty()) {
                current = events.removeFirst();
                Object complexValue = current.getComplexValue();
                if (complexValue != null) {
                    handleComplexValue(complexValue);
                } else {
                    createIndexRequest(current, events.isEmpty());
                }
            }
        }

        private void handleComplexValue(Object complexValue) {
            if (complexValue instanceof Map) {
                Map<?, ?> map = (Map<?, ?>) complexValue;
                map.forEach((key, value) -> events.add(createMapEntrySubEvent(key.toString(), value)));
            } else if (complexValue instanceof List) {
                List<?> list = (List<?>) complexValue;
                IntStream.range(0, list.size()).forEach(i -> events.add(createListEntrySubEvent(i, list.get(i))));
            } else {
                logger.warn("Ignored event with complex value type {}", complexValue.getClass());
            }
        }

        private VariableUpdateEvent createMapEntrySubEvent(String key, Object value) {
            VariableUpdateEvent subEvent = createSubEvent(value);
            subEvent.setId(DigestUtils.sha256Hex(current.getId() + key));
            subEvent.setVariableName(current.getVariableName() + "." + key);
            return subEvent;
        }

        private VariableUpdateEvent createListEntrySubEvent(int index, Object value) {
            VariableUpdateEvent subEvent = createSubEvent(value);
            subEvent.setId(current.getId() + "-" + index);
            return subEvent;
        }

        private VariableUpdateEvent createSubEvent(Object value) {
            VariableUpdateEvent subEvent = new VariableUpdateEvent();
            BeanUtils.copyProperties(current, subEvent);
            subEvent.setComplexValue(null);
            if (value instanceof Map || value instanceof List) {
                subEvent.setComplexValue(value);
            } else if (value instanceof Integer) {
                subEvent.setLongValue(((Integer) value).longValue());
            } else if (value instanceof Long) {
                subEvent.setLongValue((Long) value);
            } else if (value instanceof Double) {
                subEvent.setDoubleValue((Double) value);
            } else if (value instanceof String) {
                subEvent.setTextValue((String) value);
            }
            return subEvent;
        }

        private void createIndexRequest(VariableUpdateEvent event, boolean acknowledge) {
            String id = event.getId();
            int version = event.getRevision() + 1; // revision starts at 0, but version must be > 0
            IndexRequest request = acknowledge ?
                    requestBuilder
                            .buildIndexRequest(id, version, event, new KafkaAcknowledgment(record, acknowledgment)) :
                    requestBuilder.buildIndexRequest(id, version, event);
            bulkIndexer.add(request);
        }
    }
}
