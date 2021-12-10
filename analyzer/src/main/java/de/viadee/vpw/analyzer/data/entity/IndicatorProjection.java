package de.viadee.vpw.analyzer.data.entity;

import org.springframework.data.rest.core.config.Projection;

@Projection(name = "indicator", types = { Indicator.class })
public interface IndicatorProjection extends SimpleProjection {

    String getType();

    String getSubtype();
}
