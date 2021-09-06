package com.example.firstservice;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/first-service")
@Slf4j
public class FirstServiceController {

    @Autowired
    private Environment env;

    @GetMapping("/welcome")
    public String welcome() {
        return "welcome to the First service";
    }

    @GetMapping("/message")
    public String message(@RequestHeader("first-request") String header) {
        log.info(header);
        return "Hello World in First service";
    }

    @GetMapping("/check")
    public String message(HttpServletRequest request) {
        log.info("Server port={}", request.getServerPort());
        return String.format("First Service Port %s", env.getProperty("local.server.port"));
    }

}
