package com.glaydson.moviesbattle.entity;

import lombok.*;

import javax.persistence.*;

@Entity
@AllArgsConstructor
@Getter
@Setter
@NoArgsConstructor
@Builder
@Table(name = "TB_RANK")
public class Rank {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "rank_id")
    private Long id;

    @Column(name = "player")
    private String player;

    @Column(name = "points_ranking")
    private Double pointsRanking;

    @Column(name = "points_earned")
    private Integer pointsEarned;

    @Column(name = "rounds_played")
    private Integer roundsPlayed;

    @Column(name = "quizzes_played")
    private Integer quizzesPlayed;

}
