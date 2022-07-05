package com.glaydson.moviesbattle.api;

import com.glaydson.moviesbattle.entity.Game;
import com.glaydson.moviesbattle.resource.GameRoundResultResource;
import com.glaydson.moviesbattle.resource.GameRoundStartResource;
import com.glaydson.moviesbattle.service.GameService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Objects;

@RestController
@RequestMapping("/api/game")
public class GameRestController {

    @Autowired
    private GameService gameService;

    /**
     * Start a new game for the authenticated user
     * @param authentication Object containing the user authenticated
     * @return The Game object that was created
     * @throws Exception if the user is already in a game
     */
    @Operation(summary = "Start a new game for the authenticated user")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Game was created with 0 rounds",
                    content = { @Content(mediaType = "application/json",
                            schema = @Schema(implementation = Game.class)) }),
            @ApiResponse(responseCode = "404", description = "Game already exists",
                    content = @Content) })
    @GetMapping("/start")
    @ResponseBody
    public ResponseEntity startGame(Authentication authentication) {
        Game game = null;
        try {
            game = gameService.startGame(authentication.getName());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.FOUND).body(e.getMessage());
        }
        return ResponseEntity.ok(game);
    }

    /**
     * End a game for the authenticated user
     * @param authentication Authenticated user
     * @return Object containing the game data after is ended.
     */
    @Operation(summary = "Ends the game for an authenticated user")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "The game is ended, and new rounds will not be " +
                    "allowed. The rank is updated",
                    content = { @Content(mediaType = "application/json",
                            schema = @Schema(implementation = Game.class)) }),
            @ApiResponse(responseCode = "404", description = "An active game was not found for the " +
                    " authenticated user", content = @Content) })
    @GetMapping("/end")
    @ResponseBody
    public ResponseEntity<Game> endGame(Authentication authentication) {
        Game game = gameService.endGame(authentication.getName());
        if (Objects.isNull(game)) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(game);
    }

    /**
     * Start a game round for the authenticated user
     * @param authentication Authenticated user
     * @return A resource with information for the new round, containing 2 movies to be choosed by the user
     */
    @Operation(summary = "Start a round of a game already started")
    @Parameters(value = {
            @Parameter(name = "gameId", description = "Id of the game", required = true),
    })
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "The round was started, and two movies are available to vote",
                    content = { @Content(mediaType = "application/json",
                            schema = @Schema(implementation = GameRoundResultResource.class)) }),
            @ApiResponse(responseCode = "400", description = "Game Id not supplied",
                    content = @Content),
            @ApiResponse(responseCode = "404", description = "The game is invalid (does not exist, or " +
                    "is closed) or the round is invalid (does not exists or there's an open round not " +
                    "answered yet",
                    content = @Content) })
    @GetMapping("/startround/{gameId}")
    @ResponseBody
    public ResponseEntity startRound(
            @PathVariable Long gameId, Authentication authentication) {
        GameRoundStartResource resource = null;
        try {
            resource = gameService.startRound(gameId, authentication.getName());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
        return ResponseEntity.ok(resource);
    }

    /**
     * Play a round for a game that is active
     * @param gameId Id of the game
     * @param roundId Id of the round
     * @param option Answer (option 1 or 2)
     * @param authentication Authenticated user
     * @return A resource containing the data of the round
     */
    @Operation(summary = "Play a round of a game already started")
    @Parameters(value = {
            @Parameter(name = "gameId", description = "Id of the game", required = true),
            @Parameter(name = "roundId", description = "Id of the round", required = true),
            @Parameter(name = "option", description = "Movie choosed (1 or 2)", required = true)
    })
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "The round was completed",
                    content = { @Content(mediaType = "application/json",
                            schema = @Schema(implementation = GameRoundResultResource.class)) }),
            @ApiResponse(responseCode = "400", description = "Option is invalid",
                    content = @Content),
            @ApiResponse(responseCode = "404", description = "The game does not exists for this player, or the " +
                    "round does not exist for this game, or the round is already answered",
                    content = @Content) })
    @GetMapping("/playround/{gameId}/{roundId}/{option}")
    public ResponseEntity<Object> playRound(@PathVariable Long gameId, @PathVariable Long roundId,
                                                             @PathVariable Integer option, Authentication authentication) {
        if (option == 1 || option == 2) {
            GameRoundResultResource resource = null;
            try {
                resource = gameService.playRound(gameId, roundId, option, authentication.getName());
            } catch (Exception e) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
            }
            return ResponseEntity.ok(resource);
        } else {
            return ResponseEntity.badRequest().body("Option should be 1 or 2");
        }
    }
}
