package com.xsn.demo.spring.security.resource.server;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication
@RestController
public class ResourceServerApplication {

    public static void main(String[] args) {
        SpringApplication.run(ResourceServerApplication.class, args);
    }

    @GetMapping("/test")
    public String test() {
        return "hello world";
    }

    @GetMapping("/protectedResource")
    public String protectedResource() {
        return "resource protected";
    }
}
