package com.example.oauth2_resource_server;

import java.security.Principal;
import java.util.Map;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication
public class Oauth2ResourceServerApplication {

  public static void main(String[] args) {
    SpringApplication.run(Oauth2ResourceServerApplication.class, args);
  }
}

@RestController
class HelloController {

  @GetMapping("/")
  public Map<String, String> hello(Principal principal) {
    return Map.of(
        "message", "Hello, " + principal.getName() + "!", "principal", principal.toString());
  }
}
