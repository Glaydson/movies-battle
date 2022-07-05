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

    private static final int NUM_PAGES_READ = 10;
    private static final String URL_SEARCH = "http://www.omdbapi.com/?apikey=a824da67&s=love&type=movie&page=";
    private static final String URL_MOVIE = "http://www.omdbapi.com/?apikey=a824da67&plot=full&i=";
    public static final String INVALID_RATING = "N / A";

    /**
     * Return the total of movies in the database
     * @return Total of movies in the database
     */
    public Integer totalMovies() {
        return Long.valueOf(this.repository.count()).intValue();
    }

    /**
     * Return the title of a movie by its id
     * @param movieId id of the movie
     * @return Title of the movie with the specified id
     */
    public String getMovieTitle(Long movieId) {
        return this.repository.findById(movieId).get().getTitle();
    }

    /**
     * Obtain a list of movies from IMDB database and load them in this app database for use.
     * Only some fields are loaded, and only a subset of the movies is loaded (all movies that contain
     * the word "love" in its title).
     */
    public void loadMoviesIMDB() {
        for (int pagina = 1; pagina <= NUM_PAGES_READ; pagina++) {

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

    /**
     * Check if the user answer in a round is correct.
     * @param idMovie1 First movie option
     * @param idMovie2 Second movie option
     * @param answer Movie chosen by the user
     * @return True if the answer is correct, False otherwise
     */
    public boolean checkAnswer(Long idMovie1, Long idMovie2, Integer answer) {

        Movie movie1 = this.repository.findById(idMovie1).get();
        Movie movie2 = this.repository.findById(idMovie2).get();

        Double points1 = movie1.getImdbRating() * movie1.getImdbVotes();
        Double points2 = movie2.getImdbRating() * movie2.getImdbVotes();

        return (points1 > points2);
    }
}
