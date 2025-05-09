package com.example.client;

import static org.springframework.cloud.gateway.server.mvc.filter.LoadBalancerFilterFunctions.lb;
import static org.springframework.cloud.gateway.server.mvc.handler.HandlerFunctions.http;
import static org.springframework.web.servlet.function.RouterFunctions.route;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.gateway.server.mvc.filter.BeforeFilterFunctions;
import org.springframework.context.annotation.Bean;
import org.springframework.web.servlet.function.RouterFunction;
import org.springframework.web.servlet.function.ServerResponse;

@SpringBootApplication
public class ClientApplication {

  // @Bean
  // @LoadBalanced
  // RestClient.Builder RestClientBuilder() {
  //   return RestClient.builder();
  // }

  public static void main(String[] args) {
    SpringApplication.run(ClientApplication.class, args);
  }

  @Bean
  RouterFunction<ServerResponse> routes() {
    return route()
        .before(BeforeFilterFunctions.rewritePath("/api", "/"))
        .GET("/api/hello", http())
        .filter(lb("service"))
        .build();
  }
}

// @RestController
// class LoadBalancedControlller {
//   private final RestClient restClient;
//
//   public LoadBalancedControlller(RestClient.Builder builder) {
//     this.restClient = builder.build();
//   }
//
//   @GetMapping("/hello")
//   public String loadBalanced() {
//     return restClient.get().uri("http://service/hello").retrieve().body(String.class);
//   }
// }
