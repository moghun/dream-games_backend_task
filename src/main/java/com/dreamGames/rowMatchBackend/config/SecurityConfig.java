package com.dreamGames.rowMatchBackend.config;

import com.dreamGames.rowMatchBackend.security.JWTAuthenticationEntryPoint;
import com.dreamGames.rowMatchBackend.security.JWTAuthenticationFilter;
import com.dreamGames.rowMatchBackend.service.UserDetailsServiceImplementation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private UserDetailsServiceImplementation userDetailsService;

    private JWTAuthenticationEntryPoint handler;

    public SecurityConfig(UserDetailsServiceImplementation userDetailsService, JWTAuthenticationEntryPoint handler) {
        this.userDetailsService = userDetailsService;
        this.handler = handler;
    }

    @Bean
    public JWTAuthenticationFilter getJWTAuthenticationFilter() {
        return new JWTAuthenticationFilter();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }


    @Bean
    public SecurityFilterChain filterChain(HttpSecurity httpSecurity) throws Exception {
        httpSecurity
                .csrf()
                .disable()
                .exceptionHandling().authenticationEntryPoint(handler).and()
                .authorizeRequests()
                .requestMatchers("/api/user/AuthenticateUserRequest")
                .permitAll()
                .requestMatchers("/api/user/CreateUserRequest")
                .permitAll()
                .anyRequest()
                .authenticated()
                .and()
                .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS);
        httpSecurity.addFilterBefore(getJWTAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class);
        return httpSecurity.build();
    }
}
