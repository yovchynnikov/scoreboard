package org.test.scoreboard;

import org.test.scoreboard.exceptions.MatchNotFound;
import org.test.scoreboard.exceptions.TeamAlreadyPlays;
import org.test.scoreboard.model.Match;
import org.test.scoreboard.model.Score;

import java.util.List;

public interface Scoreboard {

    Match createMatch(String teamA, String teamB) throws TeamAlreadyPlays;

    Match updateMatch(String teamA, String teamB, Score score) throws MatchNotFound;

    Match finishMatch(String teamA, String teamB) throws MatchNotFound;

    List<Match> getScoreboard();
}
