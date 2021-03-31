package com.godx.cloud.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/test")
public class ApiController {

    @GetMapping("/hello")
    public String getText() {
        return "Hello World.";
    }

    @GetMapping("/order")
    public String getOrder() {
        return "Order.";
    }
}
