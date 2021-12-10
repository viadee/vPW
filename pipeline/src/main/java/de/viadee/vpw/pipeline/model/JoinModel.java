package de.viadee.vpw.pipeline.model;

/**
 * Joining model for the parent/child relation for Elasticsearch
 */
public class JoinModel {

    /**
     * Name of the relationship. Possible values: process, activity, variable
     */
    private final String name;

    /**
     * Id of the parent. Filled if the name is activity or variable with the id of the parent / processInstanceId or
     * activityInstanceId
     */
    private final String parent;

    JoinModel(String name, String parent) {
        this.name = name;
        this.parent = parent;
    }

    public String getName() {
        return name;
    }

    public String getParent() {
        return parent;
    }
}
