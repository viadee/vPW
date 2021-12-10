package de.viadee.vpw.analyzer.data.entity;

import org.springframework.data.rest.core.config.Projection;

@Projection(name = "processDefinition", types = { ProcessDefinition.class })
public interface ProcessDefinitionProjection extends SimpleProjection {

    String getKey();

    String getVersion();
}
