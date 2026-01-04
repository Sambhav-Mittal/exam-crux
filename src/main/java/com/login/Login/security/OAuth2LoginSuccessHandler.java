package com.login.Login.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.login.Login.dto.LoginResponse;
import com.login.Login.entities.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.core.OAuth2AccessToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;

@Component
public class OAuth2LoginSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    @Autowired
    JwtUtil jwtUtil;
    @Autowired
    OAuth2AuthorizedClientService authorizedClientService;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException {
        OAuth2User oauth2User = (OAuth2User) authentication.getPrincipal();
        String jwt = jwtUtil.generateJwtToken(oauth2User);

        User user = jwtUtil.getUserFromToken(jwt);
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());

        String json = mapper.writeValueAsString(Map.of(
                "jwtToken", jwt,
                "user", user
        ));
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write(json);


    }

//        response.sendRedirect("http://localhost:8080/api/jwt");
    public String generateJwtTokenAfterLogin(String code) {
        OAuth2User oauth2User = (OAuth2User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        // Now you can generate the JWT token using OAuth2User info
        return jwtUtil.generateJwtToken(oauth2User);  // Generate JWT token using the user's details
    }

    private OAuth2AccessToken getAccessToken(String registrationId) {
        // Retrieve the OAuth2AuthorizedClient for the current logged-in user
        OAuth2AuthorizedClient authorizedClient = authorizedClientService.loadAuthorizedClient(registrationId, SecurityContextHolder.getContext().getAuthentication().getName());

        if (authorizedClient != null) {
            return authorizedClient.getAccessToken();  // Return the access token
        }

        throw new IllegalStateException("No authorized client found for registration: " + registrationId);
    }
}

