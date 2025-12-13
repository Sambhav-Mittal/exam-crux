package com.login.Login.security;

import com.login.Login.entities.User;
import com.login.Login.exception.TokenBlacklistedException;
import com.login.Login.exception.UserNotFoundException;
import com.login.Login.repository.UserRepository;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class JwtUtil {

    private final String SECRET = "mysecretkeymysecretkeymysecretkeymysecretkey"; // 32+ chars
    private final Key key = Keys.hmacShaKeyFor(SECRET.getBytes());
    private final UserRepository userRepository;
    private final JwtBlacklistService jwtBlacklistService;

    // ------------------ GENERATE TOKEN ------------------
    public String generateToken(String email) {
        String jti = UUID.randomUUID().toString(); // unique token ID for session mapping
        return Jwts.builder()
                .setSubject(email)
                .setId(jti) // JTI claim
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60)) // 1 hour
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
        if (user.getRole() == null || !"ADMIN".equalsIgnoreCase(user.getRole().getName())) {
            throw new RuntimeException("Access denied: Admin privileges required");
        }
    }

    public void ensureAdmin() {
        User user = getAuthenticatedUserFromContext();
        if (user.getRole() == null || !"ADMIN".equalsIgnoreCase(user.getRole().getName())) {
            throw new RuntimeException("Access denied: Admin privileges required");
        }
    }

    // ------------------ HELPERS ------------------
    private String stripBearer(String token) {
        return token.startsWith("Bearer ") ? token.substring(7) : token;
    }
}
