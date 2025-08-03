package service;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import dbmanager.*;

@SpringBootApplication(scanBasePackages = {"service", "controller", "factories", "infrastructure", "simulation", "config"})
@EnableJpaRepositories(basePackages = {"simulation", "config"})
@EntityScan(basePackages = {"simulation", "config"})
@EnableScheduling
@RestController
public class Main {
    @RequestMapping("/")
    public String home() {
        return "Welcome to IoT data ingestion benchmarking project";
    }


    @Bean
    public CommandLineRunner checkBeans(ApplicationContext ctx) {
        return args -> {
            System.out.println("Beans loaded by Spring Boot:");
            for (String beanName : ctx.getBeanDefinitionNames()) {
                System.out.println(beanName);
            }
        };
    }

    public static void main(String[] args) {
        SpringApplication.run(Main.class, args);
    }
}