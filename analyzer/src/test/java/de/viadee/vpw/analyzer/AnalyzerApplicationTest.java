package de.viadee.vpw.analyzer;

import de.viadee.vpw.analyzer.config.ApplicationConfig;
import de.viadee.vpw.analyzer.config.ElasticsearchConfig;
import de.viadee.vpw.analyzer.config.RepositoryConfig;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ActiveProfiles("test")
@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = AnalyzerApplication.class, properties = "spring.main.allow-bean-definition-overriding=true")
@ContextConfiguration(classes = {ApplicationConfig.class, ElasticsearchConfig.class, RepositoryConfig.class})
public class AnalyzerApplicationTest {

    @Test
    @Disabled
    public void ignorableTest() {
    }
}
