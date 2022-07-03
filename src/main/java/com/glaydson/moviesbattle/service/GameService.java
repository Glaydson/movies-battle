package com.glaydson.moviesbattle.service;

import com.glaydson.moviesbattle.entity.Game;
import com.glaydson.moviesbattle.repository.GameRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Objects;

@Service
public class GameService {

    @Autowired
    private GameRepository repository;

    @Autowired
    private MovieService movieService;

    public Game startGame(String player) {
        // Carregar dados de filmes do IMDB
        this.movieService.carregarFilmesIMDB();

        // Verifica se não existe jogo para este jogador
        final Game game = repository.findByDateTimeEndIsNullAndPlayer(player);
        if (Objects.isNull(game)) {
            return repository.save(Game.builder()
                    .totalRounds(0L)
                    .player(player)
                    .totalPoints(0L)
                    .dateTimeStart(LocalDateTime.now()).build());
        }
        return game;
    }



    public void endGame(String player) {

        repository.findByDateTimeEndIsNullAndPlayer(player);
    }

}
