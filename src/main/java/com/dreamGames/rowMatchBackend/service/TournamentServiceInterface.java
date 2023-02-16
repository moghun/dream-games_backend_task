package com.dreamGames.rowMatchBackend.service;

import com.dreamGames.rowMatchBackend.model.Tournament;
import com.dreamGames.rowMatchBackend.model.TournamentGroup;
import com.dreamGames.rowMatchBackend.model.User;
import com.dreamGames.rowMatchBackend.responses.CurrentRankResponse;
import com.dreamGames.rowMatchBackend.responses.OneLevelProgressResponse;

import java.util.List;
import java.util.Optional;

public interface TournamentServiceInterface {
    Optional<Tournament> getTournament(Long id);

    Optional<TournamentGroup> getGroup(Long id);

    Tournament getLatestTournament();
    List<?> enterTournament(User user, Tournament tournament);

    Boolean waitingTournament(User user);

    List<?> putLeaderboard(TournamentGroup group, User user, Integer score);

    List<?> listLeaderboard(TournamentGroup group);

    CurrentRankResponse getRankInGivenTournament(Tournament tournament, User user);

    Boolean updateUserActiveTournamentRanking(User user);

    OneLevelProgressResponse claimReward(Tournament tournament, User user);

    Boolean startTournament();

}
