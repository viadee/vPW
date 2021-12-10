package de.viadee.vpw.pipeline.model;

import java.util.Date;

public class ActivityInstanceEvent extends de.viadee.camunda.kafka.event.ActivityInstanceEvent
        implements ElasticsearchEntity {

    private static final String TYPE = "activity";

    @Override
    public String getType() {
        return TYPE;
    }

    @Override
    public JoinModel getParentJoin() {
        return new JoinModel(TYPE, getProcessInstanceId());
    }

    @Override
    public Date getTimestamp() {
        return getStartTime();
    }
}
