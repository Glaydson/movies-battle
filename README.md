# movies-battle
Game API for Lets Code

- The game consists in several rounds, where in each round the user have to choose between two 
movies, betting which of them have the best IMDB punctuation.
- Each correct answer gives a point to the user.
- Three incorrect answers ends the game.
- The game can be ended at any time.

Endpoints:

- api/game/start - starts a game
- api/game/startround/{gameId} - starts a round for the specified game
- api/game/playround/{gameId}/{roundId}/{option} - process the user option
- api/end - ends a game

The API Documentation using OpenAPI 3.0 is available at http://localhost:8080/swagger-ui

This API uses the following technologies:
- Java 11
- Spring Boot 2.7
- Spring Web
- Spring Data 
- Spring Security
- H2 Database
- Lombok
- Spring Doc OpenAPI
- Spring Mock MVC
- Mockito
