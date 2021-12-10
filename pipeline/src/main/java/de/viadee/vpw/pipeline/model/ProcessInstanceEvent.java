package de.viadee.vpw.pipeline.model;

import java.util.Date;

public class ProcessInstanceEvent extends de.viadee.camunda.kafka.event.ProcessInstanceEvent
        implements ElasticsearchEntity {

    static final String TYPE = "process";

    @Override
    public String getType() {
        return TYPE;
    }

    @Override
    public Date getTimestamp() {
        return getStartTime();
    }
}
