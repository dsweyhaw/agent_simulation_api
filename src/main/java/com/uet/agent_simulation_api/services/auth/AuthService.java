package com.uet.agent_simulation_api.services.auth;

import com.uet.agent_simulation_api.constant.user.Role;
import com.uet.agent_simulation_api.exceptions.auth.UnauthorizedException;
import com.uet.agent_simulation_api.exceptions.errors.CommonErrors;
import com.uet.agent_simulation_api.exceptions.user.EmailAlreadyInUseException;
import com.uet.agent_simulation_api.models.AppUser;
import com.uet.agent_simulation_api.repositories.UserRepository;
import com.uet.agent_simulation_api.requests.auth.LoginRequest;
import com.uet.agent_simulation_api.requests.auth.RefreshRequest;
import com.uet.agent_simulation_api.requests.auth.RegisterRequest;
import com.uet.agent_simulation_api.responses.auth.GetMeResponse;
import com.uet.agent_simulation_api.responses.auth.LoginResponse;
import com.uet.agent_simulation_api.responses.auth.RefreshResponse;
import com.uet.agent_simulation_api.responses.auth.RegisterResponse;
import com.uet.agent_simulation_api.utils.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigInteger;

@Service
@RequiredArgsConstructor
public class AuthService implements IAuthService {
    private final JwtUtil jwtUtil;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;

    @Override
    public BigInteger getCurrentUserId() {
        return BigInteger.valueOf(1);
    }

    @Override
    @Transactional(readOnly = true)
    public LoginResponse login(LoginRequest request) {
        final var userId = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new UsernameNotFoundException(CommonErrors.E0005.defaultMessage())).getId();

        final var token = new UsernamePasswordAuthenticationToken(userId, request.getPassword());

        final var authentication = authenticationManager.authenticate(token);

        final var user = (AppUser) authentication.getPrincipal();

        return new LoginResponse(jwtUtil.generateAccessToken(user.getId()), jwtUtil.generateRefreshToken(user.getId()));
    }

    @Override
    @Transactional(rollbackFor = { Exception.class, Throwable.class })
    public RegisterResponse register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail()))
            throw new EmailAlreadyInUseException("This email is already in use. Please try another one.");

        final var user = AppUser.builder()
                .email(request.getEmail())
                .fullname(request.getFullname())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(Role.USER)
                .createdBy(request.getEmail())
                .build();

        userRepository.save(user);

        return null;
    }

    @Override
    public GetMeResponse getMe() {
        // Get current authenticated user
        final var user = (AppUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        return new GetMeResponse(user.getId(), user.getFullname(), user.getEmail(), user.getRole());
    }

    @Override
    @Transactional(readOnly = true)
    public RefreshResponse refresh(RefreshRequest request) {
        try {
            if (!jwtUtil.isValidToken(request.getRefreshToken()))
                throw new UnauthorizedException("Verification failed");

            final var userId = new BigInteger(jwtUtil.getUserIdFromToken(request.getRefreshToken()));
            if (!userRepository.existsById(userId))
                throw new UnauthorizedException("User not found");

            return new RefreshResponse(
                    jwtUtil.generateAccessToken(userId),
                    jwtUtil.generateRefreshToken(userId)
            );
        } catch (Exception e) {
            throw new UnauthorizedException(CommonErrors.E0003.defaultMessage());
        }
    }

    @Override
    public AppUser getCurrentUser() {
        return (AppUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    }
}
