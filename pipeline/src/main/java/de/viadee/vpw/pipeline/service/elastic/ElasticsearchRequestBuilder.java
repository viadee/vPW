package de.viadee.vpw.pipeline.service.elastic;

import org.apache.commons.codec.digest.DigestUtils;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.index.VersionType;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import de.viadee.camunda.kafka.event.HistoryEvent;
import de.viadee.vpw.pipeline.kafka.KafkaAcknowledgment;
import de.viadee.vpw.pipeline.service.json.JsonMapper;
import de.viadee.vpw.shared.config.elasticsearch.ElasticsearchProperties;

@Service
public class ElasticsearchRequestBuilder {

    private final ElasticsearchProperties properties;

    private final JsonMapper jsonMapper;

    public ElasticsearchRequestBuilder(@Qualifier("vpw.elasticsearch-de.viadee.vpw.shared.config.elasticsearch.ElasticsearchProperties") ElasticsearchProperties properties, JsonMapper jsonMapper) {
        this.properties = properties;
        this.jsonMapper = jsonMapper;
    }

    /**
     * Builds an acknowledgeable index request with default version numbering.
     */
    public IndexRequest buildIndexRequest(String id, HistoryEvent event, KafkaAcknowledgment acknowledgment) {
        return new Builder(id, event).acknowledgment(acknowledgment).build();
    }

    /**
     * Builds an acknowledgeable index request with custom version numbering.
     */
    public IndexRequest buildIndexRequest(String id, long version, HistoryEvent event,
            KafkaAcknowledgment acknowledgment) {
        return new Builder(id, event).externalVersion(version).acknowledgment(acknowledgment).build();
    }

    /**
     * Builds a non-acknowledgeable index request with custom version numbering.
     */
    public IndexRequest buildIndexRequest(String id, long version, HistoryEvent event) {
        return new Builder(id, event).externalVersion(version).build();
    }

    private class Builder {

        private final String id;

        private final HistoryEvent event;

        private long version = 0;

        private KafkaAcknowledgment acknowledgment;

        private Builder(String id, HistoryEvent event) {
            this.id = id;
            this.event = event;
        }

        private Builder externalVersion(long version) {
            if (version <= 0) {
                throw new IllegalArgumentException("version must be > 0");
            }
            this.version = version;
            return this;
        }

        private Builder acknowledgment(KafkaAcknowledgment acknowledgment) {
            this.acknowledgment = acknowledgment;
            return this;
        }

        private IndexRequest build() {
            String index = properties.getIndexPrefix() + DigestUtils.sha256Hex(event.getProcessDefinitionId());
            String type = properties.getMappingType();

            IndexRequest request = (acknowledgment == null ?
                    new IndexRequest(index, type, id) :
                    new KafkaAcknowledgeableIndexRequest(index, type, id, acknowledgment))

                    // Route all events from the same process instance to the same shard in Elasticsearch.
                    // This is necessary to use parent/child-relations.
                    .routing(event.getProcessInstanceId())

                    // Convert event to JSON and use as source
                    .source(jsonMapper.toJsonAsByteArray(event), XContentType.JSON);

            if (version > 0) {
                request.versionType(VersionType.EXTERNAL).version(version);
            }

            return request;
        }
    }
}
