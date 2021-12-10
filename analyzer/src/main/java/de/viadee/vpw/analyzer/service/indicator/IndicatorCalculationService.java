package de.viadee.vpw.analyzer.service.indicator;

import java.util.UUID;

import de.viadee.vpw.analyzer.data.entity.Indicator;
import de.viadee.vpw.analyzer.dto.entity.IndicatorInstance;
import de.viadee.vpw.analyzer.dto.entity.filter.CalculationFilter;

/**
 * Calculation of indicator instances based on the process engine events
 */
public interface IndicatorCalculationService {

    /**
     * Calculate indicator instances based on the process engine events
     *
     * @param processDefinitionId processDefinition id to calculate
     * @param indicatorId         indicator id to calculate
     * @param filter              filter to limit the calculation result
     * @return calculation result can be null if no data is available
     */
    IndicatorInstance calculate(String processDefinitionId, UUID indicatorId, CalculationFilter filter);

    /**
     * Calculate indicator instances based on the process engine events
     *
     * @param indicator indicator to calculate
     * @param filter    filter to limit the calculation result
     * @return calculation result can be null if no data is available
     */
    IndicatorInstance calculate(Indicator indicator, CalculationFilter filter);
}
