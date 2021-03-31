package com.godx.cloud.service;

import com.godx.cloud.model.CommonResult;
import com.godx.cloud.model.User;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.HashMap;
import java.util.Map;

@FeignClient("auth-service")
@Component
public interface UserService {

    @GetMapping("/oauth/user/{id}")
    public CommonResult GetUser(@PathVariable("id") int id);
}
