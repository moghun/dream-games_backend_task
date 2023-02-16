package com.dreamGames.rowMatchBackend.responses;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class OneLevelProgressResponse {
    private Integer level;
    private Integer coin;
}
