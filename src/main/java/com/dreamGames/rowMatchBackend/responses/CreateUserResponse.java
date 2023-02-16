package com.dreamGames.rowMatchBackend.responses;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class CreateUserResponse {
    private Long UserID;
    private Integer level;
    private Integer coin;
}
