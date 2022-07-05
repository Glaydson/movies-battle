package com.glaydson.moviesbattle.api;

import com.glaydson.moviesbattle.entity.Game;
import com.glaydson.moviesbattle.entity.GameRound;
import com.glaydson.moviesbattle.repository.GameRepository;
import com.glaydson.moviesbattle.repository.GameRoundRepository;
import com.glaydson.moviesbattle.service.GameService;
import com.glaydson.moviesbattle.service.MovieService;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.test.annotation.DirtiesContext;

import java.time.LocalDateTime;
import java.util.ArrayList;

import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
class GameRestControllerIntegrationTest {

    @Autowired
    private GameRestController gameRestController;

    @Autowired
    private GameService gameService;

    @Autowired
    private GameRepository gameRepository;

    @Autowired
    private GameRoundRepository gameRoundRepository;

    @Autowired
    private MovieService movieService;

    @BeforeEach
    void setUp() {
    }

    @AfterEach
    void tearDown() {
    }

    @Test
    void startGameOK() {
        given().auth().basic("user1", "pass1")
                .accept(ContentType.JSON)
                .when()
                .get("/api/game/start")
                .then()
                .statusCode(HttpStatus.OK.value());
    }

    @Test
    void startGameUserNotAuthenticated() {
        given().auth().basic("user1", "wrongPass")
                .accept(ContentType.JSON)
                .when()
                .get("/api/game/start")
                .then()
                .statusCode(org.apache.http.HttpStatus.SC_UNAUTHORIZED);
    }

    @Test()
    void startGameAlreadyExists() {
        Game game = new Game();
        game.setRounds(new ArrayList<>());
        game.setPlayer("user1");
        game.setDateTimeStart(LocalDateTime.now());
        game.setTotalRounds(0L);
        game.setTotalPoints(0);
        this.gameRepository.save(game);

        given().auth().basic("user1", "pass1")
                .accept(ContentType.JSON)
                .when()
                .get("/api/game/start")
                .then()
                .statusCode(HttpStatus.FOUND.value());
    }

    @Test
    void endGameOK() {
        Game game = new Game();
        game.setRounds(new ArrayList<>());
        game.setPlayer("user1");
        game.setDateTimeStart(LocalDateTime.now());
        game.setTotalRounds(0L);
        game.setTotalPoints(0);
        this.gameRepository.save(game);

        given().auth().basic("user1", "pass1")
                .accept(ContentType.JSON)
                .when()
                .get("/api/game/end")
                .then()
                .statusCode(HttpStatus.OK.value());
    }

    @Test
    void endGameNotExists() {
        given().auth().basic("user1", "pass1")
                .accept(ContentType.JSON)
                .when()
                .get("/api/game/end")
                .then()
                .statusCode(HttpStatus.NOT_FOUND.value());
    }

    @Test
    void endGameAlreadyEnded() {
        Game game = new Game();
        game.setRounds(new ArrayList<>());
        game.setPlayer("user1");
        game.setDateTimeStart(LocalDateTime.now());
        game.setDateTimeEnd(LocalDateTime.now());
        game.setTotalRounds(0L);
        game.setTotalPoints(0);
        this.gameRepository.save(game);

        given().auth().basic("user1", "pass1")
                .accept(ContentType.JSON)
                .when()
                .get("/api/game/end")
                .then()
                .statusCode(HttpStatus.NOT_FOUND.value());
    }

    @Test
    void startRoundOK() {
        Game game = new Game();
        game.setPlayer("user1");
        game.setDateTimeStart(LocalDateTime.now());
        game.setTotalRounds(0L);
        game.setTotalPoints(0);
        this.gameRepository.save(game);

        given().auth().basic("user1", "pass1")
                .accept(ContentType.JSON)
                .when()
                .get("/api/game/startround/1")
                .then()
                .statusCode(HttpStatus.OK.value());
    }

    @Test
    void startRoundGameDoesntExist() {
        given().auth().basic("user1", "pass1")
                .accept(ContentType.JSON)
                .when()
                .get("/api/game/startround/1")
                .then()
                .statusCode(HttpStatus.NOT_FOUND.value());
    }

    @Test
    void startRoundExistRoundWithoutAnswer() {

        Game game = new Game();
        game.setPlayer("user1");
        game.setDateTimeStart(LocalDateTime.now());
        game.setTotalRounds(0L);
        game.setTotalPoints(0);

        GameRound newRound = new GameRound(
                "user1", 1, 30L, 10L, game, null, null);

        this.gameRepository.save(game);
        this.gameRoundRepository.save(newRound);

        given().auth().basic("user1", "pass1")
                .accept(ContentType.JSON)
                .when()
                .get("/api/game/startround/1")
                .then()
                .statusCode(HttpStatus.NOT_FOUND.value());
    }

    @Test
    void startRoundGameNotActive() {

        Game game = new Game();
        game.setPlayer("user1");
        game.setDateTimeStart(LocalDateTime.now());
        game.setDateTimeEnd(LocalDateTime.now());
        game.setTotalRounds(0L);
        game.setTotalPoints(0);

        given().auth().basic("user1", "pass1")
                .accept(ContentType.JSON)
                .when()
                .get("/api/game/startround/1")
                .then()
                .statusCode(HttpStatus.NOT_FOUND.value());
    }

    @Test
    void startRoundPairsInvalid() {

        Game game = new Game();
        game.setPlayer("user1");
        game.setDateTimeStart(LocalDateTime.now());
        game.setDateTimeEnd(LocalDateTime.now());
        game.setTotalRounds(0L);
        game.setTotalPoints(0);

        given().auth().basic("user1", "pass1")
                .accept(ContentType.JSON)
                .when()
                .get("/api/game/startround/1")
                .then()
                .statusCode(HttpStatus.NOT_FOUND.value());
    }

    @Test
    void playRoundOK() {

        Game game = new Game();
        game.setPlayer("user1");
        game.setDateTimeStart(LocalDateTime.now());
        game.setTotalRounds(0L);
        game.setTotalPoints(0);

        GameRound newRound = new GameRound(
                "user1", 1, 30L, 10L, game, null, null);

        this.gameRepository.save(game);
        this.gameRoundRepository.save(newRound);

        given().auth().basic("user1", "pass1")
                .accept(ContentType.JSON)
                .when()
                .get("/api/game/playround/1/1/2")
                .then()
                .statusCode(HttpStatus.OK.value());


    }

    @Test
    void playRoundOptionInvalid() {

        Game game = new Game();
        game.setPlayer("user1");
        game.setDateTimeStart(LocalDateTime.now());
        game.setTotalRounds(0L);
        game.setTotalPoints(0);

        GameRound newRound = new GameRound(
                "user1", 1, 30L, 10L, game, 30, true);

        this.gameRepository.save(game);
        this.gameRoundRepository.save(newRound);

        given().auth().basic("user1", "pass1")
                .accept(ContentType.JSON)
                .when()
                .get("/api/game/playround/1/1/3")
                .then()
                .statusCode(HttpStatus.BAD_REQUEST.value());
    }
}