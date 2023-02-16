package com.dreamGames.rowMatchBackend.service;

import com.dreamGames.rowMatchBackend.model.Tournament;
import com.dreamGames.rowMatchBackend.model.TournamentGroup;
import com.dreamGames.rowMatchBackend.model.User;

import java.util.List;

public interface TournamentServiceInterface {
    Tournament getLatestTournement();

    List<?> enterTournament(User user, Tournament tournament);

    Boolean waitingTournament(User user);

    List<?> putLeaderboard(TournamentGroup group, User user, Integer score);

    List<?> listLeaderboard(TournamentGroup group);
}
