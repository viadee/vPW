package de.viadee.vpw.analyzer.dto.entity;

import de.viadee.vpw.analyzer.dto.typelist.ProcessVariableType;

public class ProcessVariable {

    private String name;

    private ProcessVariableType type;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ProcessVariableType getType() {
        return type;
    }

    public void setType(ProcessVariableType type) {
        this.type = type;
    }
}
