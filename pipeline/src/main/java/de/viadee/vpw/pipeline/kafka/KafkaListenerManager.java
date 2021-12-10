package de.viadee.vpw.pipeline.kafka;

import java.util.Collection;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.kafka.config.KafkaListenerEndpointRegistry;
import org.springframework.kafka.listener.MessageListenerContainer;
import org.springframework.stereotype.Component;

@Component
public class KafkaListenerManager {

    private final Logger logger = LoggerFactory.getLogger(KafkaListenerManager.class);

    private final KafkaListenerEndpointRegistry registry;

    public KafkaListenerManager() {
        this.registry = new KafkaListenerEndpointRegistry();
    }

    public synchronized void pause() {
        getContainers().forEach(container -> {
            logger.warn("Pausing consumption from Kafka partition(s) {}", container.getAssignedPartitions());
            container.pause();
        });
    }

    public synchronized void resume() {
        getContainers().forEach(container -> {
            logger.info("Resuming consumption from Kafka partition(s) {}", container.getAssignedPartitions());
            container.resume();
        });
    }

    public synchronized boolean isActive() {
        Collection<MessageListenerContainer> containers = getContainers();
        logDetails(containers);
        boolean active = containers.stream().noneMatch(MessageListenerContainer::isPauseRequested);
        logger.debug("Kafka listeners active? {}", active);
        return active;
    }

    private void logDetails(Collection<MessageListenerContainer> containers) {
        if (logger.isTraceEnabled()) {
            String message = "Partition(s) {}: Pause requested? {}, Paused? {}";
            containers.forEach(
                    c -> logger.trace(message, c.getAssignedPartitions(), c.isPauseRequested(), c.isContainerPaused()));
        }
    }

    private Collection<MessageListenerContainer> getContainers() {
        return registry.getListenerContainers();
    }
}
