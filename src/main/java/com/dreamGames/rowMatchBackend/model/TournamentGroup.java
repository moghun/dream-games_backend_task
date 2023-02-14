package com.dreamGames.rowMatchBackend.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Table(name = "groups")
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class TournamentGroup {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long ID;
    @ManyToOne()
    @JoinColumn(name = "tournamentID", referencedColumnName = "ID")
    private Tournament tournament;
    private Integer size;
    private Boolean filled;
    private Integer currMaxLevel;

    public TournamentGroup(Tournament tournament, Integer maxLevel) {
        this.tournament = tournament;
        this.size = 0;
        this.filled = false;
        this.currMaxLevel = maxLevel;
    }
}
