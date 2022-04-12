package com.xsn.demo.spring.security.resource.server.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

import java.util.Arrays;

@Configuration
public class ResourceServerConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity security) throws Exception {
        return security
                .authorizeRequests(
                        request -> request
                                .mvcMatchers("/test").permitAll()
                                .anyRequest().authenticated()
                )
                .oauth2ResourceServer().jwt()
                .and()
                .and()
                .build();
    }
}
