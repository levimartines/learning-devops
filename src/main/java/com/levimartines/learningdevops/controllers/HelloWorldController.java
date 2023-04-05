package com.levimartines.learningdevops.controllers;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1")
public class HelloWorldController {

    @GetMapping("/hello-world")
    public String privateHelloWorld() {
        return "Hello World! - Authorized from Okta.";
    }

    @GetMapping("/public/hello-world")
    public String publicHelloWorld() {
        return "Hello World!";
    }
}
