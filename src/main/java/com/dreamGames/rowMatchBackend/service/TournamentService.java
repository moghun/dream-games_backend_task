package com.dreamGames.rowMatchBackend.service;

import com.dreamGames.rowMatchBackend.model.Tournament;
import com.dreamGames.rowMatchBackend.model.TournamentGroup;
import com.dreamGames.rowMatchBackend.model.TournamentRankInGroup;
import com.dreamGames.rowMatchBackend.model.User;
import com.dreamGames.rowMatchBackend.repository.TournamentGroupRepository;
import com.dreamGames.rowMatchBackend.repository.TournamentRankInGroupRepository;
import com.dreamGames.rowMatchBackend.repository.TournamentRepository;
import com.dreamGames.rowMatchBackend.repository.UserRepository;
import com.dreamGames.rowMatchBackend.requests.UserTuple;
import com.dreamGames.rowMatchBackend.responses.CurrentLeaderboardStatusResponse;
import com.dreamGames.rowMatchBackend.responses.CurrentRankResponse;
import com.dreamGames.rowMatchBackend.responses.OneLevelProgressResponse;
import jakarta.annotation.Resource;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
public class TournamentService implements TournamentServiceInterface{

    private final UserRepository userRepository;
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
        Optional<Tournament> lastTournament = tournamentRepository.findFirstByOrderByIDDesc();

        if (lastTournament.isPresent() == true) {
            Tournament tournament = lastTournament.get();
            System.out.println(tournament.getStartDate());
            System.out.println(tournament.getEndDate());

            if (tournament.getStartDate().isBefore(LocalDateTime.now()) && tournament.getEndDate().isAfter(LocalDateTime.now())) {
                return tournament;
            }
        }
        throw new RuntimeException("Cannot find any active tournament!");
    }

    @Override
    public List<CurrentLeaderboardStatusResponse> enterTournament(User newUser, Tournament tournament) {
        User user = userRepository.findByUsername(newUser.getUsername());
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
            userRepository.save(user);

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
        userRepository.save(user);
        Optional<TournamentRankInGroup> ranking = tournamentRankInGroupRepository.findByUserAndClaimStatusAndFinalRankBetween( user, false,1, 10);
        if (ranking.isEmpty()) {
            return false;
        }
        return true;
    }

    @Override
    public List<CurrentLeaderboardStatusResponse> putLeaderboard(TournamentGroup group, User user, Integer score) {
        UserTuple tuple = new UserTuple(user.getID(), user.getUsername());
        zSetOperations.add(group.getID().toString(), tuple, score);
        return listLeaderboard(group);
    }

    @Override
    public List<CurrentLeaderboardStatusResponse> listLeaderboard(TournamentGroup group) {
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

        Tournament currentTournament = ranking.get().getGroup().getTournament();
        Tournament latestTournament = getLatestTournament();

        Long group = ranking.get().getGroup().getID();

        if (currentTournament.getID() == latestTournament.getID())
        {
            Long rank = zSetOperations.reverseRank(group.toString(), new UserTuple(user.getID(), user.getUsername()));
            return new CurrentRankResponse(group, rank.intValue());
        }

        else{
            Integer finalRank = ranking.get().getFinalRank();
            return new CurrentRankResponse(group, finalRank);
        }
    }

    @Override
    public Boolean updateUserProgressRealtime(User newUser) {
        Tournament tournament = getLatestTournament();
        User user = userRepository.findByUsername(newUser.getUsername());
        userRepository.save(user);
        Optional<TournamentRankInGroup> getRanking = tournamentRankInGroupRepository.findByUserAndGroup_Tournament(user, tournament);
        if (getRanking.isEmpty())
            return false;

        TournamentRankInGroup ranking = getRanking.get();
        ranking.setFinalScore(ranking.getFinalScore() + 1);
        putLeaderboard(ranking.getGroup(), user, ranking.getFinalScore());

        tournamentRankInGroupRepository.save(ranking);
        return true;
    }

    @Override
    public OneLevelProgressResponse claimReward(Tournament tournament, User requestingUser) {
        User user = userRepository.findByUsername(requestingUser.getUsername());
        userRepository.save(user);
        Optional<TournamentRankInGroup> ranking = tournamentRankInGroupRepository.findByUserAndGroup_Tournament(user, tournament);
        if (ranking.get().getClaimStatus() == true) {
            throw new RuntimeException("Reward is already claimed");
        }
        Integer rank = ranking.get().getFinalRank();
        Integer reward = 0;

        if (rank == 1) {
            reward = 10000;
        }

        else if (rank == 2) {
            reward = 5000;
        }

        else if (rank == 3) {
            reward = 3000;
        }

        else if (rank > 3 && rank < 11) {
            reward = 1000;
        }

        ranking.get().setClaimStatus(true);
        tournamentRankInGroupRepository.save(ranking.get());

        user.setCurrentCoins(user.getCurrentCoins() + reward);
        userRepository.save(user);

        OneLevelProgressResponse response = new OneLevelProgressResponse(user.getCurrentLevel(), user.getCurrentCoins());
        return response;
    }

    @Override
    public Boolean startTournament() {
        Optional<Tournament> latestTournament = tournamentRepository.findFirstByOrderByIDDesc();


        if (latestTournament.isPresent() == true) {
            if (latestTournament.get().getEndDate().isAfter(LocalDateTime.now()))
                return false;
            Set<String> redisKeys = redisTemplate.keys("*");
            Tournament tournament = latestTournament.get();


            assert redisKeys != null;
            List<Long> groupsIds = new ArrayList<>();
            for (String data : redisKeys) {
                groupsIds.add(Long.parseLong(data));
            }

            for (Long groupId : groupsIds) {
                TournamentGroup group = tournamentGroupRepository.findById(groupId).get();
                List<CurrentLeaderboardStatusResponse> leaderboardResponses = listLeaderboard(group);
                int rank = 0;

                for (CurrentLeaderboardStatusResponse leaderboardResponse : leaderboardResponses) {
                    User user = new User();
                    user.setID(leaderboardResponse.getID());
                    TournamentRankInGroup ranking = tournamentRankInGroupRepository.findByUserAndGroup_Tournament(user, tournament).get();

                    ranking.setFinalRank(rank+1);
                    tournamentRankInGroupRepository.save(ranking);
                    rank = rank + 1;
                }
            }
            redisTemplate.delete(Objects.requireNonNull(redisTemplate.keys("*")));
            Tournament newTournament = new Tournament();
            newTournament.setStartDate(tournament.getEndDate().plusHours(4));
            newTournament.setEndDate(tournament.getEndDate().plusHours(24));
            tournamentRepository.save(newTournament);
            return true;
        }
        Tournament newTournament = new Tournament();
        LocalDateTime now = LocalDateTime.now();
        newTournament.setStartDate(now);
        newTournament.setEndDate(now.plusHours(20));
        tournamentRepository.save(newTournament);

        return true;
    }
}
