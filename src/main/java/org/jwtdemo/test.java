package org.jwtdemo;

import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.security.interfaces.RSAPublicKey;
import java.time.Instant;
import java.util.Base64;
import java.util.Date;

/**
 * @author mengdanai
 *
 * header
 * {
 *   "alg": "HS256"
 * }
 *
 * playload
 * {
 *   "sub": "Joe"
 * }
 *
 */
public class test {

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
