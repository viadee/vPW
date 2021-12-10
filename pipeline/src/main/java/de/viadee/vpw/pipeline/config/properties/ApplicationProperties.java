package de.viadee.vpw.pipeline.config.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = ApplicationProperties.PREFIX)
public class ApplicationProperties {

    static final String PREFIX = "vpw.pipeline";

    private String processDefinitionRestUrl;

    public String getProcessDefinitionRestUrl() {
        return processDefinitionRestUrl;
    }

    public void setProcessDefinitionRestUrl(String processDefinitionRestUrl) {
        this.processDefinitionRestUrl = processDefinitionRestUrl;
    }
}
