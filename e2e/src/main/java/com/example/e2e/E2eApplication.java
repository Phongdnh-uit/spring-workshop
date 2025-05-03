package com.example.e2e;

import java.util.Collection;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.annotation.Id;
import org.springframework.data.repository.ListCrudRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@SpringBootApplication
public class E2eApplication {

  public static void main(String[] args) {
    SpringApplication.run(E2eApplication.class, args);
  }
}

record Customer(@Id Long id, String name) {}

@Controller
@ResponseBody
@RequiredArgsConstructor
class CustomerController {
  private final CustomerRepository customerRepository;

  @GetMapping("/customer")
  public ResponseEntity<Collection<Customer>> getCustomer() {
    return ResponseEntity.ok(customerRepository.findAll());
  }
}

interface CustomerRepository extends ListCrudRepository<Customer, Long> {}
