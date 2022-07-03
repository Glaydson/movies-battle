package com.glaydson.moviesbattle.service;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class FindMovieResult {

    @JsonProperty("Title")
    String Title;

    @JsonProperty("imdbID")
    String imdbID;

    @JsonProperty("imdbRating")
    String imdbRating;

    @JsonProperty("imdbVotes")
    String imdbVotes;

}
