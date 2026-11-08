package com.prestige.renovation.common.security;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import com.prestige.renovation.entity.User;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * Reads the {@code Authorization: Bearer <token>} header. A valid token whose user is still ACTIVE and not
 * deleted becomes the authenticated principal. Because tokens are stateless, the user's current state is
 * checked on every request, so a suspended or deleted user's old token stops working immediately (FR-AUTH-4).
 * Invalid tokens are ignored: the request continues as a visitor, and protected endpoints then return 401.
 * Created by {@link SecurityConfig} (not a bean) so it only runs inside the security filter chain.
 */
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;

    public JwtAuthenticationFilter(JwtService jwtService) {
        this.jwtService = jwtService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {
        String header = request.getHeader("Authorization");
        if (header != null && header.startsWith("Bearer ")) {
            Optional<AuthUser> parsed = jwtService.parse(header.substring(7).trim());
            parsed.flatMap(p -> User.findById(p.id()))
                    .filter(User::isActive)
                    .ifPresent(user -> {
                        AuthUser principal = new AuthUser(user.getId(), user.getEmail(), user.getRole());
                        var auth = new UsernamePasswordAuthenticationToken(principal, null,
                                List.of(new SimpleGrantedAuthority("ROLE_" + user.getRole().name())));
                        SecurityContextHolder.getContext().setAuthentication(auth);
                    });
        }
        chain.doFilter(request, response);
    }
}
