package com.dreamGames.rowMatchBackend.service;

import com.dreamGames.rowMatchBackend.model.Tournament;
import com.dreamGames.rowMatchBackend.model.TournamentGroup;
import com.dreamGames.rowMatchBackend.model.TournamentRankInGroup;
import com.dreamGames.rowMatchBackend.model.User;
import com.dreamGames.rowMatchBackend.repository.TournamentGroupRepository;
import com.dreamGames.rowMatchBackend.repository.TournamentRankInGroupRepository;
import com.dreamGames.rowMatchBackend.repository.TournamentRepository;
import com.dreamGames.rowMatchBackend.requests.UserTuple;
import com.dreamGames.rowMatchBackend.responses.CurrentLeaderboardStatusResponse;
import com.dreamGames.rowMatchBackend.responses.CurrentRankResponse;
import jakarta.annotation.Resource;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
public class TournamentService implements TournamentServiceInterface{
    private final UserProgressService progressService;
    private final TournamentRepository tournamentRepository;
    private final TournamentGroupRepository tournamentGroupRepository;
    private final TournamentRankInGroupRepository tournamentRankInGroupRepository;

    @Autowired
    private final RedisTemplate<String, String> redisTemplate;


    @Resource(name = "redisTemplate")
    private ZSetOperations<String, UserTuple> zSetOperations;

    @Override
    public Optional<Tournament> getTournament(Long id) {
        return tournamentRepository.findById(id);
    }

    @Override
    public Optional<TournamentGroup> getGroup(Long id) {
        return tournamentGroupRepository.findById(id);
    }

    @Override
    public Tournament getLatestTournament() {
        Optional<Tournament> lastTournament = tournamentRepository.findFirstByOrderByIdDesc();

        if (lastTournament.isPresent() == true) {
            Tournament tournament = lastTournament.get();
            if (tournament.getStartDate().isBefore(LocalDateTime.now()) && tournament.getEndDate().isAfter(LocalDateTime.now())) {
                return tournament;
            }
        }
        throw new RuntimeException("Cannot find any active tournament!");
    }

    @Override
    public List<?> enterTournament(User user, Tournament tournament) {

        if (waitingTournament(user) == true)
            throw new RuntimeException("User has a not claimed tournament reward!");

        Integer currentLevel = user.getCurrentLevel();
        Integer currentCoins = user.getCurrentCoins();
        if (currentLevel >= 20 && currentCoins >= 1000) {
            TournamentGroup newGroup;
            Optional<TournamentGroup> findGroup = tournamentGroupRepository.findByTournamentAndCurrMaxLevelAndFilled(tournament, ((currentLevel + 99) / 100) * 100, false);

            if (findGroup.isEmpty()) {
                newGroup = new TournamentGroup(tournament, ((currentLevel + 99) / 100) * 100);
                newGroup = tournamentGroupRepository.save(newGroup);
            }

            else {
                newGroup = findGroup.get();
            }

            user.setCurrentCoins(currentCoins - 1000);
            TournamentRankInGroup userRankingInGroup = new TournamentRankInGroup(user, newGroup, false, 0);
            tournamentRankInGroupRepository.save(userRankingInGroup);
            newGroup.setSize(newGroup.getSize() + 1);
            if (newGroup.getSize() == 20) {
                newGroup.setFilled(true);
            }
            tournamentGroupRepository.save(newGroup);

            return putLeaderboard(newGroup, user, 0);
        }
        if(currentLevel < 20) {
            throw new RuntimeException("User is under level 20!");
        }
        if(currentCoins < 1000)
        {
            throw new RuntimeException("Not enough coins!");
        }
        return null;
    }


    @Override
    public Boolean waitingTournament(User user) {
        Optional<TournamentRankInGroup> ranking = tournamentRankInGroupRepository.findByUserAndClaimStatusAndFinalRankBetween(false, user, 1, 10);
        if (ranking.isEmpty()) {
            return true;
        }
        return false;
    }

    @Override
    public List<?> putLeaderboard(TournamentGroup group, User user, Integer score) {
        UserTuple tuple = new UserTuple(user.getID(), user.getUsername());
        zSetOperations.add(group.getID().toString(), tuple, score);
        return listLeaderboard(group);
    }

    @Override
    public List<?> listLeaderboard(TournamentGroup group) {
        return Objects.requireNonNull(zSetOperations.reverseRangeWithScores(group.getID().toString(), 0, 20))
                .stream()
                        .map(e ->
                                new CurrentLeaderboardStatusResponse(
                                        e.getValue(),
                                        group.getID(), group.getTournament().getID(),
                                        e.getScore().intValue()))
                .collect(Collectors.toList());
    }

    @Override
    public CurrentRankResponse getRankInGivenTournament(Tournament tournament, User user) {
        Optional<TournamentRankInGroup> ranking = tournamentRankInGroupRepository.findByUserAndGroup_Tournament(user, tournament);

        if (ranking.isEmpty() == true)
            throw new RuntimeException("Did not attend to a tournament!");

        Long group = ranking.get().getGroup().getID();
        Long rank = zSetOperations.reverseRank(group.toString(), new UserTuple(user.getID(), user.getUsername()));

        return new CurrentRankResponse(group, rank.intValue());
    }

    @Override
    public Boolean updateUserActiveTournamentRanking(User user) {
        Tournament tournament = getLatestTournament();
        Optional<TournamentRankInGroup> getRanking = tournamentRankInGroupRepository.findByUserAndGroup_Tournament(user, tournament);
        if (getRanking.isEmpty())
            throw new RuntimeException("No active tournaments!");

        TournamentRankInGroup ranking = getRanking.get();
        ranking.setFinalScore(ranking.getFinalScore() + 1);
        putLeaderboard(ranking.getGroup(), user, ranking.getFinalScore());

        tournamentRankInGroupRepository.save(ranking);
        return true;
    }

}
