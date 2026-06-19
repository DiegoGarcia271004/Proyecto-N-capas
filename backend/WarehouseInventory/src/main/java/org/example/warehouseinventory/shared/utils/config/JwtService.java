package org.example.warehouseinventory.shared.utils.config;

import jakarta.servlet.http.Cookie;
import lombok.RequiredArgsConstructor;
import org.example.warehouseinventory.auth.domain.entity.User;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
@RequiredArgsConstructor
public class JwtService {

    private final JwtEncoder jwtEncoder;

    @Value("${jwt.expiration-minutes:60}")
    private Long expirationMinutes;

    @Value("${jwt.cookie-secure}")
    private boolean cookieSecure;

    public String generateToken(User user) {
        Instant now = Instant.now();

        JwtClaimsSet claims = JwtClaimsSet.builder()
                .issuer("warehouse-app")
                .issuedAt(now)
                .expiresAt(now.plusSeconds(expirationMinutes * 60))
                .subject(user.getUsername())
                .claim("role", user.getRole().name())
                .build();

        return jwtEncoder.encode(JwtEncoderParameters.from(claims)).getTokenValue();
    }

    public Cookie createTokenCookie(String token) {
        Cookie cookie = new Cookie("access_token", token);
        cookie.setHttpOnly(true);
        cookie.setSecure(cookieSecure);
        cookie.setPath("/");
        cookie.setMaxAge((int) (expirationMinutes * 60));
        return cookie;
    }

    public Cookie clearTokenCookie() {
        Cookie cookie = new Cookie("access_token", "");
        cookie.setHttpOnly(true);
        cookie.setSecure(cookieSecure);
        cookie.setPath("/");
        cookie.setMaxAge(0);
        return cookie;
    }

    public String extractUsername(Jwt jwt) {
        return jwt.getSubject();
    }

    public String extractRole(Jwt jwt) {
        return jwt.getClaimAsString("role");
    }
}
