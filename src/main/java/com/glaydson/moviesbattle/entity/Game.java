package com.glaydson.moviesbattle.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.GeneratorType;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@AllArgsConstructor
@Data
@NoArgsConstructor
@Builder
@Table(name = "TB_GAMES")
public class Game {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    //@SequenceGenerator(name = "id_game_seq", sequenceName = "id_game_seq", allocationSize = 1)
    @Column(name = "game_id")
    private Long id;

    @Column(name = "player")
    private String player;

    @Column(name = "total_points", nullable = false)
    private Long totalPoints;

    @Column(name = "total_rounds", nullable = false)
    private Long totalRounds;

    @Column(name = "date_time_start", nullable = false)
    private LocalDateTime dateTimeStart;

    @Column(name = "date_time_end")
    private LocalDateTime dateTimeEnd;



}
