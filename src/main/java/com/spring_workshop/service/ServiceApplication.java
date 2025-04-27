package com.spring_workshop.service;

import static org.springframework.web.servlet.function.RouterFunctions.route;

import com.spring_workshop.service.entity.Customer;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.web.servlet.function.HandlerFunction;
import org.springframework.web.servlet.function.RouterFunction;
import org.springframework.web.servlet.function.ServerRequest;
import org.springframework.web.servlet.function.ServerResponse;

@SpringBootApplication
public class ServiceApplication {

  public static void main(String[] args) {
    SpringApplication.run(ServiceApplication.class, args);
  }

  @Bean
  RouterFunction<ServerResponse> myRoutes(CustomerRepository customerRepository) {
    return route()
        .GET(
            "/customers",
            new HandlerFunction<ServerResponse>() {
              @Override
              public ServerResponse handle(ServerRequest request) throws Exception {
                return ServerResponse.ok().body(customerRepository.findAll());
              }
            })
        .build();
  }
}

interface CustomerRepository extends JpaRepository<Customer, Long> {}
