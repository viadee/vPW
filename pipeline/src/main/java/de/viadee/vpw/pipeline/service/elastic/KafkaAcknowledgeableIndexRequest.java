package de.viadee.vpw.pipeline.service.elastic;

import java.util.Objects;

import org.elasticsearch.action.index.IndexRequest;

import de.viadee.vpw.pipeline.kafka.KafkaAcknowledgment;

public class KafkaAcknowledgeableIndexRequest extends IndexRequest {

    private final KafkaAcknowledgment acknowledgment;

    KafkaAcknowledgeableIndexRequest(String index, String type, String id, KafkaAcknowledgment acknowledgment) {
        super(index, type, id);
        this.acknowledgment = Objects.requireNonNull(acknowledgment, "acknowledgment must not be null");
    }

    KafkaAcknowledgment getAcknowledgment() {
        return acknowledgment;
    }
}
