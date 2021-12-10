package de.viadee.vpw.analyzer.api;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import de.viadee.vpw.analyzer.dto.entity.HistogramBucket;
import de.viadee.vpw.analyzer.dto.entity.filter.CalculationFilter;
import de.viadee.vpw.analyzer.service.histogram.HistogramService;

@CrossOrigin
@RestController
public class ProcessHistogramApiController {

    private final HistogramService histogramService;

    @Autowired
    public ProcessHistogramApiController(HistogramService histogramService) {
        this.histogramService = histogramService;
    }

    /**
     * Calculate process histogram for process definition with a number of buckets
     *
     * @param processDefinitionId id of the process definition to create the histogram
     * @param rawNumberOfBuckets  number of buckets for histogram (default value 100)
     * @return calculated histogram
     */
    @GetMapping(path = "/process/{processDefinitionId}/histogram")
    public List<HistogramBucket> processCountHistogram(@PathVariable String processDefinitionId,
            @RequestParam(required = false, name = "numberOfBuckets") String rawNumberOfBuckets) {
        return filteredProcessCountHistogram(processDefinitionId, rawNumberOfBuckets, null);
    }

    /**
     * Calculate process histogram for process definition with a number of buckets and a query filter
     *
     * @param processDefinitionId id of the process definition to create the histogram
     * @param rawNumberOfBuckets  number of buckets for histogram (default value 100)
     * @param filter              filter query sent via POST parameter
     * @return calculated histogram
     */
    @PostMapping(path = "/process/{processDefinitionId}/histogram")
    public List<HistogramBucket> filteredProcessCountHistogram(@PathVariable String processDefinitionId,
            @RequestParam(required = false, name = "numberOfBuckets") String rawNumberOfBuckets,
            @RequestBody CalculationFilter filter) {
        int numberOfBuckets;
        try {
            numberOfBuckets = Integer.parseInt(rawNumberOfBuckets);
        } catch (NumberFormatException e) {
            numberOfBuckets = 100;
        }
        return histogramService.count(processDefinitionId, filter, numberOfBuckets);
    }
}
