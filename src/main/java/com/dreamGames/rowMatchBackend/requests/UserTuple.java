package com.dreamGames.rowMatchBackend.requests;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UserTuple implements Serializable {
    private Long ID;
    private String username;
}
