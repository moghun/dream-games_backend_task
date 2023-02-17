package com.dreamGames.rowMatchBackend.repository;

import com.dreamGames.rowMatchBackend.model.Tournament;
import com.dreamGames.rowMatchBackend.model.TournamentRankInGroup;
import com.dreamGames.rowMatchBackend.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TournamentRankInGroupRepository extends JpaRepository<TournamentRankInGroup, Long> {
    public Optional<TournamentRankInGroup> findByUserAndClaimStatusAndFinalRankBetween(User user, Boolean claimStatus, Integer startRank, Integer endRank);
    public Optional<TournamentRankInGroup> findByUserAndGroup_Tournament(User user, Tournament tournament);

}
