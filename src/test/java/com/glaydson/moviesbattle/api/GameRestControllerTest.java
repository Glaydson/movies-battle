package com.glaydson.moviesbattle.api;

import com.glaydson.moviesbattle.entity.Game;
import com.glaydson.moviesbattle.entity.GameRound;
import com.glaydson.moviesbattle.service.GameService;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.test.annotation.DirtiesContext;

import java.time.LocalDateTime;
import java.util.ArrayList;

import static io.restassured.RestAssured.given;
import static io.restassured.module.mockmvc.RestAssuredMockMvc.standaloneSetup;
import static org.mockito.Mockito.when;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
class GameRestControllerTest {


//    @Autowired
//    private MockMvc mvc;

    @Autowired
    private GameRestController gameRestController;

    @MockBean
    private GameService gameService;

    @BeforeEach
    void setUp() {
        standaloneSetup(this.gameRestController);
    }


    @Test
    public void testUnathourized() {
        given().get("/api/game/start").then().statusCode(org.apache.http.HttpStatus.SC_UNAUTHORIZED);

    }

    @Test
    public void testUnathourizedWhenUserDoesntExist() {
        given().auth().basic("wronguser", "user")
                .get("/api/game/start")
                .then().statusCode(org.apache.http.HttpStatus.SC_UNAUTHORIZED);

    }

    @Test
    public void testUnauthorizedWhenWrongPassword() {
        given().auth().basic("user1", "pass")
                .get("/api/game/start")
                .then().statusCode(org.apache.http.HttpStatus.SC_UNAUTHORIZED);

    }

    @Test
    public void testStatusOkUserAuthorized() {
        given().auth().basic("user1", "pass1")
                .get("/api/game/start")
                .then().statusCode(org.apache.http.HttpStatus.SC_OK);
    }

    @Test
    void testStartGameOk() throws Exception {
        GameService mockGameService = Mockito.mock(GameService.class);
        when(mockGameService.startGame("user1"))
                .thenReturn(new Game(
                        1L, null, "user1", 0, 0L, LocalDateTime.now(), null));

        given().auth().basic("user1", "pass1")
                .accept(ContentType.JSON)
                .when()
                .get("/api/game/start")
                .then()
                .statusCode(HttpStatus.OK.value());
    }

    @Test
    void testStartGameAlreadyExists() throws Exception {
        GameService mockGameService = Mockito.mock(GameService.class);
        when(mockGameService.startGame("user1"))
                .thenReturn(null);

        given().auth().basic("user1", "pass1")
                .accept(ContentType.JSON)
                .when()
                .get("/api/game/start")
                .then()
                .statusCode(HttpStatus.NOT_FOUND.value());
    }

    @Test
    void testEndGameOK() {
        GameService mockGameService = Mockito.mock(GameService.class);
        Mockito.when(mockGameService.endGame("user1"))
                .thenReturn(new Game(
                        1L, new ArrayList<GameRound>(), "user1", 1, 1L, LocalDateTime.now(), LocalDateTime.now()));

        given().auth().basic("user1", "pass1")
                .accept(ContentType.JSON)
                .when()
                .get("/api/game/end")
                .then()
                .statusCode(HttpStatus.OK.value());

    }

    @Test
    void testEndGameAlreadyEnded() {
        GameService mockGameService = Mockito.mock(GameService.class);
        Mockito.when(mockGameService.endGame("user1"))
                .thenReturn(null);

        given().auth().basic("user1", "pass1")
                .accept(ContentType.JSON)
                .when()
                .get("/api/game/end")
                .then()
                .statusCode(HttpStatus.NOT_FOUND.value());

    }

    //@Test
    void startRound() {
    }

    //@Test
    void playRound() {
    }
}