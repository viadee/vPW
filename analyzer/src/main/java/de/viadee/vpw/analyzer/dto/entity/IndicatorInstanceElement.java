package de.viadee.vpw.analyzer.dto.entity;

public class IndicatorInstanceElement {

    private final String elementId;

    private final Double value;

    public IndicatorInstanceElement(String elementId, Double value) {
        this.elementId = elementId;
        this.value = value;
    }

    public String getElementId() {
        return elementId;
    }

    public Double getValue() {
        return value;
    }
}
