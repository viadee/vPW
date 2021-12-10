package de.viadee.vpw.pipeline.config;

import java.util.HashMap;
import java.util.Map;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.test.EmbeddedKafkaBroker;
import org.springframework.kafka.test.utils.KafkaTestUtils;

@Configuration
@EnableKafka
public class KafkaTestConfiguration {

    @Value("${" + EmbeddedKafkaBroker.SPRING_EMBEDDED_KAFKA_BROKERS + "}")
    private String brokerAddresses;

    @Value("${vpw.pipeline.kafka.topics.process-instance}")
    private String processInstanceTopic;

    @Value("${vpw.pipeline.kafka.topics.activity-instance}")
    private String activityInstanceTopic;

    @Value("${vpw.pipeline.kafka.topics.variable-update}")
    private String variableUpdateTopic;

    @Bean
    public ProducerFactory<String, String> producerFactory() {
        return new DefaultKafkaProducerFactory<>(producerConfigs());
    }

    @Bean
    public Map<String, Object> producerConfigs() {
        Map<String, Object> producerConfigs = KafkaTestUtils.producerProps(this.brokerAddresses);
        producerConfigs.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        return producerConfigs;
    }

    @Bean(name = "kafkaProcessInstanceEventTemplate")
    public KafkaTemplate<String, String> kafkaProcessInstanceEventTemplate() {
        KafkaTemplate<String, String> kafkaTemplate = new KafkaTemplate<>(producerFactory(), true);
        kafkaTemplate.setDefaultTopic(processInstanceTopic);
        return kafkaTemplate;
    }

    @Bean(name = "kafkaActivityInstanceEventTemplate")
    public KafkaTemplate<String, String> kafkaActivityInstanceEventTemplate() {
        KafkaTemplate<String, String> kafkaTemplate = new KafkaTemplate<>(producerFactory(), true);
        kafkaTemplate.setDefaultTopic(activityInstanceTopic);
        return kafkaTemplate;
    }

    @Bean(name = "kafkaVariableUpdateEventTemplate")
    public KafkaTemplate<String, String> kafkaVariableUpdateEventTemplate() {
        KafkaTemplate<String, String> kafkaTemplate = new KafkaTemplate<>(producerFactory(), true);
        kafkaTemplate.setDefaultTopic(variableUpdateTopic);
        return kafkaTemplate;
    }

    @Primary
    @Bean
    public ConsumerFactory<String, String> consumerFactory(KafkaProperties kafkaProperties) {
        Map<String, Object> kafkaConsumerConfigs = new HashMap<>();
        kafkaConsumerConfigs.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, this.brokerAddresses);
        kafkaConsumerConfigs.put(ConsumerConfig.GROUP_ID_CONFIG, kafkaProperties.getConsumer().getGroupId());
        kafkaConsumerConfigs.put(ConsumerConfig.CLIENT_ID_CONFIG, kafkaProperties.getConsumer().getClientId());
        kafkaConsumerConfigs.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        kafkaConsumerConfigs.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        kafkaConsumerConfigs.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, false);
        return new DefaultKafkaConsumerFactory<>(kafkaConsumerConfigs);
    }
}
