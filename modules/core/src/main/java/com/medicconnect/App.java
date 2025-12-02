package com.medicconnect;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.ApplicationContext;
import org.springframework.context.event.EventListener;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.cloud.openfeign.EnableFeignClients;

import java.time.LocalDateTime;
import java.util.Arrays;

@SpringBootApplication(scanBasePackages = {
        "com.medicconnect",
        "com.medicconnect.openmrs"
})
@EnableFeignClients(basePackages = {
        "com.medicconnect.openmrs.client"
})
@EntityScan(basePackages = {
        "com.medicconnect.models",              // MedicConnect models
        "com.medicconnect.openmrs.model"        // OpenMRS models
})
@EnableJpaRepositories(basePackages = {
        "com.medicconnect.repository",        // MedicConnect repos
        "com.medicconnect.openmrs.repository"   // OpenMRS repos
})
public class App {

    private final Environment env;
    private final ApplicationContext ctx;

    @Value("${server.port:8080}")
    private String port;

    @Value("${spring.datasource.url}")
    private String dbUrl;

    public App(Environment env, ApplicationContext ctx) {
        this.env = env;
        this.ctx = ctx;
    }

    public static void main(String[] args) {
        SpringApplication.run(App.class, args);
        System.out.println("🩺 MedicConnect Unified Backend is starting...");
    }

    @EventListener(ApplicationReadyEvent.class)
    public void logStartupInfo() {
        System.out.println("\n=======================================================");
        System.out.println("           ✔ MedicConnect Backend Started ✔");
        System.out.println("-------------------------------------------------------");
        System.out.println(" Startup Time      : " + LocalDateTime.now());
        System.out.println(" HTTP Port         : " + port);
        System.out.println(" Database URL      : " + dbUrl);
        System.out.println(" Beans Loaded      : " + ctx.getBeanDefinitionCount());
        System.out.println(" Profiles          : " + Arrays.toString(env.getActiveProfiles()));
        System.out.println("=======================================================\n");
    }
}
