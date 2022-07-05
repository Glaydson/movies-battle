package com.glaydson.moviesbattle.repository;

import com.glaydson.moviesbattle.entity.Game;
import org.springframework.data.repository.CrudRepository;

import java.time.LocalDateTime;

public interface GameRepository extends CrudRepository<Game, Long> {

    Game findByDateTimeEndIsNullAndPlayer(String player);

}
