package de.viadee.vpw.analyzer.data.entity;

import java.util.UUID;

import javax.persistence.Entity;

@Entity
public class ProcessElement extends AbstractUUIDModel {

    private String processElementId;

    private String name;

    private String type;

    public ProcessElement() {
        super();
    }

    public ProcessElement(UUID id) {
        super(id);
    }

    public ProcessElement(final String processElementId, final String name, final String type) {
        this.processElementId = processElementId;
        this.name = name;
        this.type = type;
    }

    public String getProcessElementId() {
        return this.processElementId;
    }

    public String getName() {
        return this.name;
    }

    public String getType() {
        return this.type;
    }
}
