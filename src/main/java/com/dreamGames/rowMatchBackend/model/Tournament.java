package com.dreamGames.rowMatchBackend.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Table(name = "tournaments")
@Entity
@AllArgsConstructor
@NoArgsConstructor
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
        this.startDate = LocalDateTime.now();
    }
}
