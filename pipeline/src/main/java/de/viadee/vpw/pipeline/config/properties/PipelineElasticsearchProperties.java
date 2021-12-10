package de.viadee.vpw.pipeline.config.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

import de.viadee.vpw.shared.config.elasticsearch.ElasticsearchProperties;

/**
 * Pipeline-spezifische Properties (zusätzlich zu {@link ElasticsearchProperties}
 */
@ConfigurationProperties(prefix = ApplicationProperties.PREFIX + ".elasticsearch")
public class PipelineElasticsearchProperties {

    /**
     * Maximum number of single requests per bulk request.
     */
    private int bulkActions = 100;

    /**
     * Flush interval for bulk requests in milliseconds.
     */
    private long flushInterval = 5000;

    /**
     * Number of shards per index.
     */
    private int numberOfShards = 1;

    /**
     * Number of replicas for each primary shard.
     */
    private int numberOfReplicas = 2;

    public int getBulkActions() {
        return bulkActions;
    }

    public void setBulkActions(int bulkActions) {
        this.bulkActions = bulkActions;
    }

    public long getFlushInterval() {
        return flushInterval;
    }

    public void setFlushInterval(long flushInterval) {
        this.flushInterval = flushInterval;
    }

    public int getNumberOfShards() {
        return numberOfShards;
    }

    public void setNumberOfShards(int numberOfShards) {
        this.numberOfShards = numberOfShards;
    }

    public int getNumberOfReplicas() {
        return numberOfReplicas;
    }

    public void setNumberOfReplicas(int numberOfReplicas) {
        this.numberOfReplicas = numberOfReplicas;
    }
}
