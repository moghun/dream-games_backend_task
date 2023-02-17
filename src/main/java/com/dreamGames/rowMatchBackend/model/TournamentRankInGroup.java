package com.dreamGames.rowMatchBackend.model;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Table(name = "ranks")
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class TournamentRankInGroup {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long ID;
    @ManyToOne()
    @JoinColumn(name = "user_id", referencedColumnName = "ID")
    private User user;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "tournament_id", referencedColumnName = "ID")
    private TournamentGroup group;
    private Boolean claimStatus;
    private Integer finalScore;
    private Integer finalRank;

    public TournamentRankInGroup(User user, TournamentGroup group, Boolean claimStatus, Integer finalScore) {
        this.user = user;
        this.group = group;
        this.claimStatus = claimStatus;
        this.finalScore = finalScore;
    }
}
