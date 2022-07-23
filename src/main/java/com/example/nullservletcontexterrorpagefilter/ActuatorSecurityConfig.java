package com.example.nullservletcontexterrorpagefilter;

import org.springframework.boot.actuate.autoconfigure.security.servlet.EndpointRequest;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;

/**
 * Separated security configuration for actuator requests, actually located in a library where
 * we ensure consistent actuator behavior/authentication.
 *
 * Including this {@link SecurityFilterChain}, in addition to the application's own
 * occasionally results in buggy error behavior.
 */
@Configuration
@ConditionalOnWebApplication
public class ActuatorSecurityConfig {

    @Bean
    @Order(-2000)
    public SecurityFilterChain actuatorSecurityFilterChain(HttpSecurity http) throws Exception {
        http.requestMatcher(EndpointRequest.toAnyEndpoint());
        http.authorizeRequests(authorize -> authorize
                .requestMatchers(EndpointRequest.to("health")).permitAll()
                .anyRequest().hasRole("ACTUATOR")
        );
        http.sessionManagement(session -> session
                .sessionCreationPolicy(SessionCreationPolicy.NEVER)
        );
        http.csrf().disable();
        http.httpBasic();
        return http.build();
    }
}
