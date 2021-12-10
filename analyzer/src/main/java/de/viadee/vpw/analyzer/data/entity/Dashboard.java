package de.viadee.vpw.analyzer.data.entity;

import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.Entity;

import com.fasterxml.jackson.databind.JsonNode;

import de.viadee.vpw.analyzer.data.converter.JpaJsonNodeConverter;
import jakarta.validation.constraints.NotNull;

@Entity
public class Dashboard extends AbstractUUIDModel {

    @NotNull
    private String name;

    private String description;

    @Column(columnDefinition = "TEXT")
    @Convert(converter = JpaJsonNodeConverter.class)
    private JsonNode visualizations;

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

    public JsonNode getVisualizations() {
        return visualizations;
    }

    public void setVisualizations(JsonNode visualizations) {
        this.visualizations = visualizations;
    }
}
