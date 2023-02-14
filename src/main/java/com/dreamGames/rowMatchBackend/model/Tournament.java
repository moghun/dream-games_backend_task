package com.dreamGames.rowMatchBackend.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Table(name = "tournaments")
@Entity
@AllArgsConstructor
@Getter
@Setter
public class Tournament
{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long ID;
    private LocalDateTime startDate;
    private LocalDateTime endDate;

    public Tournament() {
        this.startDate = LocalDateTime.now();
        this.endDate = LocalDateTime.now();
    }
}
