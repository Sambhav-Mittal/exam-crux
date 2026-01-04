package com.examCrux.webApp.controller;

import com.examCrux.webApp.dto.LoginResponse;
import com.examCrux.webApp.entities.User;
import com.examCrux.webApp.security.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class HelloController {

    @Autowired
    JwtUtil jwtUtil;

    @GetMapping("/jwt")
    public ResponseEntity<LoginResponse> hello(@RequestHeader("Authorization") String token) {
        User user = jwtUtil.getAuthenticatedUserFromContext();
        LoginResponse response = LoginResponse.builder().token(token.substring(7)).user(user).build();
        return ResponseEntity.ok(response);
    }
}

