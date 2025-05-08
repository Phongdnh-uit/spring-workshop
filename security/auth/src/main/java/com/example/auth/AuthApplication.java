package com.example.auth;

import java.security.Principal;
import java.util.Map;
import javax.sql.DataSource;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsPasswordService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.server.authorization.config.annotation.web.configurers.OAuth2AuthorizationServerConfigurer;
import org.springframework.security.provisioning.JdbcUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication
public class AuthApplication {

  public static void main(String[] args) {
    SpringApplication.run(AuthApplication.class, args);
  }

  @Bean
  PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
  }

  // @Bean
  // InMemoryUserDetailsManager inMemoryUserDetailsManager(PasswordEncoder pw) {
  //   var encodedPassword = pw.encode("password");
  //   System.out.println("Encoded password: " + encodedPassword);
  //   var users =
  //       Set.of(
  //           User.withUsername("user").roles("USER").password(encodedPassword).build(),
  //           User.withUsername("admin").roles("USER", "ADMIN").password(encodedPassword).build());
  //
  //   return new InMemoryUserDetailsManager(users);
  // }

  @Bean
  JdbcUserDetailsManager dataSourceUserDetailsManager(DataSource dataSource) {
    return new JdbcUserDetailsManager(dataSource);
  }

  @Bean
  UserDetailsPasswordService userDetailsPasswordService(JdbcUserDetailsManager userDetailsManager) {
    return new UserDetailsPasswordService() {
      @Override
      public UserDetails updatePassword(UserDetails user, String newPassword) {
        var updatedUser = User.withUserDetails(user).password(newPassword).build();
        userDetailsManager.updateUser(updatedUser);
        return updatedUser;
      }
    };
  }

  @Bean
  SecurityFilterChain mSecurityFilterChain(HttpSecurity http) throws Exception {
    return http.authorizeHttpRequests(auth -> auth.anyRequest().authenticated())
        .with(
            OAuth2AuthorizationServerConfigurer.authorizationServer(),
            as -> as.oidc(Customizer.withDefaults()))
        .formLogin(Customizer.withDefaults())
        .webAuthn(
            wa ->
                wa.allowedOrigins("http://localhost:9090")
                    .rpName("WebAuthn Demo")
                    .rpId("localhost"))
        .oneTimeTokenLogin(
            ott -> {
              ott.tokenGenerationSuccessHandler(
                  (request, response, oneTimeToken) -> {
                    System.out.println(
                        "please visit http://localhost:9090/login/ott?token="
                            + oneTimeToken.getTokenValue());
                    response.setHeader(HttpHeaders.CONTENT_TYPE, MediaType.TEXT_PLAIN_VALUE);
                    response.getWriter().println("You've got a one-time token at gmail console");
                  });
            })
        .build();
  }
}

@RestController
class AuthController {

  @GetMapping("/test")
  public Map<String, Object> user(Principal principal) {
    return Map.of(
        "name", principal.getName(),
        "authorities", principal.toString(),
        "details", principal);
  }
}
