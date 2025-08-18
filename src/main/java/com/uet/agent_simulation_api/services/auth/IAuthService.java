package com.uet.agent_simulation_api.services.auth;

import com.uet.agent_simulation_api.models.AppUser;
import com.uet.agent_simulation_api.requests.auth.LoginRequest;
import com.uet.agent_simulation_api.requests.auth.RefreshRequest;
import com.uet.agent_simulation_api.requests.auth.RegisterRequest;
import com.uet.agent_simulation_api.responses.auth.GetMeResponse;
import com.uet.agent_simulation_api.responses.auth.LoginResponse;
import com.uet.agent_simulation_api.responses.auth.RefreshResponse;
import com.uet.agent_simulation_api.responses.auth.RegisterResponse;

import java.math.BigInteger;

public interface IAuthService {
    /**
     * This method is used to get current user id.
     *
     * @return BigInteger
     */
    BigInteger getCurrentUserId();

    /**
     * This method is used to perform login.
     *
     * @param request LoginRequest
     *
     * @return LoginResponse
     */
    LoginResponse login(LoginRequest request);

    /**
     * This method is used to perform registration.
     *
     * @param request RegisterRequest
     *
     * @return RegisterResponse
     */
    RegisterResponse register(RegisterRequest request);

    /**
     * This method is used to get current authenticated user.
     *
     * @return GetMeResponse
     */
    GetMeResponse getMe();

    /**
     * This method is used to refresh token.
     *
     * @param request RefreshRequest
     *
     * @return RefreshResponse
     */
    RefreshResponse refresh(RefreshRequest request);

    /**
     * This method is used to get current logged in user.
     *
     * @return boolean
     */
    AppUser getCurrentUser();
}
