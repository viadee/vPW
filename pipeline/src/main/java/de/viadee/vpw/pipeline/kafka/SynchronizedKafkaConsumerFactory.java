package de.viadee.vpw.pipeline.kafka;

import java.util.Map;

import org.apache.kafka.clients.consumer.Consumer;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;

public class SynchronizedKafkaConsumerFactory<K, V> extends DefaultKafkaConsumerFactory<K, V> {

    public SynchronizedKafkaConsumerFactory(Map<String, Object> configs) {
        super(configs);
    }

    @Override
    public Consumer<K, V> createConsumer() {
        return new SynchronizedKafkaConsumer<>(super.createConsumer());
    }

    @Override
    public Consumer<K, V> createConsumer(String clientIdSuffix) {
        return new SynchronizedKafkaConsumer<>(super.createConsumer(clientIdSuffix));
    }

    @Override
    public Consumer<K, V> createConsumer(String groupId, String clientIdSuffix) {
        return new SynchronizedKafkaConsumer<>(super.createConsumer(groupId, clientIdSuffix));
    }

    @Override
    public Consumer<K, V> createConsumer(String groupId, String clientIdPrefix, String clientIdSuffix) {
        return new SynchronizedKafkaConsumer<>(super.createConsumer(groupId, clientIdPrefix, clientIdSuffix));
    }
}
