package com.glaydson.moviesbattle.api;

import com.glaydson.moviesbattle.entity.Game;
import com.glaydson.moviesbattle.entity.GameRound;
import com.glaydson.moviesbattle.entity.Movie;
import com.glaydson.moviesbattle.resource.GameRoundResultResource;
import com.glaydson.moviesbattle.resource.GameRoundStartResource;
import com.glaydson.moviesbattle.service.GameService;
import com.glaydson.moviesbattle.service.MovieService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/game")
public class GameRestController {

    @Autowired
    private GameService gameService;

    @Autowired
    private MovieService movieService;

    /**
     * Start a new game for the authenticated user
     * @param authentication Object containing the user authenticated
     * @return The Game object
     * @throws Exception if the user is already in a game
     */
    @GetMapping("/start")
    @ResponseBody
    public ResponseEntity<Game> startGame(Authentication authentication) {
        System.out.println(authentication.getName());
        return ResponseEntity.ok(gameService.startGame(authentication.getName()));
    }

    /**
     * End a game for the authenticated user
     * @param authentication
     * @return
     */
    @GetMapping("/end")
    @ResponseBody
    public ResponseEntity<Game> endGame(Authentication authentication) {
        return ResponseEntity.ok(gameService.endGame(authentication.getName()));
    }

    /**
     * Start a game round for the authenticated user
     * @param authentication
     * @return
     */
    @GetMapping("/startround/{gameId}")
    @ResponseBody
    public ResponseEntity<GameRoundStartResource> startRound(@PathVariable Long gameId, Authentication authentication) {
        return ResponseEntity.ok(gameService.startRound(gameId, authentication.getName()));
    }

    @PostMapping("/playround/{gameId}/{roundId}/{option}")
    public ResponseEntity<Object> playRound(@PathVariable Long gameId, @PathVariable Long roundId,
                                                             @PathVariable Integer option, Authentication authentication) {
        if (option == 1 || option == 2) {
            return ResponseEntity.ok(gameService.playRound(gameId, roundId, option, authentication.getName()));
        } else {
            return ResponseEntity.badRequest().body("Option should be 1 or 2");
        }
    }


}
