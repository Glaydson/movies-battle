package com.glaydson.moviesbattle.service;

import com.glaydson.moviesbattle.entity.Movie;
import com.glaydson.moviesbattle.repository.MovieRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.*;

@Service
public class MovieService {

    @Autowired
    private MovieRepository repository;

    private static final int NUM_PAGINAS_LIDAS = 10;
    private static final String URL_SEARCH = "http://www.omdbapi.com/?apikey=a824da67&s=love&type=movie&page=";
    private static final String URL_MOVIE = "http://www.omdbapi.com/?apikey=a824da67&plot=full&i=";
    public static final String INVALID_RATING = "N / A";
//    public static String SQL_INSERT =
//            "INSERT INTO TB_MOVIES " +
//            "(TITLE, IMDB_RATING, IMDB_VOTES) " +
//            "VALUES ('%s', %s, %s);";


    public List<Movie> findAllMovies() {
        List<Movie> movies = new ArrayList<>();
        this.repository.findAll().forEach(movies::add);
        return movies;
    }

    public void carregarFilmesIMDB() {
        // Lê filmes da api IMDB e carrega na tabela de filmes
        for (int pagina = 1; pagina <= NUM_PAGINAS_LIDAS; pagina++) {

            RestTemplate restTemplate = new RestTemplate();
            String urlMoviesList = URL_SEARCH + pagina;
            ResponseEntity<SearchMoviesResult> response = restTemplate.getForEntity(urlMoviesList.toLowerCase(), SearchMoviesResult.class);

            for (Object result: Objects.requireNonNull(response.getBody()).getSearch()) {
                String urlMovie = URL_MOVIE + ((LinkedHashMap)result).get("imdbID");
                ResponseEntity<FindMovieResult> responseMovie = restTemplate.getForEntity(urlMovie.toLowerCase(), FindMovieResult.class);

                if (null != responseMovie.getBody()) {
                    if (!INVALID_RATING.equals(responseMovie.getBody().getImdbRating())) {
                        this.repository.save(Movie.builder()
                                .imdbRating(Double.valueOf(responseMovie.getBody().getImdbRating()))
                                .title(responseMovie.getBody().getTitle())
                                .imdbVotes(Integer.valueOf(responseMovie.getBody().getImdbVotes().replace(",", "")))
                                .build());
                    }
                }
            }
        }
    }

}
