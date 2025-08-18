package com.uet.agent_simulation_api.security.filters;

import com.uet.agent_simulation_api.models.AppUser;
import com.uet.agent_simulation_api.repositories.UserRepository;
import com.uet.agent_simulation_api.utils.JwtUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetails;
import org.springframework.stereotype.Service;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.math.BigInteger;

@Service
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private final JwtUtil jwtUtil;
    private final UserRepository userRepository;

    @Override
    protected void doFilterInternal(HttpServletRequest req, HttpServletResponse res, FilterChain filterChain) throws ServletException, IOException {
        final String header = req.getHeader("Authorization");
        // Check header.
        if (isInvalidHeader(header)) {
            filterChain.doFilter(req, res);
            return;
        }

        // Validate token.
        final String token = header.substring(7).trim();
        if (!jwtUtil.isValidToken(token)) {
            filterChain.doFilter(req, res);
            return;
        }

        final BigInteger userId = new BigInteger(jwtUtil.getUserIdFromToken(token));
        final AppUser user = userRepository.findById(userId).orElse(null);
        if (user == null) {
            filterChain.doFilter(req, res);
            return;
        }

        final var authToken = new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities());
        authToken.setDetails(new WebAuthenticationDetails(req));
        SecurityContextHolder.getContext().setAuthentication(authToken);

        filterChain.doFilter(req, res);
    }

    /**
     * This method is used check if the header is invalid.
     *
     * @param header String
     * @return boolean
     */
    private boolean isInvalidHeader(String header) {
        return header == null || !header.startsWith("Bearer") || header.length() <= 8;
    }
}
