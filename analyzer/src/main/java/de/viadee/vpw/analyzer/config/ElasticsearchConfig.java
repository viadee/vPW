package de.viadee.vpw.analyzer.config;

import org.elasticsearch.action.admin.cluster.health.ClusterHealthRequest;
import org.elasticsearch.action.admin.cluster.health.ClusterHealthResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.Requests;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.cluster.health.ClusterHealthStatus;
import org.elasticsearch.core.TimeValue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.actuate.health.AbstractHealthIndicator;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import java.io.IOException;
@Configuration
public class ElasticsearchConfig {

    @Bean
    public HealthIndicator elasticsearchHealthIndicator(RestHighLevelClient client) {
        return new ElasticsearchHealthIndicator(client);
    }

    /**
     * Based on {@link org.springframework.boot.actuate.elasticsearch.ElasticsearchRestHealthIndicator}, but uses
     * RestHighLevelClient.
     */
    public static class ElasticsearchHealthIndicator extends AbstractHealthIndicator {

        private final Logger logger = LoggerFactory.getLogger(ElasticsearchHealthIndicator.class);

        private final RestHighLevelClient client;

        ElasticsearchHealthIndicator(RestHighLevelClient client) {
            this.client = client;
        }

        @Override
        protected void doHealthCheck(Health.Builder builder) throws Exception {
            logger.debug("Performing Elasticsearch health check");
            ClusterHealthResponse response = performClusterHealthRequest();
            ClusterHealthStatus status = response.getStatus();
            switch (status) {
                case GREEN:
                case YELLOW:
                    builder.up();
                    logger.debug("Elasticsearch cluster status: {}", status);
                    break;
                case RED:
                default:
                    builder.down();
                    logger.warn("Elasticsearch cluster status: {}", status);
                    break;
            }
            builder.withDetail("clusterName", response.getClusterName());
            builder.withDetail("clusterHealth", status);
        }

        private ClusterHealthResponse performClusterHealthRequest() throws IOException {
            ClusterHealthRequest request = Requests.clusterHealthRequest("_all")
                    .waitForYellowStatus().timeout(TimeValue.timeValueSeconds(5));
            return client.cluster().health(request, RequestOptions.DEFAULT);
        }
    }
}
