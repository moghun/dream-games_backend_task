package com.dreamGames.rowMatchBackend.repository;

import com.dreamGames.rowMatchBackend.model.Tournament;
import com.dreamGames.rowMatchBackend.model.TournamentGroup;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TournamentGroupRepository extends JpaRepository<TournamentGroup, Long> {
    public Optional<TournamentGroup> findByTournamentAndCurrMaxLevelAndFilled(Tournament tournament, Integer CurrMaxLevel, Boolean filled);
}
