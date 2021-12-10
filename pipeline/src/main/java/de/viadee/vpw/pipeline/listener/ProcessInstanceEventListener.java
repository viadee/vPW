package de.viadee.vpw.pipeline.listener;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.elasticsearch.action.index.IndexRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

import de.viadee.vpw.pipeline.kafka.KafkaAcknowledgment;
import de.viadee.vpw.pipeline.model.ProcessInstanceEvent;
import de.viadee.vpw.pipeline.service.elastic.ElasticsearchBulkIndexer;
import de.viadee.vpw.pipeline.service.elastic.ElasticsearchRequestBuilder;
import de.viadee.vpw.pipeline.service.json.JsonMapper;

@Component
public class ProcessInstanceEventListener {

    private final Logger logger = LoggerFactory.getLogger(ProcessInstanceEventListener.class);

    private final ElasticsearchRequestBuilder requestBuilder;

    private final ElasticsearchBulkIndexer bulkIndexer;

    private final JsonMapper jsonMapper;

    @Autowired
    public ProcessInstanceEventListener(ElasticsearchRequestBuilder requestBuilder,
            ElasticsearchBulkIndexer bulkIndexer, JsonMapper jsonMapper) {
        this.requestBuilder = requestBuilder;
        this.bulkIndexer = bulkIndexer;
        this.jsonMapper = jsonMapper;
    }

    @KafkaListener(topics = "${vpw.pipeline.kafka.topics.process-instance}", clientIdPrefix = "${spring.kafka.consumer.client-id}-process-instance")
    public void listen(ConsumerRecord<String, String> record, Acknowledgment acknowledgment) {
        logger.trace("Received record: {}", record);
        ProcessInstanceEvent event = jsonMapper.fromJson(record.value(), ProcessInstanceEvent.class);
        IndexRequest request = requestBuilder.buildIndexRequest(event.getProcessInstanceId(), getVersion(event), event,
                new KafkaAcknowledgment(record, acknowledgment));
        bulkIndexer.add(request);
    }

    private int getVersion(ProcessInstanceEvent event) {
        // End events have higher version number then start events. Prevents overriding of end events from late start events.
        return event.getEndTime() == null ? 1 : 2;
    }
}
