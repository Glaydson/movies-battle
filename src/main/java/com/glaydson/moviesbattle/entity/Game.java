package com.glaydson.moviesbattle.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;
import org.hibernate.annotations.GeneratorType;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@AllArgsConstructor
@Getter
@Setter
@NoArgsConstructor
@Builder
@Table(name = "TB_GAMES")
public class Game {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "game_id")
    private Long id;

    @OneToMany(mappedBy = "game", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JsonIgnoreProperties("game")
    private List<GameRound> rounds;

    @Column(name = "player")
    private String player;

    @Column(name = "total_points", nullable = false)
    private Integer totalPoints;

    @Column(name = "total_rounds", nullable = false)
    private Long totalRounds;

    @Column(name = "date_time_start", nullable = false)
    private LocalDateTime dateTimeStart;

    @Column(name = "date_time_end")
    private LocalDateTime dateTimeEnd;





}
