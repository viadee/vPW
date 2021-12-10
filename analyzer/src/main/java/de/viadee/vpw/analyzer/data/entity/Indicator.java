package de.viadee.vpw.analyzer.data.entity;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
//import javax.validation.constraints.NotNull;

import jakarta.validation.constraints.NotNull;
import org.springframework.stereotype.Component;

import de.viadee.vpw.analyzer.data.typelist.AggregationOperator;
import de.viadee.vpw.analyzer.data.typelist.IndicatorSubtype;
import de.viadee.vpw.analyzer.data.typelist.IndicatorType;

@Entity
@Component
public class Indicator extends AbstractUUIDModel {

    @NotNull
    private String name;

    private String description;

    private String processDefinitionId;

    @NotNull
    @Enumerated(EnumType.STRING)
    private IndicatorType type;

    @NotNull
    @Enumerated(EnumType.STRING)
    private IndicatorSubtype subtype;

    @NotNull
    @Enumerated(EnumType.STRING)
    private AggregationOperator operator;

    private String variable;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getProcessDefinitionId() {
        return processDefinitionId;
    }

    public void setProcessDefinitionId(String processDefinitionId) {
        this.processDefinitionId = processDefinitionId;
    }

    public IndicatorType getType() {
        return type;
    }

    public void setType(IndicatorType type) {
        this.type = type;
    }

    public IndicatorSubtype getSubtype() {
        return subtype;
    }

    public void setSubtype(IndicatorSubtype subtype) {
        this.subtype = subtype;
    }

    public String getVariable() {
        return variable;
    }

    public void setVariable(String variable) {
        this.variable = variable;
    }

    public AggregationOperator getOperator() {
        return operator;
    }

    public void setOperator(AggregationOperator operator) {
        this.operator = operator;
    }
}
