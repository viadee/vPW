package de.viadee.vpw.pipeline.kafka;

import java.util.Objects;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.common.TopicPartition;
import org.springframework.kafka.support.Acknowledgment;

public class KafkaAcknowledgment {

    private final TopicPartition partition;

    private final long offset;

    private final Acknowledgment acknowledgment;

    public KafkaAcknowledgment(ConsumerRecord<String, String> record, Acknowledgment acknowledgment) {
        Objects.requireNonNull(record, "record must not be null");
        this.partition = new TopicPartition(record.topic(), record.partition());
        this.offset = record.offset();
        this.acknowledgment = Objects.requireNonNull(acknowledgment, "acknowledgment must not be null");
    }

    public TopicPartition getPartition() {
        return partition;
    }

    public long getOffset() {
        return offset;
    }

    public void acknowledge() {
        acknowledgment.acknowledge();
    }
}
