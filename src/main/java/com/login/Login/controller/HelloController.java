package com.login.Login.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;

@RestController
@RequestMapping("api")
public class HelloController {

    @GetMapping("/jwt")
    public String hello(@AuthenticationPrincipal UserDetails user) {
        return "{\n \"username\": " + user.getUsername() + "\n}";
    }
}

