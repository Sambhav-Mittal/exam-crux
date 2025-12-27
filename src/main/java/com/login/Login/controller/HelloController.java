package com.login.Login.controller;

import com.login.Login.dto.LoginResponse;
import com.login.Login.entities.User;
import com.login.Login.security.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;

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

