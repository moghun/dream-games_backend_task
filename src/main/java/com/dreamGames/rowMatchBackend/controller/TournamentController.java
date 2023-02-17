package com.dreamGames.rowMatchBackend.controller;

import com.dreamGames.rowMatchBackend.model.Tournament;
import com.dreamGames.rowMatchBackend.model.TournamentGroup;
import com.dreamGames.rowMatchBackend.model.User;
import com.dreamGames.rowMatchBackend.requests.ClaimRequest;
import com.dreamGames.rowMatchBackend.requests.LeaderboardRequest;
import com.dreamGames.rowMatchBackend.requests.RankRequest;
import com.dreamGames.rowMatchBackend.responses.CurrentLeaderboardStatusResponse;
import com.dreamGames.rowMatchBackend.responses.CurrentRankResponse;
import com.dreamGames.rowMatchBackend.responses.OneLevelProgressResponse;
import com.dreamGames.rowMatchBackend.service.TournamentService;
import com.dreamGames.rowMatchBackend.service.UserProgressService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/tournament")
@RequiredArgsConstructor
public class TournamentController {
    private final UserProgressService userService;
    private final TournamentService tournamentService;
    @PostMapping("/EnterTournamentRequest")
    public ResponseEntity<?> enter() {
        try {
            Tournament tournament = tournamentService.getLatestTournament();
            User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            List<CurrentLeaderboardStatusResponse> leaderboard = tournamentService.enterTournament(user, tournament);
            return new ResponseEntity<>(leaderboard, HttpStatus.OK);
        }
        catch (RuntimeException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping(value = "/ClaimRewardRequest")
    public ResponseEntity<?> claim(@Valid @RequestBody ClaimRequest tournamentID) {
        Optional<Tournament> tournament = tournamentService.getTournament(tournamentID.getTournament());

        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        if (tournament.isPresent() == true) {
            OneLevelProgressResponse response;
            try {
                response = tournamentService.claimReward(tournament.get(), user);
            }
            catch (RuntimeException e) {
                return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
            }
            return new ResponseEntity<>(response, HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }

    @PostMapping(value = "/GetRankRequest")
    public ResponseEntity<?> rank(@Valid @RequestBody RankRequest tournamentRankRequest) {
        Optional<Tournament> tournament = tournamentService.getTournament(tournamentRankRequest.getTournament());

        Optional<User> user = userService.findUser(tournamentRankRequest.getID());

        if (user.isPresent() == true && tournament.isPresent() == true) {
            CurrentRankResponse tournamentRankResponse;
            try {
                tournamentRankResponse = tournamentService.getRankInGivenTournament(tournament.get(), user.get());
            }
            catch (RuntimeException e) {
                return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
            }
            return new ResponseEntity<>(tournamentRankResponse, HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }

    @PostMapping(value = "/GetLeaderboardRequest")
    public ResponseEntity<?> leaderboard(@Valid @RequestBody LeaderboardRequest groupID) {
        Optional<TournamentGroup> group = tournamentService.getGroup(groupID.getGroup());

        if (group.isEmpty() == true)
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);

        List<CurrentLeaderboardStatusResponse> leaderboard = tournamentService.listLeaderboard(group.get());

        return new ResponseEntity<>(leaderboard, HttpStatus.OK);
    }

    @PostMapping("/ManuelTournamentStartRequest")
    public ResponseEntity<?> start()
    {
        tournamentService.startTournament();
        return new ResponseEntity<>(HttpStatus.OK);
    }
}