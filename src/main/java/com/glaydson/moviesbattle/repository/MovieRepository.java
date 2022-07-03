package com.glaydson.moviesbattle.repository;

import com.glaydson.moviesbattle.entity.Movie;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface MovieRepository extends CrudRepository<Movie, Long> {

    List<Movie> findByIdEqualsAndIdEquals(Long id1, Long id2);

}
