package com.xsn.demo.spring.security.oauth2.server;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.ClientAuthenticationMethod;
import org.springframework.security.oauth2.server.authorization.JdbcOAuth2AuthorizationConsentService;
import org.springframework.security.oauth2.server.authorization.JdbcOAuth2AuthorizationService;
import org.springframework.security.oauth2.server.authorization.OAuth2AuthorizationConsentService;
import org.springframework.security.oauth2.server.authorization.OAuth2AuthorizationService;
import org.springframework.security.oauth2.server.authorization.client.JdbcRegisteredClientRepository;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClient;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClientRepository;
import org.springframework.security.oauth2.server.authorization.config.ProviderSettings;

@Configuration
public class OauthServerConfig {

    @Autowired
    JdbcTemplate jdbcTemplate;

    @Bean
    RegisteredClientRepository registeredClientRepository() {
        JdbcRegisteredClientRepository registeredClientRepository
                = new JdbcRegisteredClientRepository(jdbcTemplate);

        RegisteredClient client1 = RegisteredClient.withId("test1")
                .clientName("测试1")
                .clientSecret("test1")
                .clientAuthenticationMethod(ClientAuthenticationMethod.CLIENT_SECRET_BASIC)
                .authorizationGrantType(AuthorizationGrantType.AUTHORIZATION_CODE)
                .authorizationGrantType(AuthorizationGrantType.REFRESH_TOKEN)
                .redirectUri("http://localhost:8080/redirect")
                .scope("scope:test1")
                .build();
        RegisteredClient client2 = RegisteredClient.withId("test2")
                .clientName("测试2")
                .clientSecret("test2")
                .clientAuthenticationMethod(ClientAuthenticationMethod.CLIENT_SECRET_BASIC)
                .authorizationGrantType(AuthorizationGrantType.JWT_BEARER)
                .redirectUri("http://localhost:8080/redirect")
                .scope("scope:test2")
                .build();

        registeredClientRepository.save(client1);
        registeredClientRepository.save(client2);
        return registeredClientRepository;
    }

    @Bean
    public OAuth2AuthorizationService authorizationService(
            JdbcTemplate jdbcTemplate
            , RegisteredClientRepository registeredClientRepository
    ) {
        return new JdbcOAuth2AuthorizationService(
                jdbcTemplate, registeredClientRepository
        );
    }

    @Bean
    public OAuth2AuthorizationConsentService authorizationConsentService(
            JdbcTemplate jdbcTemplate
            , RegisteredClientRepository registeredClientRepository
    ) {
        return new JdbcOAuth2AuthorizationConsentService(
                jdbcTemplate, registeredClientRepository
        );
    }

    @Bean
    public ProviderSettings providerSettings() {
        return ProviderSettings.builder().issuer("http://localhost:9000").build();
    }
}
