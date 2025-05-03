package com.example.kafka_example;

import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.event.EventListener;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;

@SpringBootApplication
public class KafkaExampleApplication {

  private static final String TOPIC_NAME = "dog-adoption-requests";

  public static void main(String[] args) {
    SpringApplication.run(KafkaExampleApplication.class, args);
  }

  @Bean
  ApplicationRunner sender(KafkaTemplate<String, DogAdoptionRequest> template) {
    return _ -> {
      template.send(TOPIC_NAME, new DogAdoptionRequest(1, "Buddy"));
    };
  }

    @KafkaListener(topics = TOPIC_NAME, groupId = "mygroup")
    void listen(DogAdoptionRequest request) {
        System.out.println("Received dog adoption request: " + request);
    }
}

record DogAdoptionRequest(int dogId, String dogname) {}