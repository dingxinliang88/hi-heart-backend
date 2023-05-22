package com.juzi.heart;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * @author codejuzi
 */
@EnableScheduling
@SpringBootApplication
public class HiHeartApplication {
    public static void main(String[] args) {
        SpringApplication.run(HiHeartApplication.class, args);
    }
}
