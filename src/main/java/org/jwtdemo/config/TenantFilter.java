package org.jwtdemo.config;

import java.io.IOException;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.jwtdemo.utils.JwtUtilsCustomer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.util.Assert;

import javax.crypto.SecretKey;

/**
 * @author mengdanai
 */
public class TenantFilter implements Filter {

    private static final Logger log = LoggerFactory.getLogger(TenantFilter.class);
    private final UserDetailsService userDetailsService;

    public TenantFilter(UserDetailsService users) {
        this.userDetailsService = users;
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {

        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;

        try {

            JwtUtilsCustomer jwtUtilsCustomer = new JwtUtilsCustomer().extractJwtFromRequest(request, response);
            String username = jwtUtilsCustomer.usernameFromJwt();

            if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                UserDetails userDetails = userDetailsService.loadUserByUsername(username);
                UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                        userDetails, null, userDetails.getAuthorities());
                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authToken);
            }
        } catch (Exception e) {
            log.error(e.getMessage());
        }

        filterChain.doFilter(request, response);
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
