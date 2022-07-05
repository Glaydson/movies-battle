package com.glaydson.moviesbattle.service;

import com.glaydson.moviesbattle.entity.Game;
import com.glaydson.moviesbattle.entity.Rank;
import com.glaydson.moviesbattle.repository.RankRepository;
import com.glaydson.moviesbattle.resource.RankResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
public class RankService {

    @Autowired
    private RankRepository repository;

    public void updateRank(Game game) {
        // Points = Number of quizzes * percentage of correct answers
        //Double percOfCorrectAnswers =  game.getTotalPoints().doubleValue() / game.getTotalRounds().doubleValue();

        // Find if the player has a rank
        Optional<Rank> optRank = this.repository.findByPlayer(game.getPlayer());
        Rank rank = optRank.isPresent()? optRank.get() : null;

        if (Objects.isNull(rank)) {
            Rank newRank = new Rank();
            newRank.setPlayer(game.getPlayer());
            newRank.setQuizzesPlayed(1);
            newRank.setRoundsPlayed(game.getTotalRounds().intValue());
            newRank.setPointsEarned(game.getTotalPoints());
            newRank.setPointsRanking(game.getTotalPoints().doubleValue() / game.getTotalRounds().doubleValue());
            this.repository.save(newRank);
        } else {
            rank.setQuizzesPlayed(rank.getQuizzesPlayed() + 1);
            rank.setRoundsPlayed(rank.getRoundsPlayed() + game.getTotalRounds().intValue());
            rank.setPointsEarned(rank.getPointsEarned() + game.getTotalPoints());
            rank.setPointsRanking(rank.getQuizzesPlayed() *
                    (rank.getPointsEarned().doubleValue() / rank.getRoundsPlayed().doubleValue()));
            this.repository.save(rank);
        }
    }

    public List<RankResource> getRanking() {

        List<Rank> allRanks = this.repository.findAllByOrderByPointsRankingDesc();

        List<RankResource> ranking = new ArrayList<>();

        for (Rank rank : allRanks) {
            ranking.add(new RankResource(rank.getPlayer(), rank.getPointsRanking()));
        }
        return ranking;

    }
}
