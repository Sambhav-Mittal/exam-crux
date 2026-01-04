package com.examCrux.webApp.security;

import com.examCrux.webApp.entities.User;
import com.examCrux.webApp.exception.TokenBlacklistedException;
import com.examCrux.webApp.exception.UserNotFoundException;
import com.examCrux.webApp.repository.RoleRepository;
import com.examCrux.webApp.repository.UserRepository;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;
import java.util.Map;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class JwtUtil {

    private final String SECRET = "mysecretkeymysecretkeymysecretkeymysecretkey"; // 32+ chars
    private final Key key = Keys.hmacShaKeyFor(SECRET.getBytes());
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;

    private final JwtBlacklistService jwtBlacklistService;
    private final long JwtExpirationInMs = (1000 * 60 * 60) ;

    // ------------------ GENERATE TOKEN ------------------
    public String generateToken(String email) {
        String jti = UUID.randomUUID().toString(); // unique token ID for session mapping
        return Jwts.builder()
                .setSubject(email)
                .setId(jti) // JTI claim
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + JwtExpirationInMs)) // 1 hour
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    public String generateJwtToken(OAuth2User oauth2User) {
        Map<String, Object> userAttributes = oauth2User.getAttributes();
        String jti = UUID.randomUUID().toString(); // unique token ID for session mapping
        User user = userRepository.findByUsername((String) userAttributes.get("email")).orElse(null);
        if(user==null){
            User registerUser = User.builder()
                    .username((String) userAttributes.get("email"))
                    .profileImage((String) userAttributes.get("picture"))
                    .active(true)
                    .role(roleRepository.findByRoleNameIgnoreCase("USER").orElseThrow(()->new RuntimeException("Role Not Found"))).build();
            userRepository.save(registerUser);
            user = registerUser;
        }else {
            user.setProfileImage((String) userAttributes.get("picture"));
            userRepository.save(user);
        }
        // Create the JWT token
        return Jwts.builder()
                .setSubject(user.getUsername()) // Use the username as the subject
                .setId(jti)
                .claim("role", user.getRole().getRoleName())   // Optionally include user attributes in the JWT
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + JwtExpirationInMs))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    // ------------------ VALIDATE TOKEN ------------------
    public void validateToken(String token) {
        token = stripBearer(token);

        if (jwtBlacklistService.isBlacklisted(token)) {
            String reason = jwtBlacklistService.getBlacklistReason(token);
            throw new TokenBlacklistedException(reason);
        }

        try {
            Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);
        } catch (ExpiredJwtException e) {
            throw new RuntimeException("Token expired");
        } catch (MalformedJwtException | SignatureException e) {
            throw new RuntimeException("Invalid token signature");
        } catch (Exception e) {
            throw new RuntimeException("Invalid token");
        }
    }

    // ------------------ CLAIM EXTRACTION ------------------
    private Claims extractClaims(String token) {
        token = stripBearer(token);
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    public String extractUsername(String token) {
        return extractClaims(token).getSubject();
    }

    public String extractJti(String token) {
        return extractClaims(token).getId();
    }

    public Date getExpirationDate(String token) {
        return extractClaims(token).getExpiration();
    }

    public long getExpirationMillis(String token) {
        return getExpirationDate(token).getTime();
    }

    // ------------------ USER HELPERS ------------------
    public User getUserFromToken(String token) throws UserNotFoundException {
        validateToken(token);
        String email = extractUsername(token);
        return userRepository.findByUsername(email)
                .orElseThrow(() -> new UserNotFoundException("User not found for token"));
    }

    public User getAuthenticatedUserFromContext() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || auth.getName() == null) {
            throw new RuntimeException("Unauthorized access");
        }
        String email = auth.getName();
        return userRepository.findByUsername(email)
                .orElseThrow(() -> new RuntimeException("Authenticated user not found"));
    }

    public void ensureAdminFromToken(String token) throws UserNotFoundException {
        User user = getUserFromToken(token);
        if (user.getRole() == null || !"ADMIN".equalsIgnoreCase(user.getRole().getRoleName())) {
            throw new RuntimeException("Access denied: Admin privileges required");
        }
    }

    public void ensureAdmin() {
        User user = getAuthenticatedUserFromContext();
        if (user.getRole() == null || !"ADMIN".equalsIgnoreCase(user.getRole().getRoleName())) {
            throw new RuntimeException("Access denied: Admin privileges required");
        }
    }

    // ------------------ HELPERS ------------------
    private String stripBearer(String token) {
        return token.startsWith("Bearer ") ? token.substring(7) : token;
    }
}
