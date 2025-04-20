package com.ashish.projects.VrboApp.security;

import com.ashish.projects.VrboApp.entity.User;
import com.ashish.projects.VrboApp.service.UserService;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.servlet.HandlerExceptionResolver;

import java.io.IOException;

@Configuration
@RequiredArgsConstructor
public class JWTAuthFilter extends OncePerRequestFilter {

    private final JWTService jwtService;
    private final UserService userService;

    @Autowired
    @Qualifier("handlerExceptionResolver")
    private HandlerExceptionResolver handlerExceptionResolver;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        try {
            final String requestTokenHeader = request.getHeader("Authorization");

            // Step 1: Check if header is null or doesn't start properly
            if (requestTokenHeader == null || !requestTokenHeader.startsWith("Bearer ")) {
                filterChain.doFilter(request, response);
                return;
            }

            // Step 2: Extract token safely
            String token = requestTokenHeader.substring(7).trim();

            // Step 3: If token is empty, skip
            if (token.contains(" ")) {
                throw new IllegalArgumentException("Invalid JWT: token contains whitespace");

            }

            // Step 4: Get userId from token
            Long userId = jwtService.getUserIdFromToken(token);

            // Step 5: Set authentication in context if not already present
            if (userId != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                User user = userService.getUserById(userId);

                UsernamePasswordAuthenticationToken authenticationToken =
                        new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities());

                authenticationToken.setDetails(
                        new WebAuthenticationDetailsSource().buildDetails(request)
                );

                SecurityContextHolder.getContext().setAuthentication(authenticationToken);
            }

            filterChain.doFilter(request, response);

        } catch (JwtException | IllegalArgumentException ex) {
            // Handle invalid token gracefully
            handlerExceptionResolver.resolveException(request, response, null, ex);
        }
    }
}
