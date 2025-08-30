package com.example.demo.controllers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestController {
    private static final Logger log = LoggerFactory.getLogger(TestController.class);

    @GetMapping("/test-splunk")
    public String testSplunk() {
        log.info("Hello Test Splunk! Spring Boot log test");
        return "Event sent to splunk!";
    }

}
