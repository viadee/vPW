package de.viadee.vpw.analyzer.service.histogram;

import java.util.List;

import de.viadee.vpw.analyzer.dto.entity.HistogramBucket;
import de.viadee.vpw.analyzer.dto.entity.filter.CalculationFilter;

public interface HistogramService {

    List<HistogramBucket> count(String processDefinitionId, CalculationFilter filter, int numberOfBuckets);
}
