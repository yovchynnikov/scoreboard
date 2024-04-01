package org.test.scoreboard;

import org.test.scoreboard.model.Match;
import org.test.scoreboard.model.Score;

import java.util.Collection;
import java.util.stream.Stream;

public class ScoreboardImpl implements Scoreboard {

    @Override
    public Match createMatch(String teamA, String teamB) {
        return null;
    }

    @Override
    public Match updateMatch(String teamA, String teamB, Score score) {
        return null;
    }

    @Override
    public Match finishMatch(String teamA, String teamB) {
        return null;
    }

    @Override
    public Collection<Match> getScoreboard() {
        return null;
    }
}
