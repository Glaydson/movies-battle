package com.glaydson.moviesbattle.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;

import javax.persistence.*;

@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode
@Table(name = "TB_GAME_ROUND")
public class GameRound {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "game_round_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "game_id", nullable = false)
    @JsonIgnoreProperties("rounds")
    private Game game;

    @Column(name = "player", nullable = false)
    private String player;

    @Column(name = "round_number", nullable = false)
    private Integer roundNumber;

    @Column(name = "id_movie_1",nullable = false)
    private Long iDMovie1;

    @Column(name = "id_movie_2",nullable = false)
    private Long iDMovie2;

    @Column(name = "answer")
    private Integer answer;

    @Column(name = "correct")
    private Boolean correct;

    public GameRound(String player, Integer roundNumber, Long iDMovie1, Long idMovie2, Game game) {
        this.player = player;
        this.roundNumber = roundNumber;
        this.iDMovie1 = iDMovie1;
        this.iDMovie2 = idMovie2;
        this.game = game;
    }

    public GameRound(String player, Integer roundNumber, Long iDMovie1, Long idMovie2, Game game, Integer answer, Boolean correct) {
        this.player = player;
        this.roundNumber = roundNumber;
        this.iDMovie1 = iDMovie1;
        this.iDMovie2 = idMovie2;
        this.game = game;
        this.answer = answer;
        this.correct = correct;
    }



}
