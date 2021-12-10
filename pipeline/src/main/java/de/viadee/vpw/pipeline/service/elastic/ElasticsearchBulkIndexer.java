package de.viadee.vpw.pipeline.service.elastic;

import java.util.Collection;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.BiConsumer;
import java.util.stream.Collectors;

import javax.annotation.PreDestroy;

import org.elasticsearch.action.ActionListener;
import org.elasticsearch.action.DocWriteRequest;
import org.elasticsearch.action.bulk.BackoffPolicy;
import org.elasticsearch.action.bulk.BulkItemResponse;
import org.elasticsearch.action.bulk.BulkProcessor;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.core.TimeValue;
import org.elasticsearch.rest.RestStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import de.viadee.vpw.pipeline.config.properties.PipelineElasticsearchProperties;
import de.viadee.vpw.pipeline.kafka.KafkaAcknowledgment;
import de.viadee.vpw.pipeline.kafka.KafkaListenerManager;

@Service
public class ElasticsearchBulkIndexer {

    private static final Logger logger = LoggerFactory.getLogger(ElasticsearchBulkIndexer.class);

    private final ExecutorService executorService = Executors.newCachedThreadPool();

    private final RestHighLevelClient elasticsearchClient;

    private final KafkaListenerManager kafkaListenerManager;

    private final BulkProcessor bulkProcessor;

    @Autowired
    public ElasticsearchBulkIndexer(PipelineElasticsearchProperties properties, RestHighLevelClient elasticsearchClient,
            KafkaListenerManager kafkaListenerManager) {
        this.elasticsearchClient = elasticsearchClient;
        this.kafkaListenerManager = kafkaListenerManager;
        bulkProcessor = buildBulkProcessor(properties);
    }

    @PreDestroy
    public void shutdown() {
        bulkProcessor.close();
        executorService.shutdown();
    }

    /**
     * Adds an index request to the bulk processor.
     *
     * @param request The request to send to Elasticsearch
     */
    public void add(IndexRequest request) {
        if (logger.isTraceEnabled()) {
            logger.trace("Add request: index='{}', id='{}', version={}, source={}", request.index(), request.id(),
                    request.version(), request.source().utf8ToString());
        }
        bulkProcessor.add(request);
    }

    private BulkProcessor buildBulkProcessor(PipelineElasticsearchProperties properties) {
        return BulkProcessor.builder(createConsumer(), createListener())
                .setConcurrentRequests(0)
                .setBulkActions(properties.getBulkActions())
                .setFlushInterval(TimeValue.timeValueMillis(properties.getFlushInterval()))
                .setBackoffPolicy(BackoffPolicy.exponentialBackoff())
                .build();
    }

    private BiConsumer<BulkRequest, ActionListener<BulkResponse>> createConsumer() {
        return (request, listener) -> elasticsearchClient.bulkAsync(request, RequestOptions.DEFAULT, listener);
    }

    private BulkProcessor.Listener createListener() {
        return new BulkProcessor.Listener() {

            @Override
            public void beforeBulk(long executionId, BulkRequest request) {
                logger.debug("Executing bulk request {} with {} actions", executionId, request.numberOfActions());
            }

            @Override
            public void afterBulk(long executionId, BulkRequest request, BulkResponse response) {
                logger.debug("Bulk request {} with {} actions finished successfully in {} ms", executionId,
                        request.numberOfActions(), response.getTook().millis());
                acknowledgeKafkaOffsets(request);
                logFailures(response);
            }

            @Override
            public void afterBulk(long executionId, BulkRequest request, Throwable failure) {
                logger.error("Bulk request " + executionId + " with " + request.numberOfActions() + " actions failed",
                        failure);
                if (kafkaListenerManager.isActive()) {
                    kafkaListenerManager.pause();
                }
            }
        };
    }

    private void acknowledgeKafkaOffsets(BulkRequest request) {
        Collection<KafkaAcknowledgment> acknowledgments = getAcknowledgmentsWithMaxOffsetPerPartition(request);
        if (kafkaListenerManager.isActive()) {
            acknowledgments.forEach(ack -> executorService.execute(() -> {
                logger.debug("Committing offset {} for partition '{}'", ack.getOffset() + 1, ack.getPartition());
                ack.acknowledge();
            }));
        } else {
            logger.warn("Previous bulk request has failed - offsets will not be committed to Kafka!");
        }
    }

    private Collection<KafkaAcknowledgment> getAcknowledgmentsWithMaxOffsetPerPartition(BulkRequest request) {
        List<DocWriteRequest<?>> requests = request.requests();
        return requests.stream()
                .filter(req -> req instanceof KafkaAcknowledgeableIndexRequest)
                .map(req -> ((KafkaAcknowledgeableIndexRequest) req).getAcknowledgment())
                .collect(Collectors.toMap(KafkaAcknowledgment::getPartition, ack -> ack,
                        (ack1, ack2) -> ack1.getOffset() >= ack2.getOffset() ? ack1 : ack2))
                .values();
    }

    private void logFailures(BulkResponse response) {
        // only print warnings which are not 409 (i.e. version conflicts)
        if (response.hasFailures() && response.getItems() != null) {
            for (BulkItemResponse itemResponse : response.getItems()) {
                if (itemResponse.getFailure() != null
                        && itemResponse.getFailure().getStatus() != null
                        && itemResponse.getFailure().getStatus() != RestStatus.CONFLICT) {
                    logger.warn(response.buildFailureMessage());
                }
            }
        }
    }
}
