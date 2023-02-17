package com.dreamGames.rowMatchBackend.service;

import com.dreamGames.rowMatchBackend.model.User;
import com.dreamGames.rowMatchBackend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@RequiredArgsConstructor
public class UserProgressService implements UserProgressServiceInterface{
    @Autowired
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
    public ArrayList<Integer> getUserProgress(User user) {
        Integer currentLevel = user.getCurrentLevel();
        Integer currentCoin = user.getCurrentCoins();
        ArrayList<Integer> array = new ArrayList<Integer>();
        array.add(currentLevel);
        array.add(currentCoin);
        return array;
    }

    @Override
    public User updateUserProgress(User user, Integer updatedLevel, Integer updatedCoin) {
        user.setCurrentLevel(updatedLevel);
        user.setCurrentCoins(updatedCoin);
        return userRepository.save(user);
    }

    @Override
    public Optional<User> oneLevelUserProgress(User authUser) {
        User user = userRepository.findByUsername(authUser.getUsername());
        ArrayList<Integer> currentStatus = getUserProgress(user);
        Integer currentLevel = currentStatus.get(0);
        Integer currentCoin = currentStatus.get(1);
        User updatedUser = updateUserProgress(user, currentLevel+1, currentCoin +25);
        return Optional.of(userRepository.save(updatedUser));
    }

}
