package com.dreamGames.rowMatchBackend.responses;



import com.dreamGames.rowMatchBackend.requests.UserTuple;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter

public class CurrentLeaderboardStatusResponse {
    private Long ID;
    private String username;
    private Long group;
    private Long tournament;
    private Integer score;

    public CurrentLeaderboardStatusResponse(UserTuple tuple, Long group, Long tournament, Integer score) {
        this.ID = tuple.getID();
        this.username = tuple.getUsername();
        this.group = group;
        this.tournament = tournament;
        this.score = score;

    }
}

