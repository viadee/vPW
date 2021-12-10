package de.viadee.vpw.analyzer.data.entity;

import java.util.Date;

import org.springframework.data.rest.core.config.Projection;

@Projection(name = "simple", types = { Dashboard.class })
public interface SimpleProjection {

    String getId();

    Date getLastChanged();

    String getName();

    String getDescription();
}
