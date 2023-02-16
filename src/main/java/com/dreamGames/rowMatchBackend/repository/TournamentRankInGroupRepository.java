package com.dreamGames.rowMatchBackend.repository;

import com.dreamGames.rowMatchBackend.model.TournamentRankInGroup;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TournamentRankInGroupRepository extends JpaRepository<TournamentRankInGroup, Long> {
}
