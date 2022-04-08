package com.xsn.demo.spring.security.oauth2.client;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

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
    public String message() {

        return restTemplate.getForObject(
                "http://localhost:8090/messages"
                , String.class
        );
    }

    @Configuration
    static class RestTemplateConfiguration {

        @Bean
        public RestTemplate restTemplate(RestTemplateBuilder builder) {
            return builder.build();
        }
    }
}
