package com.example.service;

import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.context.WebServerInitializedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication
@RestController
public class ServiceApplication {

  public static void main(String[] args) {
    SpringApplication.run(ServiceApplication.class, args);
  }

  private final AtomicInteger port = new AtomicInteger(0);

  @GetMapping("/hello")
  Map<String, String> hello() {
    return Map.of("message", "Hello World", "port", String.valueOf(port.get()));
  }

  @EventListener
  void onStartup(WebServerInitializedEvent event) {
    this.port.set(event.getWebServer().getPort());
  }
}
