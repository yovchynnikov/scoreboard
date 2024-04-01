package org.test.scoreboard;

import org.test.scoreboard.exceptions.MatchNotFoundException;
import org.test.scoreboard.exceptions.TeamAlreadyPlaysException;
import org.test.scoreboard.model.Match;
import org.test.scoreboard.model.Score;

import java.util.List;

public interface Scoreboard {

    Match createMatch(String teamHome, String teamGuest) throws TeamAlreadyPlaysException;

    void updateMatch(String teamHome, String teamGuest, Score score) throws MatchNotFoundException;

    void finishMatch(String teamHome, String teamGuest) throws MatchNotFoundException;

    List<Match> getScoreboard();
}
