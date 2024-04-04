package com.example.springbasicsecurity.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Configuration
public class RestAuthenticationEntryPoint {
    @Bean
    public AuthenticationEntryPoint authenticationEntryPoint() {
        return (request, response, authException) -> {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json");

            Map<String, Object> errorDetails = new HashMap<>();
            errorDetails.put("timestamp", new Date(System.currentTimeMillis()));
            errorDetails.put("status", HttpServletResponse.SC_UNAUTHORIZED);
            errorDetails.put("error", "Unauthorized");
            errorDetails.put("message", "Unauthorized");
            errorDetails.put("path", request.getServletPath()); // Use getRequestURI() to get the correct path

            try (OutputStream out = response.getOutputStream()) {
                ObjectMapper mapper = new ObjectMapper();
                mapper.writeValue(out, errorDetails);
                out.flush();
            } catch (IOException e) {
                throw new ServletException("Failed to write to response", e);
            }
        };
    }
}
