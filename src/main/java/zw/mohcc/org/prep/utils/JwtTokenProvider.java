package zw.mohcc.org.prep.utils;

import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import org.springframework.security.core.GrantedAuthority;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.stream.Collectors;

@Component
public class JwtTokenProvider {

    private final SecretKey jwtSecret;

    // Injecting the secret key from properties or environment variables
    public JwtTokenProvider(@Value("${jwt.secret}") String secretKey) {
        this.jwtSecret = Keys.hmacShaKeyFor(Decoders.BASE64.decode(secretKey));  // Decodes base64 secret key
    }

    // Generate a token
    public String generateToken(Authentication authentication) {
        String username = authentication.getName();
        Date now = new Date();
        // Token valid for 1 hour
        long jwtExpirationInMs = 3600000;  // 1 hour in milliseconds
        Date expiryDate = new Date(now.getTime() + jwtExpirationInMs);

        // Get user roles
        String roles = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(","));

        return Jwts.builder()
                .setSubject(username)
                .claim("roles", roles)
                .setIssuedAt(new Date())
                .setExpiration(expiryDate)
                .signWith(jwtSecret, SignatureAlgorithm.HS512)  // Use secure key with HS512 algorithm
                .compact();
    }

    // Get username from token
    public String getUsernameFromToken(String token) {
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(jwtSecret)
                .build()
                .parseClaimsJws(token)
                .getBody();
        return claims.getSubject();
    }

    // Validate token
    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder().setSigningKey(jwtSecret).build().parseClaimsJws(token);
            return true;
        } catch (MalformedJwtException | ExpiredJwtException | UnsupportedJwtException | IllegalArgumentException ex) {
            // Optionally log the exceptions for better debugging
            return false;
        }
    }
}
