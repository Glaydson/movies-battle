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
import java.util.Optional;
import java.util.Random;

@Service
public class GameService {

    @Autowired
    private GameRepository repository;

    @Autowired
    private GameRoundRepository roundRepository;

    @Autowired
    private MovieService movieService;

    @Autowired
    private RankService rankService;

    /**
     * Start a game for the player authenticated
     * @param player User authenticated
     * @return The Game object
     */
    public Game startGame(String player) throws Exception {

        // Check if there isn't game for this player
        final Game game = repository.findByDateTimeEndIsNullAndPlayer(player);
        if (Objects.isNull(game)) {
            // Load movie data from IMDB
            this.movieService.loadMoviesIMDB();
            return repository.save(Game.builder()
                    .totalRounds(0L)
                    .player(player)
                    .totalPoints(0)
                    .dateTimeStart(LocalDateTime.now()).build());
        } else {
            // Game already started for the player
            throw new Exception("There is an active Game for this player");
        }
    }

    /**
     * Ends a game for the authenticated player
     * @param player Authenticated player
     * @return The Game object
     */
    public Game endGame(String player) {

        // Check if exists an active game for this player
        final Game game = repository.findByDateTimeEndIsNullAndPlayer(player);

        if (Objects.isNull(game)) {
            // There's no active game for this player
            return null;
        }
        // Ends the game - sets the end attribute
        game.setDateTimeEnd(LocalDateTime.now());
        this.repository.save(game);
        this.rankService.updateRank(game);
        return game;
    }

    /**
     * Creates a new round for the player
     * @param player The authenticated player
     * @return A new round for the player
     */
    public GameRoundStartResource startRound(Long gameId, String player) throws Exception {

        // Search an active game with this id and for this player
        Optional<Game> gameOptional = repository.findById(gameId);
        if (gameOptional.isEmpty()) {
            throw new Exception("Game not found");
        }
        final Game game = gameOptional.get();

        // Check if the game is associated with this player and if its active
        if (game.getPlayer().equals(player) && Objects.isNull(game.getDateTimeEnd())) {
            List<GameRound> gameRounds = game.getRounds();
            // Check if the last round is completed. Otherwise, can't continue
            if (gameRounds.size() > 0) {
                GameRound lastRound = gameRounds.get(gameRounds.size() - 1);
                if (Objects.isNull(lastRound.getAnswer())) {
                    // Last round has no answer, must play and complete the round
                    throw new Exception("There is a round without an answer. Can't start a new round");
                }
            }
            // Find two random movies to put in the round
            Integer totalMovies = this.movieService.totalMovies();
            Random rand = new Random();
            boolean pairsAreOk;
            Long idMovie1, idMovie2;
            do {
                idMovie1 = (long) rand.ints(0, (totalMovies + 1))    // IntStream
                        .findAny()
                        .getAsInt();

                idMovie2 = (long) rand.ints(0, (totalMovies + 1))    // IntStream
                        .findAny()
                        .getAsInt();
                pairsAreOk = checkIfPairsAreOk(gameRounds, idMovie1, idMovie2);
            } while (!pairsAreOk);

            GameRound newRound = new GameRound(
                    player, gameRounds.size() + 1, idMovie1, idMovie2, game);

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
            // The game is not active and/or does not belong to this player
            throw new Exception("Game not active or does not belong to this player");
        }
    }

    /**
     * Play a round of the game. Processes the option chose by the user and update the round and the game.
     * @param gameId id of the game
     * @param roundId id of the round
     * @param option movie selected (1 or 2)
     * @param player Player authenticated
     * @return Resource containing the results after processing the round
     * @throws Exception If some information is invalid
     */
    public GameRoundResultResource playRound(Long gameId, Long roundId, Integer option, String player)
            throws Exception {

        // Verify if the game exists
        // Verify if the game is for this player and its started
        // Verify if the round exists for that game and is not responded yet
        // Check if the answer is correct
        // Update points of the game
        // Calculate number of erros up to now
        // Verify if the game is over (3 errors)
        // Mount resource response with the information to the player

        // Search the game
        Optional<Game> gameOptional = repository.findById(gameId);
        if (gameOptional.isEmpty()) {
            throw new Exception("Game not found");
        }
        final Game game = gameOptional.get();

        if (game.getPlayer().equals(player) && Objects.isNull(game.getDateTimeEnd())) {
            // Verify if the round exists for that game
            Integer numberOfErrors = 0;
            for (GameRound gameRound : game.getRounds()) {
                if (null != gameRound.getCorrect() && !gameRound.getCorrect()) {
                    numberOfErrors++;
                }
            }
            List<GameRound> roundsGame = game.getRounds();
            boolean correct = false;
            boolean roundExists = false;
            for (GameRound gameRound: roundsGame) {
                if (gameRound.getId().equals(roundId)) {
                    // Round exists
                    roundExists = true;
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
                        throw new Exception("Round already answered.");
                    }
                }
            }
            // Round not found for the roundId supplied
            if (!roundExists) throw new Exception("Round not found.");
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
            throw new Exception("There is no game active with the id supplied for this player");
        }
    }

    /**
     * Check if a pair of movies is ok.
     * Non valid sequences: [A-A] the same movie repeated;
     * [A-B, A-B] repeated pairs – pairs of kind A-B e B-A are the same
     * The following pairs are valid: [A-B, B-C]
     * @param gameRounds List of rounds of the current game
     * @param idMovie1 id of the first movie to be included in the round
     * @param idMovie2 id of the second movie to be included in the round
     * @return True if the pairs are valid, false otherwise
     */
    private boolean checkIfPairsAreOk(List<GameRound> gameRounds, Long idMovie1, Long idMovie2) {
        if (Objects.equals(idMovie1, idMovie2)) return false;
        for (GameRound round: gameRounds) {
            Long mId1 = round.getIDMovie1();
            Long mId2 = round.getIDMovie2();
            if ( (Objects.equals(idMovie1, mId1) && Objects.equals(idMovie2, mId2)) ||
                    (Objects.equals(idMovie1, mId2) && Objects.equals(idMovie2, mId1)) ) {
                return false;
            }
        }
        return true;
    }

    /**
     * Update some fields of the game. Verify if maximum number of erros has been reached.
     * @param game Game to be updated
     * @param correct If the answer of the last round is correct or not
     * @param numberOfErrors number of erros up to now
     */
    private void updateGame(Game game, boolean correct, Integer numberOfErrors) {
        if (correct) game.setTotalPoints(game.getTotalPoints() + 1);
        if (numberOfErrors == 3) {
            game.setDateTimeEnd(LocalDateTime.now()); // game is over
            this.rankService.updateRank(game);
        }
        game.setTotalRounds(game.getTotalRounds() + 1);
        this.repository.save(game);
    }

    /**
     * Update the game round
     * @param gameRound the game round to be updated
     */
    private void updateGameRound(GameRound gameRound) {
        this.roundRepository.save(gameRound);
    }
}
