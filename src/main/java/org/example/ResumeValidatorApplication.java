package org.example;

import org.example.service.ResumeNavigatorService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class ResumeValidatorApplication implements CommandLineRunner {

    private final ResumeNavigatorService navigatorService;

    public ResumeValidatorApplication(ResumeNavigatorService navigatorService) {
        this.navigatorService = navigatorService;
    }

    public static void main(String[] args) {
        SpringApplication.run(ResumeValidatorApplication.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        navigatorService.navigateThroughResumes();
    }
}
