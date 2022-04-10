package com.xsn.demo.spring.security.oauth2.client;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class OAuth2ClientConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity security) throws Exception {
        return security
                .authorizeHttpRequests(
                        request -> request
                                .mvcMatchers("/callback", "/getToken")
                                    .permitAll()
                                .anyRequest()
                                    .authenticated()
                )
                .oauth2Login(Customizer.withDefaults())
                .build();
    }

}
