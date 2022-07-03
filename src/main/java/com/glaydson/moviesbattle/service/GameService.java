package com.glaydson.moviesbattle.service;

import com.glaydson.moviesbattle.entity.Game;
import com.glaydson.moviesbattle.entity.GameRound;
import com.glaydson.moviesbattle.repository.GameRepository;
import com.glaydson.moviesbattle.repository.GameRoundRepository;
import com.glaydson.moviesbattle.resource.GameRoundResultResource;
import com.glaydson.moviesbattle.resource.GameRoundStartResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Random;

@Service
public class GameService {

    @Autowired
    private GameRepository repository;

    @Autowired
    private GameRoundRepository roundRepository;

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
                    .totalPoints(0)
                    .dateTimeStart(LocalDateTime.now()).build());
        }
        return game;
    }



    public Game endGame(String player) {

        // Verifica se existe jogo para este jogador
        final Game game = repository.findByDateTimeEndIsNullAndPlayer(player);

        if (Objects.isNull(game)) {
            // TODO não há jogo ativo para o jogador
        }
        // Encerra o jogo
        game.setDateTimeEnd(LocalDateTime.now());
        return game;
    }

    /**
     * Creates a new round for the player
     * @param player
     * @return A new round for the player
     */
    public GameRoundStartResource startRound(Long gameId, String player) {

        //TODO validate the movie pairs before creating a new round

        // Procura jogo iniciado para este jogador
        //final Game game = repository.findByDateTimeEndIsNullAndPlayer(player);
        final Game game = repository.findById(gameId).get();

        if (Objects.isNull(game)) {
            // O jogo não existe
            // TODO o que fazer quando não há jogo para o jogador ou o jogo não existe
            // o que fazer quando aciona o startround em sequencia, sem finalizar a rodada (answer continua nulo)

            return null;
        } else {

            if (game.getPlayer().equals(player) && Objects.isNull(game.getDateTimeEnd())) {
                List<GameRound> gameRounds = game.getRounds();
                // Find 2 random movies to put in the round
                Integer totalMovies = this.movieService.totalMovies();
                Random rand = new Random();

                Long idMovie1 = (long) rand.ints(0, (totalMovies + 1))    // IntStream
                        .findAny()
                        .getAsInt();

                Long idMovie2 = (long) rand.ints(0, (totalMovies + 1))    // IntStream
                        .findAny()
                        .getAsInt();

                GameRound newRound = new GameRound(player, gameRounds.size() + 1, idMovie1, idMovie2, game);
                //gameRounds.add(newRound);
                //game.setRounds(gameRounds);
                this.roundRepository.save(newRound);
                repository.save(game);
                return GameRoundStartResource.builder()
                        .roundId(newRound.getId())
                        .gameId(newRound.getGame().getId())
                        .player(newRound.getPlayer())
                        .roundNumber(newRound.getRoundNumber())
                        .movie1Title(movieService.getMovieTitle(newRound.getIDMovie1()))
                        .movie2Title(movieService.getMovieTitle((newRound.getIDMovie2())))
                        .build();
            } else {
                // TODO não há jogo iniciado para este jogador
                return null;
            }
        }

    }

    public GameRoundResultResource playRound(Long gameId, Long roundId, Integer option, String player) {

        // Verify if the game exists
        // Verify if the game is for this player and its started
        // Verify if the round exists for that game and is not responded yet
        // Check if the answer is correct
        // Update points of the game
        // Calculate number of erros up to now
        // Verify if the game is over (3 errors)
        // Mount resource response with the information to the player

        final Game game = repository.findById(gameId).get();
        if (Objects.isNull(game)) {
            // O jogo não existe
            // TODO o que fazer quando não há jogo para o jogador ou o jogo não existe
            // o que fazer quando aciona o playround em sequencia, ou sem que exista round em aberto (todos answers diferentes de null)

            return null;
        } else {
            if (game.getPlayer().equals(player) && Objects.isNull(game.getDateTimeEnd())) {
                // Verify if the round exists for that game
                Integer numberOfErrors = 0;
                for (GameRound gameRound : game.getRounds()) {
                    if (null != gameRound.getCorrect() && !gameRound.getCorrect()) {
                        numberOfErrors++;
                    }
                }
                //Integer numberOfErrors = (int)game.getRounds().stream().filter(round -> null != round && !round.getCorrect()).count();
                List<GameRound> roundsGame = game.getRounds();
                boolean correct = false;
                for (GameRound gameRound: roundsGame) {
                    if (gameRound.getId().equals(roundId)) {
                        // Round exists
                        if (Objects.isNull(gameRound.getCorrect())) {
                            // Round not answered yet - ok
                            // check if the answer is correct
                            gameRound.setAnswer(option == 1 ? gameRound.getIDMovie1().intValue() :
                                    gameRound.getIDMovie2().intValue());
                            correct = this.movieService.checkAnswer(gameRound.getIDMovie1(), gameRound.getIDMovie2(), gameRound.getAnswer());
                            gameRound.setCorrect(correct);
                            if (!correct) numberOfErrors++;
                            updateGameRound(gameRound);
                            break;
                        } else {
                            // round invalid - already answered
                            return null;
                        }
                    } else {
                        // round not exists for this game
                        return null;
                    }
                }

                updateGame(game, correct, numberOfErrors);
                return GameRoundResultResource.builder()
                                .gameId(gameId)
                                .correct(correct)
                                .player(player)
                                .totalPoints(game.getTotalPoints())
                                .numberOfErrors(numberOfErrors)
                                .roundId(roundId)
                                .build();
            } else {
                // TODO não há jogo iniciado para este jogador
                return null;
            }
        }
    }


    private void updateGame(Game game, boolean correct, Integer numberOfErrors) {

        if (correct) game.setTotalPoints(game.getTotalPoints() + 1);
        if (numberOfErrors > 3) game.setDateTimeEnd(LocalDateTime.now()); // game is over
        game.setTotalRounds(game.getTotalRounds() + 1);
        this.repository.save(game);
    }

    private void updateGameRound(GameRound gameRound) {
        this.roundRepository.save(gameRound);
    }
}
