package com.example.springbasicsecurity.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Configuration
public class SpringSecurityConfig {
    @Autowired
    private RestAuthenticationEntryPoint restAuthenticationEntryPoint;

    @Autowired
    private EncoderConfig encoderConfig;

    @Autowired
    public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
        auth
                .inMemoryAuthentication()
                .withUser("Admin")
                .password(encoderConfig.passwordEncoder().encode("password"))
                .roles("ADMIN");

        auth
                .inMemoryAuthentication()
                .withUser("User")
                .password(encoderConfig.passwordEncoder().encode("password2"))
                .roles("USER");
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.authorizeHttpRequests(expressionInterceptUrlRegistry -> expressionInterceptUrlRegistry
                        .requestMatchers("/public/**")
                        .permitAll()
                        .requestMatchers("/user/**")
                        .hasAnyRole("USER", "ADMIN")
                        .requestMatchers("/**")
                        .hasRole("ADMIN")
                        .anyRequest()
                        .authenticated())
                .httpBasic(Customizer.withDefaults())
                .exceptionHandling(httpSecurityExceptionHandlingConfigurer -> httpSecurityExceptionHandlingConfigurer
                        .authenticationEntryPoint(restAuthenticationEntryPoint.authenticationEntryPoint()));

        return http.build();
    }
}
