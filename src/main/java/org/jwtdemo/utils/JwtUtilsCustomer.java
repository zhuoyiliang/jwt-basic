package org.jwtdemo.utils;


import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.Assert;

import javax.crypto.SecretKey;
import java.io.IOException;
import java.util.Base64;

/**
 * @author mengdanai
 */


@Slf4j
public class JwtUtilsCustomer {

    private static final String AUTH_HEADER = "Authorization";
    private static final String TOKEN_PREFIX = "Bearer ";

    private static final SecretKey SECRET_KEY = Keys.hmacShaKeyFor(Decoders.BASE64.decode("EbFLDmJb4sRa9I8h4ub7+QS3Ys16vJAyvMoWv+cRK/0="));

    // private static final SecretKey SECRET_KEY = Jwts.SIG.HS256.key().build();
    // private static final String str = Base64.getEncoder().encodeToString(SECRET_KEY.getEncoded());

    private Jws<Claims> claimsJws;

    public JwtUtilsCustomer extractJwtFromRequest(HttpServletRequest req, HttpServletResponse res) throws IOException {
        try {
            final String authHeader = req.getHeader(AUTH_HEADER);
            if (authHeader != null && authHeader.startsWith(TOKEN_PREFIX)) {
                String token = authHeader.substring(TOKEN_PREFIX.length());
                this.claimsJws = Jwts.parser().verifyWith(SECRET_KEY).build().parseSignedClaims(token);
            } else {
                log.error("Authorization header is missing or invalid");
            }
        } catch (JwtException e) {
            // 记录错误信息
            // 抛出该异常表示向方法传递了非法或不适当的参数
            throw new IllegalArgumentException("Failed to extract JWT from request" + e.getMessage());
        }
        return this;
    }

    public String usernameFromJwt() {
        if (claimsJws != null) {
            return claimsJws.getPayload().getSubject();
        }
        return null;
    }


    public static void main(String[] args) {
        // SecretKey key = Jwts.SIG.HS256.key().build();
        // String str = Base64.getEncoder().encodeToString(key.getEncoded());

        String mykeyString = "EbFLDmJb4sRa9I8h4ub7+QS3Ys16vJAyvMoWv+cRK/0=";
        SecretKey key = Keys.hmacShaKeyFor(Decoders.BASE64.decode(mykeyString));

        String jws = Jwts.builder()
                .subject("Joe")
                .signWith(key)
                .compact();
        System.out.println(jws);

        assert Jwts.parser().verifyWith(key).build().parseSignedClaims(jws).getPayload().getSubject().equals("Joe");
        try {

            Jwts.parser().verifyWith(key).build().parseSignedClaims(jws);
            System.out.println("ok");
        } catch (JwtException e) {
            System.out.println(e.getMessage());
        }
    }
}
