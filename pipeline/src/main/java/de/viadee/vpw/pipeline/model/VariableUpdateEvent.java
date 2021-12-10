package de.viadee.vpw.pipeline.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class VariableUpdateEvent extends de.viadee.camunda.kafka.event.VariableUpdateEvent
        implements ElasticsearchEntity {

    private static final String TYPE = "variable";

    @Override
    public String getType() {
        return TYPE;
    }

    @Override
    public JoinModel getParentJoin() {
        return new JoinModel(TYPE, getActivityInstanceId());
    }

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    public Object getComplexValue() {
        return super.getComplexValue();
    }

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    public void setComplexValue(Object complexValue) {
        super.setComplexValue(complexValue);
    }
}
