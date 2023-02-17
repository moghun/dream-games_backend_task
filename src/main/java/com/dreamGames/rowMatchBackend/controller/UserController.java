package com.dreamGames.rowMatchBackend.controller;

import com.dreamGames.rowMatchBackend.model.User;
import com.dreamGames.rowMatchBackend.requests.UserRequest;
import com.dreamGames.rowMatchBackend.responses.AuthenticationResponse;
import com.dreamGames.rowMatchBackend.responses.CreateUserResponse;
import com.dreamGames.rowMatchBackend.responses.OneLevelProgressResponse;
import com.dreamGames.rowMatchBackend.security.JWTTokenProvider;
import com.dreamGames.rowMatchBackend.service.TournamentService;
import com.dreamGames.rowMatchBackend.service.UserDetailsServiceImplementation;
import com.dreamGames.rowMatchBackend.service.UserProgressService;
import org.springframework.security.crypto.password.PasswordEncoder;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;

@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserController {
    private final UserProgressService userService;
    private final TournamentService tournamentService;
    private final AuthenticationManager authenticationManager;
    private final PasswordEncoder passwordEncoder;
    private final JWTTokenProvider JWTTokenProvider;
    private final UserDetailsServiceImplementation userDetailsServiceImplementation;

    @PostMapping("/CreateUserRequest")
    public ResponseEntity<?> create(@Valid @RequestBody UserRequest userRequest) {
        User user = userService.createUser(userRequest.getUsername(), passwordEncoder.encode(userRequest.getPassword()));

        CreateUserResponse response = new CreateUserResponse();
        response.setUserID(user.getID());
        response.setLevel(user.getCurrentLevel());
        response.setCoin(user.getCurrentCoins());

        return new ResponseEntity<CreateUserResponse>(response, HttpStatus.CREATED);
    }

    @PostMapping("/UpdateLevelRequest")
    public ResponseEntity<?> progress() {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        User updatedUser = userService.oneLevelUserProgress(user).get();
        try{
            Boolean x = tournamentService.updateUserActiveTournamentRanking(user);
            System.out.println(x);
        }
        catch (RuntimeException e) {System.out.println(e);}
        OneLevelProgressResponse response = new OneLevelProgressResponse();
        response.setLevel(updatedUser.getCurrentLevel());
        response.setCoin(updatedUser.getCurrentCoins());

        return new ResponseEntity<OneLevelProgressResponse>(response, HttpStatus.OK);
    }

    @PostMapping("/AuthenticateUserRequest")
    public ResponseEntity<?> auth(@Valid @RequestBody UserRequest userRequest) throws Exception {
        UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(userRequest.getUsername(), userRequest.getPassword());
        Authentication auth = authenticationManager.authenticate(authToken);
        SecurityContextHolder.getContext().setAuthentication(auth);
        String jwtToken = JWTTokenProvider.generateJWTToken(auth);

        AuthenticationResponse response = new AuthenticationResponse();
        response.setJwtToken(jwtToken);
        response.setUsername(userRequest.getUsername());

        return new ResponseEntity<AuthenticationResponse>(response, HttpStatus.CREATED);
    }
}