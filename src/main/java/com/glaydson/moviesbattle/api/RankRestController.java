package com.glaydson.moviesbattle.api;

import com.glaydson.moviesbattle.resource.GameRoundResultResource;
import com.glaydson.moviesbattle.resource.RankResource;
import com.glaydson.moviesbattle.service.RankService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("api/rank")
public class RankRestController {

    @Autowired
    private RankService rankService;

    /**
     * Return the list of players ordered by their points in the ranking
     * @return Ranking of players
     */
    @Operation(summary = "Return the ranking of the game - a list of players ordered by his points")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "The list is returned",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = GameRoundResultResource.class))})
    })
    @GetMapping("/")
    public ResponseEntity<List<RankResource>> getRanking() {
        List<RankResource> resource = rankService.getRanking();
        return ResponseEntity.ok(resource);
    }

}
