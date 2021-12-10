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
import de.viadee.vpw.pipeline.model.IncidentEvent;
import de.viadee.vpw.pipeline.service.elastic.ElasticsearchBulkIndexer;
import de.viadee.vpw.pipeline.service.elastic.ElasticsearchRequestBuilder;
import de.viadee.vpw.pipeline.service.json.JsonMapper;

@Component
public class IncidentEventListener {

    private final Logger logger = LoggerFactory.getLogger(IncidentEventListener.class);

    private final ElasticsearchRequestBuilder requestBuilder;

    private final ElasticsearchBulkIndexer bulkIndexer;

    private final JsonMapper jsonMapper;

    @Autowired
    public IncidentEventListener(ElasticsearchRequestBuilder requestBuilder, ElasticsearchBulkIndexer bulkIndexer,
            JsonMapper jsonMapper) {
        this.requestBuilder = requestBuilder;
        this.bulkIndexer = bulkIndexer;
        this.jsonMapper = jsonMapper;
    }

    @KafkaListener(topics = "${vpw.pipeline.kafka.topics.incident}", clientIdPrefix = "${spring.kafka.consumer.client-id}-incident")
    public void listen(ConsumerRecord<String, String> record, Acknowledgment acknowledgment) {
        logger.trace("Received record: {}", record);
        IncidentEvent event = jsonMapper.fromJson(record.value(), IncidentEvent.class);
        IndexRequest request = requestBuilder
                .buildIndexRequest(event.getCauseIncidentId(), event, new KafkaAcknowledgment(record, acknowledgment));
        bulkIndexer.add(request);
    }
}
