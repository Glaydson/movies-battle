package com.glaydson.moviesbattle.api;

import com.glaydson.moviesbattle.entity.Game;
import com.glaydson.moviesbattle.entity.Movie;
import com.glaydson.moviesbattle.service.GameService;
import com.glaydson.moviesbattle.service.MovieService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/game")
public class GameRestController {

    @Autowired
    private GameService gameService;

    @Autowired
    private MovieService movieService;

    @GetMapping("/start")
    @ResponseBody
    public ResponseEntity<Game> startGame(Authentication authentication) {
        System.out.println(authentication.getName());
        return ResponseEntity.ok(gameService.startGame(authentication.getName()));
    }

    @GetMapping("/end")
    @ResponseBody
    public ResponseEntity<String> endGame(Authentication authentication) {
        gameService.endGame(authentication.getName());
        return new ResponseEntity<>("Jogo finalizado para o usuário " + authentication.getName(), HttpStatus.OK);
    }

    @GetMapping("/allmovies")
    public @ResponseBody
    List<Movie> returnMovies() {
        return this.movieService.findAllMovies();
    }
}
