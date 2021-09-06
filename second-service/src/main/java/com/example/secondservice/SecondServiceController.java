package com.example.secondservice;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/second-service")
@Slf4j
public class SecondServiceController {

    @Autowired
    private Environment env;

    @GetMapping("/welcome")
    public String welcome() {
        return "welcome to the Second service";
    }

    @GetMapping("/message")
    public String message(@RequestHeader("second-request") String header) {
        log.info(header);
        return "Hello World in Second service";
    }

    @GetMapping("/check")
    public String message(HttpServletRequest request) {
        log.info("Server port={}", request.getServerPort());
        return String.format("First Service Port %s", env.getProperty("local.server.port"));
    }
}
