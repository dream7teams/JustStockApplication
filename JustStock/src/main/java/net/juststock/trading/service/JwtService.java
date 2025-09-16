package net.juststock.trading.service;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import net.juststock.trading.domain.admin.AdminProfile;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.time.OffsetDateTime;
import java.util.Date;
import java.util.Map;

@Service
public class JwtService {

    private final Key key;
    private final int accessMinutes;
    private final int refreshDays;

    public JwtService(
            @Value("${app.jwt.secret}") String secret,
            @Value("${app.jwt.accessMinutes}") int accessMinutes,
            @Value("${app.jwt.refreshDays}") int refreshDays) {
        this.key = Keys.hmacShaKeyFor(secret.getBytes());
        this.accessMinutes = accessMinutes;
        this.refreshDays = refreshDays;
    }

    /** ✅ Create JWT access token */
    public String createAccessToken(String subject, Map<String, Object> claims) {
        OffsetDateTime now = OffsetDateTime.now();
        return Jwts.builder()
                .setSubject(subject)
                .addClaims(claims)
                .setIssuedAt(Date.from(now.toInstant()))
                .setExpiration(Date.from(now.plusMinutes(accessMinutes).toInstant()))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    /** ✅ Create JWT refresh token */
    public String createRefreshToken(String subject, int version) {
        OffsetDateTime now = OffsetDateTime.now();
        return Jwts.builder()
                .setSubject(subject)
                .claim("ver", version)
                .claim("typ", "refresh")
                .setIssuedAt(Date.from(now.toInstant()))
                .setExpiration(Date.from(now.plusDays(refreshDays).toInstant()))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    /** ✅ Generate token for AdminProfile */
    public String generateToken(AdminProfile admin) {
        Map<String, Object> claims = Map.of(
                "adminId", admin.getId(),
                "email", admin.getEmail()
        );
        return this.createAccessToken(admin.getEmail(), claims);
    }

    /** ✅ Parse and validate JWT, returns Jws<Claims> */
    public Jws<Claims> parse(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token);
    }

    /** ✅ Validate token and return claims body */
    public Claims extractClaims(String token) {
        return parse(token).getBody();
    }

    /** ✅ Check if token is expired */
    public boolean isExpired(String token) {
        try {
            return extractClaims(token).getExpiration().before(new Date());
        } catch (JwtException e) {
            return true;
        }
    }
}
