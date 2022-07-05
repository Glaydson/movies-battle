package com.glaydson.moviesbattle.api;

import com.glaydson.moviesbattle.entity.Game;
import com.glaydson.moviesbattle.entity.GameRound;
import com.glaydson.moviesbattle.service.GameService;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import static io.restassured.module.mockmvc.RestAssuredMockMvc.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.ArrayList;

import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(SpringExtension.class)
@WebMvcTest
//@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT )
  //  @RunWith(SpringRunner.class)
class GameRestControllerTest {

    @Value("${server.port}")
    private int serverPort;

//    @Autowired
//    private MockMvc mvc;

//
    @Autowired
    private GameRestController gameRestController;

    @MockBean
    private GameService gameService;

    @BeforeEach
    void setUp() {
        standaloneSetup(this.gameRestController);
        //assertEquals(8080, serverPort);
    }

//    @Test
//    void startGame() throws Exception {
//        RequestBuilder request = MockMvcRequestBuilders.get("/api/game/start");
//        MvcResult result = mvc.perform(request).andReturn();
//        assertEquals("", result.getResponse().getContentAsString());
//    }


    @Test
    public void testUnathourizedWhenStatusWhenDontAuth() {
        given().get("/api/game/start").then().statusCode(org.apache.http.HttpStatus.SC_UNAUTHORIZED);

    }

    @Test
    public void testUnathourizedStatusWhenUserDoesntExist() {
        given().auth().basic("wronguser", "user")
                .get("/api/game/start")
                .then().statusCode(org.apache.http.HttpStatus.SC_UNAUTHORIZED);

    }

    @Test
    public void testUnauthorizedStatusWhenWrongPassword() {
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
    @DirtiesContext
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
    @DirtiesContext
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
    @DirtiesContext
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
    @DirtiesContext
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