package com.glaydson.moviesbattle.repository;

import com.glaydson.moviesbattle.entity.Rank;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Optional;

public interface RankRepository extends CrudRepository<Rank, Long> {

    Optional<Rank> findByPlayer(String player);

    List<Rank> findAllByOrderByPointsRankingDesc();

}
