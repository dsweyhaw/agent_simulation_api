package com.uet.agent_simulation_api.security;

import com.uet.agent_simulation_api.security.filters.JwtAuthenticationFilter;
import com.uet.agent_simulation_api.security.providers.AppAuthenticationProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * This class is used to configure security.
 */
@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {
    private final JwtAuthenticationFilter jwtFilter;
    private final AppAccessDeniedHandler appAccessDeniedHandler;
    private final AppAuthenticationProvider appAuthenticationProvider;
    private final AppAuthenticationEntrypoint appAuthenticationEntrypoint;

    @Bean
    public AuthenticationManager authenticationManager(HttpSecurity http) throws Exception {
        return http.getSharedObject(AuthenticationManagerBuilder.class)
                .authenticationProvider(appAuthenticationProvider)
                .build();
    }

    /**
     * This bean is used to configure security.
     *
     * @param http - HttpSecurity
     * @return SecurityFilterChain
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .exceptionHandling(
                        (exceptionHandling) -> exceptionHandling
                                .accessDeniedHandler(appAccessDeniedHandler)
                                .authenticationEntryPoint(appAuthenticationEntrypoint)
                )
                .authorizeHttpRequests((requests) -> requests
                        // Because of frontend doesn't implement authentication feature, we need to permit all requests
                        .requestMatchers(HttpMethod.GET, "/api/**").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/**").permitAll()
                        .requestMatchers(HttpMethod.PUT, "/api/**").permitAll()
                        .requestMatchers(HttpMethod.DELETE, "/api/**").permitAll()

                        .requestMatchers(HttpMethod.GET, "/actuator/**").permitAll()

                        // Auth endpoints - public
//                        .requestMatchers(HttpMethod.POST, "/api/v1/auth/login").permitAll()
//                        .requestMatchers(HttpMethod.POST, "/api/v1/auth/register").permitAll()
//                        .requestMatchers(HttpMethod.POST, "/api/v1/auth/refresh").permitAll()
//
//                        // Health check
//                        .requestMatchers(HttpMethod.GET, "/api/v1/health").permitAll()
//
//                         .requestMatchers(HttpMethod.GET, "/api/v1/experiment_result_images/multi_experiment_animation/**").permitAll()
//
//                        .requestMatchers(HttpMethod.GET, "/api/v1/experiment_results/{id}/download").permitAll()
//
//                        .requestMatchers(HttpMethod.GET, "/api/v1/metrics/**").permitAll()
//
//                        .requestMatchers(HttpMethod.GET, "/actuator/**").permitAll()

                        .anyRequest().authenticated()
                )
                .sessionManagement(sess -> sess.sessionCreationPolicy(SessionCreationPolicy.STATELESS));

        http.addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}
