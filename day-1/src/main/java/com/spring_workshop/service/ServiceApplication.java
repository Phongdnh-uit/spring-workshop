package com.spring_workshop.service;

import static org.springframework.web.servlet.function.RouterFunctions.route;

import com.spring_workshop.service.entity.Customer;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.client.RestClient;
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
        .GET(
            "/pattern-matching",
            _ -> ServerResponse.ok().body(PatternMatching.getMessage(new SecureLoan("Hello word"))))
        .build();
  }
}

interface CustomerRepository extends JpaRepository<Customer, Long> {}

final class PatternMatching {
  public static String getMessage(Loan loan) {
    return switch (loan) {
      case UnsecureLoan _ -> "This is unsecure loan";
      case SecureLoan sl -> sl.getMessage();
      default -> "Unknow type";
    };
  }
}

sealed class Loan permits SecureLoan, UnsecureLoan {}

final class SecureLoan extends Loan {
  public SecureLoan(String message) {
    this.message = message;
  }

  private String message;

  public String getMessage() {
    return message;
  }
}

final class UnsecureLoan extends Loan {}

@Controller
@ResponseBody
class DemoController {

  private final RestClient http;

  DemoController(RestClient.Builder http) {
    this.http = http.build();
  }

  @GetMapping("/delay")
  String delay() {
    return this.http.get().uri("http://httpbin.org/delay/2").retrieve().body(String.class);
  }
}
