package com.dreamGames.rowMatchBackend.repository;

import com.dreamGames.rowMatchBackend.model.TournamentGroup;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
@Repository
public interface TournamentGroupRepository extends JpaRepository<TournamentGroup, Long> {
}
