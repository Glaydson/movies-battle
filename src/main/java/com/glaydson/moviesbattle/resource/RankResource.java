package com.glaydson.moviesbattle.resource;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RankResource {

    private String player;
    private Double pointsRanking;

}
