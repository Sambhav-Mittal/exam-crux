package com.login.Login.controller.admin;


import com.login.Login.entities.User;
import com.login.Login.exception.InvalidCredentialsException;
import com.login.Login.exception.UserNotFoundException;
import com.login.Login.repository.UserRepository;
import com.login.Login.security.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

@RestController
@RequestMapping("/admin")
public class AdminController {
    @Autowired
    JwtUtil jwtUtil;
    @Autowired
    PasswordEncoder passwordEncoder;
    @Autowired
    UserRepository userRepository;

    @PostMapping("/login")
    public Map<String, String> login(@RequestBody Map<String, String> request) {
        try {
            User user = userRepository.findByUsername(request.get("username"))
                    .orElseThrow(() -> new UserNotFoundException("User not found"));
            if (!passwordEncoder.matches(request.get("password"), user.getPassword())) {
                throw new InvalidCredentialsException("Wrong Password!");
            }
        } catch (Exception e) {
            return Map.of("error", "Invalid username or password");
        }
        String token = jwtUtil.generateToken(request.get("username"));
        return Map.of("token", token);
    }
}
