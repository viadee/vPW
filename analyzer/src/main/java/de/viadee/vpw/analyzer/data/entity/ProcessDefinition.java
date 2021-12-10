package de.viadee.vpw.analyzer.data.entity;

import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.OneToMany;

import org.camunda.bpm.model.bpmn.Bpmn;
import org.camunda.bpm.model.bpmn.BpmnModelInstance;
import org.camunda.bpm.model.bpmn.instance.FlowNode;
import org.camunda.bpm.model.bpmn.instance.Lane;

@Entity
public final class ProcessDefinition extends AbstractModel<String> {

    private String key;

    private Integer version;

    private String name;

    private String category;

    private String description;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "processDefinitionId")
    private Set<ProcessElement> elements;

    @Lob
    @Basic(fetch = FetchType.LAZY)
    private String xml;

    private String versionTag;

    private String deploymentId;

    private Date deploymentTime;

    private String tenantId;

    public ProcessDefinition() {
        super("");
        this.elements = new HashSet<>();
    }

    private void updateModelInstance(final BpmnModelInstance modelInstance) {
        final Collection<FlowNode> nodes = modelInstance.getModelElementsByType(FlowNode.class);
        final Collection<Lane> lanes = modelInstance.getModelElementsByType(Lane.class);

        final Set<ProcessElement> processElementSet = new HashSet<>();
        for (final FlowNode node : nodes) {
            processElementSet
                    .add(new ProcessElement(node.getId(), node.getName(), node.getElementType().getTypeName()));
        }

        for (final Lane lane : lanes) {
            processElementSet
                    .add(new ProcessElement(lane.getId(), lane.getName(), lane.getElementType().getTypeName()));
        }

        this.elements = processElementSet;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public Integer getVersion() {
        return version;
    }

    public void setVersion(Integer version) {
        this.version = version;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Set<ProcessElement> getElements() {
        return elements;
    }

    public void setElements(Set<ProcessElement> elements) {
        this.elements = elements;
    }

    public String getXml() {
        return xml;
    }

    public void setXml(String xml) {
        this.xml = xml;
        this.updateModelInstance(
                Bpmn.readModelFromStream(new ByteArrayInputStream(xml.getBytes(StandardCharsets.UTF_8))));
    }

    public String getVersionTag() {
        return versionTag;
    }

    public void setVersionTag(String versionTag) {
        this.versionTag = versionTag;
    }

    public String getDeploymentId() {
        return deploymentId;
    }

    public void setDeploymentId(String deploymentId) {
        this.deploymentId = deploymentId;
    }

    public Date getDeploymentTime() {
        return deploymentTime;
    }

    public void setDeploymentTime(Date deploymentTime) {
        this.deploymentTime = deploymentTime;
    }

    public String getTenantId() {
        return tenantId;
    }

    public void setTenantId(String tenantId) {
        this.tenantId = tenantId;
    }
}
