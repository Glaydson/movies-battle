package com.glaydson.moviesbattle.resource;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GameRoundStartResource {

    private Long roundId;

    private Long gameId;

    private String player;

    private String movie1Title;

    private String movie2Title;

    private Integer roundNumber;


}
