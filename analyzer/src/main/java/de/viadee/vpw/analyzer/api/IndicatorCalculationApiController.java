package de.viadee.vpw.analyzer.api;

import java.util.UUID;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import de.viadee.vpw.analyzer.dto.entity.IndicatorInstance;
import de.viadee.vpw.analyzer.dto.entity.filter.CalculationFilter;
import de.viadee.vpw.analyzer.service.indicator.IndicatorCalculationService;

@CrossOrigin
@RestController
public class IndicatorCalculationApiController {

    private final IndicatorCalculationService calculationService;

    @Autowired
    public IndicatorCalculationApiController(IndicatorCalculationService calculationService) {
        this.calculationService = calculationService;
    }

    /**
     * Calculate indicator instance with indicator id
     *
     * @param processDefinitionId processDefinitionId sent via URL path
     * @param indicatorId         indicatorId sent via URL path
     * @return calculated process instance
     */
    @GetMapping(value = "/process/{processDefinitionId}/indicator/{indicatorId}/calculate")
    public IndicatorInstance calculate(@PathVariable String processDefinitionId, @PathVariable UUID indicatorId) {
        return calculateWithFilter(processDefinitionId, indicatorId, null);
    }

    /**
     * Calculate indicator instance with indicator id and filter
     *
     * @param processDefinitionId processDefinitionId sent via URL path
     * @param indicatorId         indicatorId sent via URL path
     * @param filter              filter sent via POST parameter
     * @return calculated indicator instance
     */
    @PostMapping(value = "/process/{processDefinitionId}/indicator/{indicatorId}/calculate")
    public IndicatorInstance calculateWithFilter(@PathVariable String processDefinitionId,
            @PathVariable UUID indicatorId, @Valid @RequestBody CalculationFilter filter) {
        return calculationService.calculate(processDefinitionId, indicatorId, filter);
    }
}
