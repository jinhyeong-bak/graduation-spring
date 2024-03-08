package com.example.demo.config;

import com.example.demo.repository.AccountRepository;
import com.example.demo.security.CustomUserDetailsService;
import com.example.demo.security.filter.JwtAuthenticationFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfigurer  {

    private final AccountRepository accountRepository;
    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    private static final String whiteList[] = {
            "/login",
            "/logout",
            "/signup",
            "/account/**",
            "/css/**",
            "/*.ico",
            "/error",
            "/v3/api-docs",
            "/v3/api-docs/**",
            "/swagger-resources/**",
            "/swagger-ui/**",
    };

    @Bean
    @Order(0)
    public SecurityFilterChain whiteListFilterChain(HttpSecurity http) throws Exception {
        http = http .cors(cors -> cors.disable())
                .csrf(csrf -> csrf.disable());

        http = http.sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS));

        http.authorizeHttpRequests(authorize ->
                authorize
                        .requestMatchers(whiteList).permitAll()
                        .anyRequest().authenticated()
        );

        return http.build();
    }

    @Bean
    @Order(1)
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        http = http .cors(cors -> cors.disable())
                    .csrf(csrf -> csrf.disable());

        http = http.sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS));

        http = http.addFilterBefore( jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public UserDetailsService userDetailsService() {
        return new CustomUserDetailsService(accountRepository);
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
