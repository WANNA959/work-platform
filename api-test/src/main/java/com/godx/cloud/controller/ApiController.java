package com.godx.cloud.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ApiController {

    @GetMapping("/api/hello")
    public String getText() {
        return "Hello World.";
    }

    @GetMapping("/api/order")
    public String getOrder() {
        return "Order.";
    }
}
