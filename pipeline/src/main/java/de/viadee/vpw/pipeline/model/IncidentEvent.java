package de.viadee.vpw.pipeline.model;

import java.util.Date;

public class IncidentEvent extends de.viadee.camunda.kafka.event.IncidentEvent
        implements ElasticsearchEntity {

    private static final String TYPE = "incident";

    @Override
    public String getType() {
        return TYPE;
    }

    @Override
    public Object getParentJoin() {
        return new JoinModel(ProcessInstanceEvent.TYPE, getProcessInstanceId());
    }

    @Override
    public Date getTimestamp() {
        return getCreateTime();
    }
}
