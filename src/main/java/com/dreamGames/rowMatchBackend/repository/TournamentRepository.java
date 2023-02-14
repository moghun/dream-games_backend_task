package com.dreamGames.rowMatchBackend.repository;

import com.dreamGames.rowMatchBackend.model.Tournament;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TournamentRepository extends JpaRepository<Tournament, Long> {
}
