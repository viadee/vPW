package de.viadee.vpw.pipeline.listener;

import java.util.Date;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.elasticsearch.action.index.IndexRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

import de.viadee.vpw.pipeline.kafka.KafkaAcknowledgment;
import de.viadee.vpw.pipeline.model.ActivityInstanceEvent;
import de.viadee.vpw.pipeline.service.elastic.ElasticsearchBulkIndexer;
import de.viadee.vpw.pipeline.service.elastic.ElasticsearchRequestBuilder;
import de.viadee.vpw.pipeline.service.json.JsonMapper;

@Component
public class ActivityInstanceEventListener {

    private final Logger logger = LoggerFactory.getLogger(ActivityInstanceEventListener.class);

    private final ElasticsearchRequestBuilder requestBuilder;

    private final ElasticsearchBulkIndexer bulkIndexer;

    private final JsonMapper jsonMapper;

    @Autowired
    public ActivityInstanceEventListener(ElasticsearchRequestBuilder requestBuilder,
            ElasticsearchBulkIndexer bulkIndexer, JsonMapper jsonMapper) {
        this.requestBuilder = requestBuilder;
        this.bulkIndexer = bulkIndexer;
        this.jsonMapper = jsonMapper;
    }

    @KafkaListener(topics = "${vpw.pipeline.kafka.topics.activity-instance}", clientIdPrefix = "${spring.kafka.consumer.client-id}-activity-instance")
    public void listen(ConsumerRecord<String, String> record, Acknowledgment acknowledgment) {
        logger.trace("Received record: {}", record);

        ActivityInstanceEvent event = jsonMapper.fromJson(record.value(), ActivityInstanceEvent.class);

        IdAndVersion idAndVersion = new IdAndVersion(event);
        IndexRequest request = requestBuilder.buildIndexRequest(idAndVersion.id, idAndVersion.version, event,
                new KafkaAcknowledgment(record, acknowledgment));

        bulkIndexer.add(request);
    }

    private class IdAndVersion {

        private String id;

        private long version;

        private IdAndVersion(ActivityInstanceEvent event) {
            id = event.getActivityInstanceId();
            Date endTime = event.getEndTime();

            // End events have higher version number then start events. Prevents overriding of end events from late start events.
            version = endTime == null ? 1L : 2L;

            // Join parallel gateway events. All events share the process-instance-id and the event-type
            // we can use the hash as alternative id. Use the timestamp as version to store only the latest event.
            if (event.getActivityType().equals("parallelGateway")) {

                id = DigestUtils.sha256Hex(event.getProcessInstanceId() + event.getActivityId());

                // Use end timestamp as version number. We only want to store the latest join event.
                if (endTime != null) {
                    version = endTime.getTime();
                }
            }
        }
    }
}
