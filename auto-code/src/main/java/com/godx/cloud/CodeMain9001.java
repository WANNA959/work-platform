package com.godx.cloud;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient
public class CodeMain9001 {
    public static void main(String[] args) {
        SpringApplication.run(CodeMain9001.class, args);
    }
}
