package com.glaydson.moviesbattle.repository;

import com.glaydson.moviesbattle.entity.Movie;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;

public interface MovieRepository extends JpaRepository<Movie, Long> {


}
