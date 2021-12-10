package de.viadee.vpw.analyzer.runner;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.List;
import java.util.stream.StreamSupport;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;

import de.viadee.vpw.analyzer.data.entity.Indicator;
import de.viadee.vpw.analyzer.data.repository.IndicatorRepository;

@Component
public class IndicatorInitializer implements ApplicationRunner {

    private final IndicatorRepository indicatorRepository;

    @Autowired
    public IndicatorInitializer(IndicatorRepository indicatorRepository) {
        this.indicatorRepository = indicatorRepository;
    }

    @Override
    public void run(ApplicationArguments args) throws Exception {
        List<Indicator> indicators = readDefaultIndicators();
        createNonExistingIndicators(indicators);
    }

    private List<Indicator> readDefaultIndicators() throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        InputStream indicators = new ClassPathResource("default-indicators.json").getInputStream();
        return Arrays.asList(objectMapper.readValue(indicators, Indicator[].class));
    }

    private void createNonExistingIndicators(List<Indicator> indicators) {
        Iterable<Indicator> existingIndicators = indicatorRepository.findAll();
        indicators.stream().filter(i -> notExists(i, existingIndicators)).forEach(indicatorRepository::save);
    }

    private boolean notExists(Indicator indicator, Iterable<Indicator> existing) {
        return StreamSupport.stream(existing.spliterator(), false)
                .noneMatch(i -> matchByTypeSubtypeOperator(indicator, i));
    }

    private boolean matchByTypeSubtypeOperator(Indicator i1, Indicator i2) {
        return i1.getType() == i2.getType()
                && i1.getSubtype() == i2.getSubtype()
                && i1.getOperator() == i2.getOperator();
    }
}
