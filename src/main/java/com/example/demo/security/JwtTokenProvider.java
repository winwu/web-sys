package com.example.demo.security;

import com.example.demo.entity.Role;
import com.example.demo.exception.CustomException;
import io.jsonwebtoken.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import java.util.*;
import java.util.stream.Collectors;

@Component
public class JwtTokenProvider {
    static final String TOKEN_PREFIX = "Bearer";

    @Value("${security.jwt.token.secret-key}")
    private String key;

    @Value("${security.jwt.token.expire-length}")
    private long EXPIRATION_TIME;

    @Autowired
    private MyUserDetailService myUserDetails;


    public String createToken(String username, List<Role> roles) {
        Claims claims = Jwts.claims().setSubject(username);
        // @TODO: make auth to be ["ROLE_ADMIN", "ROLE_CLIENT"]
        claims.put("auth", roles.stream()
                .map(role -> new SimpleGrantedAuthority(role.getName()))
                .filter(Objects::nonNull)
                .collect(Collectors.toList()));

        System.out.println("???");

//        claims.put("auth", roles.stream()
//                .map(s -> new SimpleGrantedAuthority(s.getAuthority()))
//                .filter(Objects::nonNull)
//                .collect(Collectors.toList()));

        Date now = new Date();
        Date validity = new Date(now.getTime() + EXPIRATION_TIME);
        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(now)
                // 在 payload 放入 exp
                .setExpiration(validity)
                .signWith(SignatureAlgorithm.HS256, key)
                .compact();
    }

    public String getUsername(String token) {
        return Jwts.parser().setSigningKey(key).parseClaimsJws(token).getBody().getSubject();
    }

    public Authentication getAuthentication(String token) {
        UserDetails userDetails = myUserDetails.loadUserByUsername(getUsername(token));
        return new UsernamePasswordAuthenticationToken(userDetails, "", userDetails.getAuthorities());
    }

    public String resolveToken(HttpServletRequest req) {
        String bearerToken = req.getHeader("Authorization");
        // 去掉 Bearer
        if (bearerToken != null && bearerToken.startsWith(TOKEN_PREFIX + " ")) {
            return bearerToken.substring(7);
        }
        return null;
    }

    public boolean validateToken(String token) {
        try {
            Jwts.parser().setSigningKey(key).parseClaimsJws(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            System.out.println("ValidateToken failed");
            System.out.println(e.getMessage());
            throw new CustomException("Expired or invalid jwt token", HttpStatus.UNAUTHORIZED);
        }
    }

    public Map<String, Object> parseToken(String token) {
        Claims claims = Jwts.parser()
                // 驗證
                .setSigningKey(key)
                .parseClaimsJws(token)
                .getBody();
        return claims.entrySet().stream()
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));

    }

}
