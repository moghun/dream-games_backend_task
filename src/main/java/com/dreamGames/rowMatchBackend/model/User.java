package com.dreamGames.rowMatchBackend.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Table(name = "users")
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long ID;
    @Column(unique=true)
    private String username;
    private String password;
    private Integer currentLevel;
    private Integer currentCoins;

    public User(String username, String password) {
        this.username = username;
        this.password = password;
        this.currentLevel = 1;
        this.currentCoins = 5000;
    }
}
