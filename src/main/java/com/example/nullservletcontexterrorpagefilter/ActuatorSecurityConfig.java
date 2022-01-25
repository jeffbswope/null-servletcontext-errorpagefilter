package com.example.nullservletcontexterrorpagefilter;

import org.springframework.boot.actuate.autoconfigure.security.servlet.EndpointRequest;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;

/**
 * Separated security configuration for actuator requests in a library where
 * we ensure consistent actuator behavior/authentication.
 *
 * Including this {@link WebSecurityConfigurerAdapter}, in addition to the application's own
 * results in a second {@link org.springframework.boot.security.servlet.ApplicationContextRequestMatcher}
 * delegate in {@link org.springframework.security.web.access.RequestMatcherDelegatingWebInvocationPrivilegeEvaluator}
 * during which matching the {@link jakarta.servlet.ServletContext} comes up null.
 */
@Configuration
@ConditionalOnWebApplication
public class ActuatorSecurityConfig {

    /**
     * Commenting the @Configuration line below fixes the tests.
     */
    @Configuration
    @Order(-2000)
    @ConditionalOnWebApplication
    public static class ApplicationActuatorSecurityConfigurer extends WebSecurityConfigurerAdapter {
        @Override
        protected void configure(HttpSecurity http) throws Exception {
            http.requestMatcher(EndpointRequest.toAnyEndpoint())
                .authorizeRequests()
                    .requestMatchers(EndpointRequest.to("version", "health")).permitAll()
                    .anyRequest().hasRole("ACTUATOR")
                    .and()
                .sessionManagement()
                    .sessionCreationPolicy(SessionCreationPolicy.NEVER)
                    .and()
                .csrf()
                    .disable()
                .httpBasic();
        }
    }
}
