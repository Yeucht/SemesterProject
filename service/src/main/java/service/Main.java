package service;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication(scanBasePackages = {"service", "controller"})
@RestController
public class Main {
    @RequestMapping("/")
    public String home() {
        return "Hello Docker World";
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