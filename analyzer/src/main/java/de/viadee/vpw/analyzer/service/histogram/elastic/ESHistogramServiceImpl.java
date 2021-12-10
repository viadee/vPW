package de.viadee.vpw.analyzer.service.histogram.elastic;

import java.util.Collections;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import de.viadee.vpw.analyzer.dto.entity.HistogramBucket;
import de.viadee.vpw.analyzer.dto.entity.filter.CalculationFilter;
import de.viadee.vpw.analyzer.service.histogram.HistogramService;
import de.viadee.vpw.analyzer.service.histogram.elastic.dto.Bounds;
import de.viadee.vpw.analyzer.service.histogram.elastic.step.ESHistogramBucketAggregator;
import de.viadee.vpw.analyzer.service.histogram.elastic.step.ESHistogramMinMaxAggregator;

@Component
public class ESHistogramServiceImpl implements HistogramService {

    private final ESHistogramMinMaxAggregator histogramMinMaxService;

    private final ESHistogramBucketAggregator histogramBucketAggregator;

    @Autowired
    public ESHistogramServiceImpl(ESHistogramMinMaxAggregator histogramMinMaxService,
            ESHistogramBucketAggregator histogramBucketAggregator) {
        this.histogramMinMaxService = histogramMinMaxService;
        this.histogramBucketAggregator = histogramBucketAggregator;
    }

    @Override
    public List<HistogramBucket> count(String processDefinitionId, CalculationFilter filter, int numberOfBuckets) {
        Bounds bounds = this.histogramMinMaxService.bounds(processDefinitionId, filter);

        if (bounds != null) {
            return this.histogramBucketAggregator.bucketForBounds(processDefinitionId, filter, bounds, numberOfBuckets);
        }

        return Collections.emptyList();
    }
}
