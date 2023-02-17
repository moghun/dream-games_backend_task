package com.dreamGames.rowMatchBackend.requests;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class RankRequest {
    private Long ID;
    private Long tournament;
}
