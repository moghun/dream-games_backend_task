package com.dreamGames.rowMatchBackend.service;

import com.dreamGames.rowMatchBackend.model.User;
import com.dreamGames.rowMatchBackend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@RequiredArgsConstructor
public class UserProgressService implements UserProgressServiceInterface{

    private final UserRepository userRepository;
    @Override
    public User createUser(String username, String password) {
        User user = new User(username, password);
        return userRepository.save(user);
    }

    @Override
    public Optional<User> findUser(Long userID) {
        return userRepository.findById(userID);
    }

    @Override
    public Optional<User> updateUserProgress(User user, Integer updatedLevel, Integer updatedCoin) {
        user.setCurrentLevel(updatedLevel);
        user.setCurrentCoins(updatedCoin);
        return Optional.of(userRepository.save(user));
    }
}
