package com.dreamGames.rowMatchBackend.repository;

import com.dreamGames.rowMatchBackend.model.Tournament;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TournamentRepository extends JpaRepository<Tournament, Long> {
    public Optional<Tournament> findFirstByOrderByIDDesc();
}
