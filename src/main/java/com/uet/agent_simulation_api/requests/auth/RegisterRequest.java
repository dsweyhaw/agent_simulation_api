package com.uet.agent_simulation_api.requests.auth;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RegisterRequest {
    @NotBlank(message = "fullname is required")
    @Size(min = 2, max = 255, message = "fullname must be between 2 and 255 characters")
    private String fullname;

    @NotBlank(message = "email is required")
    @Email
    @Size(max = 255, message = "email is too long")
    private String email;

    @NotBlank(message = "password is required")
    @Size(min = 8, max = 255, message = "password must be between 8 and 255 characters")
    private String password;
}
