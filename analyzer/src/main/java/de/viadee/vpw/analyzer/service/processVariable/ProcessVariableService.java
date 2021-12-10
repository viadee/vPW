package de.viadee.vpw.analyzer.service.processVariable;

import java.util.List;

import de.viadee.vpw.analyzer.dto.entity.ProcessVariable;
import de.viadee.vpw.analyzer.dto.typelist.ProcessVariableType;

public interface ProcessVariableService {

    List<ProcessVariable> findByProcessDefinition(String processDefinitionId);

    List<ProcessVariable> findByProcessDefinition(String processDefinitionId, ProcessVariableType type);

    List<String> findByProcessDefinitionAndVariableName(String processDefinitionId, String processVariableName);
}
