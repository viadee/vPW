package de.viadee.vpw.pipeline;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

@SpringBootApplication
public class PipelineApplication {

    private static ConfigurableApplicationContext ctx;

    public static void main(String[] args) {
        ctx = SpringApplication.run(PipelineApplication.class, args);
    }

    public static ConfigurableApplicationContext getCtx() {
        return ctx;
    }

    public static void exitApplication(ConfigurableApplicationContext ctx){
        int exitCode = SpringApplication.exit(ctx, () -> -1);
        System.exit(exitCode);
    }
}
