package com.xly.marry;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@SpringBootApplication(exclude = {SecurityAutoConfiguration.class})
public class MarryApplication {

    private static final Logger log = LoggerFactory.getLogger(MarryApplication.class);

    public static void main(String[] args) {
        log.info("Starting MarryApplication...");
        SpringApplication.run(MarryApplication.class, args);
        log.info("MarryApplication started successfully.");
    }

}
