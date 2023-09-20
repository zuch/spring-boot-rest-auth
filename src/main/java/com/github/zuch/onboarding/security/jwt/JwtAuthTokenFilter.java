package com.github.zuch.onboarding.security.jwt;

import com.github.zuch.onboarding.security.service.UserDetailsServiceImpl;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class JwtAuthTokenFilter extends OncePerRequestFilter {

    private final JwtUtils jwtUtils;
    private final UserDetailsServiceImpl userDetailsService;

    @Override
    protected void doFilterInternal(
            @NonNull final HttpServletRequest request, @NonNull final HttpServletResponse response,
            @NonNull final FilterChain filterChain) throws ServletException, IOException {
        try {
            final String jwt = jwtUtils.parseJwt(request);
            if (jwt != null && jwtUtils.validateJwtToken(jwt)) {
                final String username = jwtUtils.getUserNameFromJwtToken(jwt);
                final UserDetails userDetails = userDetailsService.loadUserByUsername(username);
                final UsernamePasswordAuthenticationToken authentication =
                        new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        } catch (final Exception e) {
            logger.error("Cannot set user authentication", e);
        }

        filterChain.doFilter(request, response);
    }
}
