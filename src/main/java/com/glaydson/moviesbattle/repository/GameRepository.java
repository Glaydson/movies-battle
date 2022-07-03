package com.glaydson.moviesbattle.repository;

import com.glaydson.moviesbattle.entity.Game;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GameRepository extends JpaRepository<Game, Long> {

    Game findByDateTimeEndIsNullAndPlayer(String player);

}
