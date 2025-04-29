package com.example.web;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.example.web.grpc.Users;
import com.example.web.grpc.UsersServiceGrpc;
import com.google.protobuf.Empty;
import io.grpc.stub.StreamObserver;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.graphql.data.method.annotation.BatchMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.graphql.data.method.annotation.SchemaMapping;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.support.RestClientAdapter;
import org.springframework.web.service.annotation.GetExchange;
import org.springframework.web.service.invoker.HttpServiceProxyFactory;

@SpringBootApplication
public class WebApplication {

  @Bean
  RestClient restClient(RestClient.Builder builder) {
    return builder.baseUrl("https://jsonplaceholder.typicode.com").build();
  }

  @Bean
  HttpServiceProxyFactory build(RestClient http) {
    return HttpServiceProxyFactory.builder()
        .exchangeAdapter(RestClientAdapter.create(http))
        .build();
  }

  @Bean
  DeclarativeUsersClient declarativeUsersClient(HttpServiceProxyFactory factory) {
    return factory.createClient(DeclarativeUsersClient.class);
  }

  public static void main(String[] args) {
    SpringApplication.run(WebApplication.class, args);
  }

  @Bean
  ApplicationRunner runner(DeclarativeUsersClient userClient) {
    return _ -> {
      //   userClient.users().forEach(System.out::println);
      //   System.out.println("=======");
      //   System.out.println(userClient.user(1L));
    };
  }
}

// https://jsonplaceholder.typicode.com/users

interface DeclarativeUsersClient {

  @GetExchange("/users/{id}")
  User user(@PathVariable("id") Integer id);

  @GetExchange("/users")
  Collection<User> users();
}

record User(int id, String name, String username, String email, Address address) {}

record Address(String street, String suite, String city, String zipcode, Geo geo) {}

record Geo(float lat, float lng) {}

 @Component
 class SimpleUserClient {

   private final RestClient http;

   SimpleUserClient(RestClient.Builder builder) {
     this.http = builder.build();
   }

   private final ParameterizedTypeReference<List<User>> typeRef =
       new ParameterizedTypeReference<List<User>>() {};

   Collection<User> users() {
     return this.http
         .get()
         .uri("https://jsonplaceholder.typicode.com/users")
         .retrieve()
         .body(typeRef);
   }
 }

@Service
class UserService extends UsersServiceGrpc.UsersServiceImplBase {
  private final SimpleUserClient userClient;

  public UserService(SimpleUserClient userClient) {
    this.userClient = userClient;
  }

  @Override
  public void all(Empty request, StreamObserver<Users> responseObserver) {
    var all = this.userClient.users()
            .stream()
            .map(u->com.example.web.grpc.User.newBuilder()
                    .setUsername(u.username())
            .setName(u.name())
            .setId(u.id()).build()).toList();
    responseObserver.onNext(Users.newBuilder().addAllUsers(all).build());
    responseObserver.onCompleted();
  }
}

@Component
class UserModelAssembler implements RepresentationModelAssembler<User, EntityModel<User>> {

  @Override
  public EntityModel<User> toModel(User entity) {
    var controller = HateoasUserController.class;
    var self = linkTo(methodOn(controller).all()).withRel("all");
    var one = linkTo(methodOn(controller).one(entity.id())).withSelfRel();
    return EntityModel.of(entity, self, one);
  }
}

@Controller
class GraphqlController {
  private final DeclarativeUsersClient userClient;

  public GraphqlController(DeclarativeUsersClient userClient) {
    this.userClient = userClient;
  }

  @QueryMapping
  Collection<User> users() {
    return userClient.users();
  }

  @BatchMapping
  Map<User, Address> address(Collection<User> users) {
    var addresses = new HashMap<User, Address>();
    for (User user : users) {
      addresses.put(user, user.address());
    }
    return addresses;
  }

//  @SchemaMapping
//  Address address(User user) {
//    System.out.println("returning address for user " + user.name());
//    return user.address();
//  }
}

@Controller
class MvcController {
  private final DeclarativeUsersClient usersClient;

  public MvcController(DeclarativeUsersClient usersClient) {
    this.usersClient = usersClient;
  }

  @GetMapping("/users.html")
  String users(Model model) {
    model.addAttribute("users", usersClient.users());
    return "users";
  }
}

@Controller
@ResponseBody
class HateoasUserController {
  private final DeclarativeUsersClient usersClient;

  private final UserModelAssembler userModelAssembler;

  HateoasUserController(DeclarativeUsersClient usersClient, UserModelAssembler userModelAssembler) {
    this.usersClient = usersClient;
    this.userModelAssembler = userModelAssembler;
  }

  @GetMapping("/users/{id}")
  EntityModel<User> one(@PathVariable("id") Integer id) {
    return this.userModelAssembler.toModel(usersClient.user(id));
  }

  @GetMapping("/users")
  CollectionModel<EntityModel<User>> all() {
    return this.userModelAssembler.toCollectionModel(usersClient.users());
  }

  // normal endpoint
  Collection<User> users() {
    return usersClient.users();
  }
}
