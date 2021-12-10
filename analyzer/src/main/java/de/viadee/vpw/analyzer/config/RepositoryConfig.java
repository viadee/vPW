package de.viadee.vpw.analyzer.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.rest.core.config.RepositoryRestConfiguration;
import org.springframework.data.rest.webmvc.config.RepositoryRestConfigurer;

import de.viadee.vpw.analyzer.data.entity.Dashboard;
import de.viadee.vpw.analyzer.data.entity.Indicator;
import de.viadee.vpw.analyzer.data.entity.ProcessDefinition;
import org.springframework.web.servlet.config.annotation.CorsRegistry;

/**
 * Configure the repository rest exposure.
 */
@Configuration
public class RepositoryConfig implements RepositoryRestConfigurer {

    /**
     * Enable expods ids for entities
     *
     * @param config repository rest configuration
     */
    @Override
    public void configureRepositoryRestConfiguration(RepositoryRestConfiguration config, CorsRegistry cors) {
        config.exposeIdsFor(Indicator.class, Dashboard.class, ProcessDefinition.class);
    }
}
