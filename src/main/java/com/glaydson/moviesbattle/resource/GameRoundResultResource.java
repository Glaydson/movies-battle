package com.glaydson.moviesbattle.resource;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GameRoundResultResource {

    // gameid, roundid, correct or not, player, totalPoints, numberOfErrors

    private Long gameId;

    private Long roundId;

    private Boolean correct;

    private String player;

    private Integer totalPoints;

    private Integer numberOfErrors;

}
