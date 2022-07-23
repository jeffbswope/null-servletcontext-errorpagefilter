package com.example.nullservletcontexterrorpagefilter;

import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@ConditionalOnWebApplication
public class WebSecurityConfig {

    @Bean
    public UserDetailsService userDetailsService() {
        final InMemoryUserDetailsManager userDetailsManager = new InMemoryUserDetailsManager();

        userDetailsManager.createUser(
                User.withUsername("fobclient")
                        .password("{noop}fobclient")
                        .roles("FOB_MANAGER")
                        .build());

        userDetailsManager.createUser(
                User.withUsername("other")
                        .password("{noop}other")
                        .roles("OTHER")
                        .build());

        return userDetailsManager;
    }

    @Bean
    public SecurityFilterChain applicationSecurityFilterChain(HttpSecurity http) throws Exception {
        http.authorizeRequests(authorize -> authorize
                .anyRequest().authenticated()
        );
        http.sessionManagement(session -> session
                .sessionCreationPolicy(SessionCreationPolicy.NEVER)
        );
        http.httpBasic();
        return http.build();
    }
}
