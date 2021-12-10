package de.viadee.vpw.analyzer.api;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import de.viadee.vpw.analyzer.dto.entity.ProcessVariable;
import de.viadee.vpw.analyzer.dto.typelist.ProcessVariableType;
import de.viadee.vpw.analyzer.service.processVariable.ProcessVariableService;

/**
 * Process variables search api controller
 */
@CrossOrigin
@RestController
public class ProcessVariableApiController {

    private final ProcessVariableService processVariableService;

    @Autowired
    public ProcessVariableApiController(ProcessVariableService processVariableService) {
        this.processVariableService = processVariableService;
    }

    /**
     * Find all process variables for a process definition id
     *
     * @param processDefinitionId id of the process definition
     * @param type                select only a specific type of process variables
     * @return list of process variables with name and type
     */
    @GetMapping("/process/{processDefinitionId}/variables")
    public List<ProcessVariable> findByProcessDefinitionId(@PathVariable String processDefinitionId,
            @RequestParam(required = false) String type) {
        return this.processVariableService
                .findByProcessDefinition(processDefinitionId, ProcessVariableType.fromValue(type));
    }

    @GetMapping("/process/{processDefinitionId}/variable/{variableName}/values")
    public List<String> findByProcessDefinitionAndVariableName(@PathVariable String processDefinitionId,
                                                           @PathVariable String variableName) {
        return this.processVariableService.findByProcessDefinitionAndVariableName(processDefinitionId,variableName);
    }
}
