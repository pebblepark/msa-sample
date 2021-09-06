package com.example.userservice.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.core.env.Environment;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class UserController {

    private final Environment env;

    @RequestMapping("/user-service/health-check")
    public String healthCheck() {
        return String.format("USER-SERVICE running on Port %s", env.getProperty("local.server.port"));
    }

}
