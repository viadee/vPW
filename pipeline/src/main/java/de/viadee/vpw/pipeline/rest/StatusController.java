package de.viadee.vpw.pipeline.rest;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.apache.kafka.clients.admin.AdminClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import de.viadee.vpw.pipeline.kafka.KafkaListenerManager;

/**
 * REST-Endpunkte für Kubernetes-Readiness-/Liveness-Probes.
 */
@RestController
public class StatusController {

    private static final String TRUE = Boolean.TRUE.toString();

    private static final String FALSE = Boolean.FALSE.toString();

    private final Logger logger = LoggerFactory.getLogger(StatusController.class);

    private final KafkaListenerManager kafkaListenerManager;

    private final AdminClient adminClient;

    public StatusController(KafkaListenerManager kafkaListenerManager, AdminClient adminClient) {
        this.kafkaListenerManager = kafkaListenerManager;
        this.adminClient = adminClient;
    }

    /**
     * Liefert immer Status 200 "OK" (sobald die Anwendung läuft, soll für Kubernetes "ready" gemeldet werden).
     *
     * @return {@link HttpStatus#OK}
     */
    @GetMapping(path = "status/ready", produces = MediaType.TEXT_PLAIN_VALUE)
    public ResponseEntity<String> isReady() {
        logger.debug("Status ready? {}", TRUE);
        return ResponseEntity.ok(TRUE);
    }

    /**
     * Prüft, ob der Kafka-Broker erreichbar ist und die Kafka-Listener aktiv sind.
     *
     * @return {@link HttpStatus#OK}, wenn aktiv; sonst {@link HttpStatus#INTERNAL_SERVER_ERROR}
     */
    @GetMapping(path = "status/alive", produces = MediaType.TEXT_PLAIN_VALUE)
    public ResponseEntity<String> isAlive() {
        boolean alive = isBrokerAvailable() && kafkaListenerManager.isActive();
        String message = "Status alive? {}";
        if (alive) {
            logger.debug(message, TRUE);
            return ResponseEntity.ok(TRUE);
        } else {
            logger.warn(message, FALSE);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(FALSE);
        }
    }

    private boolean isBrokerAvailable() {
        String message = "Kafka broker available? {}";
        try {
            // Workaround, um die Verbindung zu Kafka zu prüfen
            adminClient.listTopics().names().get(5, TimeUnit.SECONDS);
            logger.debug(message, TRUE);
            return true;
        } catch (InterruptedException | ExecutionException | TimeoutException e) {
            logger.warn(message, FALSE);
            return false;
        }
    }
}
