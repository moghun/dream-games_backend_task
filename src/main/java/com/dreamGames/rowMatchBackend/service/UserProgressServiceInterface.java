package com.dreamGames.rowMatchBackend.service;

import com.dreamGames.rowMatchBackend.model.User;

import java.util.List;
import java.util.Optional;

public interface UserProgressServiceInterface {
    User createUser(String username, String password);
    Optional<User> findUser(Long userID);
    List getUserProgress(User user);
    User updateUserProgress(User user, Integer updatedLevel, Integer updatedCoin);

    Optional<User> oneLevelUserProgress(User user);

}
