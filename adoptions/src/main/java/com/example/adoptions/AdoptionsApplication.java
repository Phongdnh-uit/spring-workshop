package com.example.adoptions;

import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.modulith.events.IncompleteEventPublications;

@SpringBootApplication
public class AdoptionsApplication {

    public static void main(String[] args) {
        SpringApplication.run(AdoptionsApplication.class, args);
    }

    @Bean
    ApplicationRunner youIncompletedMe(IncompleteEventPublications incompleteEventPublications) {
        return args -> {
            incompleteEventPublications.resubmitIncompletePublications(ep -> true);
        };
    }
}
