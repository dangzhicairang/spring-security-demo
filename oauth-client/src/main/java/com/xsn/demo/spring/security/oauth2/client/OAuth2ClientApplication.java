package com.xsn.demo.spring.security.oauth2.client;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.annotation.RegisteredOAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.endpoint.DefaultClientCredentialsTokenResponseClient;
import org.springframework.security.oauth2.client.endpoint.DefaultPasswordTokenResponseClient;
import org.springframework.security.oauth2.client.endpoint.OAuth2ClientCredentialsGrantRequest;
import org.springframework.security.oauth2.client.endpoint.OAuth2PasswordGrantRequest;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.core.OAuth2AccessToken;
import org.springframework.security.oauth2.core.endpoint.OAuth2AccessTokenResponse;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.Optional;

@SpringBootApplication
@RestController
public class OAuth2ClientApplication {

    public static void main(String[] args) {
        SpringApplication.run(OAuth2ClientApplication.class, args);
    }

    @RequestMapping(value = "/callback", method = RequestMethod.GET)
    public String callback(String code) {
        return "code: " + code;
    }

    @RequestMapping(value = "/test", method = RequestMethod.GET)
    public String test() {
        return "hello security";
    }

    @Autowired
    RestTemplate restTemplate;

    @RequestMapping(value = "/message", method = RequestMethod.GET)
    public String message(
            @RegisteredOAuth2AuthorizedClient("messaging-client-id")
                    OAuth2AuthorizedClient authorizedClient
    ) {
        OAuth2AccessToken accessToken = authorizedClient.getAccessToken();
        String tokenValue = accessToken.getTokenValue();

        String uriTemplate = "http://localhost:8090/messages";
        URI uri = UriComponentsBuilder.fromUriString(uriTemplate).build().toUri();
        RequestEntity<Void> requestEntity = RequestEntity.get(uri)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + tokenValue)
                .build();
        ResponseEntity<String> response = restTemplate.exchange(requestEntity, String.class);

        return Optional.ofNullable(response)
                .map(ResponseEntity::getBody)
                .orElse("empty");
    }

    @Configuration
    static class RestTemplateConfiguration {

        @Bean
        public RestTemplate restTemplate(RestTemplateBuilder builder) {
            return builder.build();
        }
    }

    @Autowired
    ClientRegistrationRepository repository;

    @RequestMapping(value = "/getToken", method = RequestMethod.GET)
    public String getToken() {
        ClientRegistration secret = repository.findByRegistrationId("messaging-client-id3");
        OAuth2ClientCredentialsGrantRequest request =
                new OAuth2ClientCredentialsGrantRequest(secret);
        DefaultClientCredentialsTokenResponseClient client = new DefaultClientCredentialsTokenResponseClient();
        OAuth2AccessTokenResponse tokenResponse = client.getTokenResponse(request);
        return tokenResponse.getAccessToken().getTokenValue();
    }
}
