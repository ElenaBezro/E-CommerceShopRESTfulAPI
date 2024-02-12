package com.bezro.shopRESTfulAPI.jwtUtils;

import com.bezro.shopRESTfulAPI.entities.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.time.Duration;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class JwtTokenUtils {
    //TODO: put this value in props and get using @Value() or store as a environment variable
    //@Value("${jwt.secret}")
    private static final String SECRET = "secretsecretsecretsecretsecretsecretsecretsecret";
    //TODO: put this value in props and get using @Value()
    //@Value("${jwt.lifetime}")
    private Duration jwtLifetime = Duration.ofMinutes(30);

    public String generateToken(UserDetails userDetails) {
        Map<String, Object> claims = new HashMap<>();
        List<String> rolesList = userDetails.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .toList();
        claims.put("roles", rolesList);
        claims.put("userId", ((User) userDetails).getId());
        //TODO: add id into claims

        Date issuedDate = new Date();
        Date exriredDate = new Date(issuedDate.getTime() + jwtLifetime.toMillis());
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(String.valueOf(((User) userDetails).getId()))
                .setIssuedAt(issuedDate)
                .setExpiration(exriredDate)
                .signWith(getSignKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    private Key getSignKey() {
        byte[] keyBytes = Decoders.BASE64.decode(SECRET);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    public String getUsername(String token) {
        return getAllClaimsFromToken(token).getSubject();
    }

    public Date getExpiration(String token) {
        return getAllClaimsFromToken(token).getExpiration();
    }

    public Boolean isTokenExpired(String token) {
        return getAllClaimsFromToken(token).getExpiration().before(new Date());
    }

    public List<?> getRoles(String token) {
        return getAllClaimsFromToken(token).get("roles", List.class);
    }

    public Long getUserId(String token) {
        return getAllClaimsFromToken(token).get("userId", Long.class);
    }

    public Claims getAllClaimsFromToken (String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSignKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }
}
