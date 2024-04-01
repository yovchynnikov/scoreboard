package org.test.scoreboard;

import org.test.scoreboard.exceptions.MatchNotFoundException;
import org.test.scoreboard.exceptions.TeamAlreadyPlaysException;
import org.test.scoreboard.model.Match;
import org.test.scoreboard.model.Score;
import org.test.scoreboard.model.TeamsPair;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentSkipListSet;

public class ScoreboardImpl implements Scoreboard {

    private final Set<Match> orderedMatches = new ConcurrentSkipListSet<>(
            Comparator.<Match, Integer>comparing(m -> m.getScore().totalScore())
                    .thenComparing(Match::getStartedAt)
                    .reversed());

    private final Map<String, String> teams = new ConcurrentHashMap<>();

    private final Map<TeamsPair, Match> matches = new ConcurrentHashMap<>();

    @Override
    public Match createMatch(String teamHome, String teamGuest) {
        validateMatchName(teamHome);
        validateMatchName(teamGuest);
        if (teams.containsKey(teamHome) || teams.containsKey(teamGuest)) {
            throw new TeamAlreadyPlaysException();
        }
        TeamsPair key = constructKey(teamHome, teamGuest);
        Match result = new Match(Instant.now(), teamHome, teamGuest, Score.EMPTY_SCORE);
        teams.put(teamHome, teamHome);
        teams.put(teamGuest, teamGuest);
        orderedMatches.add(result);
        matches.put(key, result);
        return result;
    }

    private void validateMatchName(String teamName) {
        if (teamName == null || teamName.trim().isEmpty()) {
            throw new IllegalArgumentException("Team name cannot be null/empty");
        }
    }

    @Override
    public void updateMatch(String teamHome, String teamGuest, Score score) {
        validateScores(score);
        TeamsPair key = constructKey(teamHome, teamGuest);
        Match oldMatch = matches.remove(key);
        if (oldMatch == null) {
            throw new MatchNotFoundException();
        }
        Match newMatch = oldMatch.withScore(score);
        orderedMatches.remove(oldMatch);
        orderedMatches.add(newMatch);
        matches.put(key, newMatch);
    }

    private void validateScores(Score score) {
        if (score.teamHome() < 0 || score.teamGuest() < 0) {
            throw new IllegalArgumentException("Score cannot negative");
        }
    }

    @Override
    public void finishMatch(String teamHome, String teamGuest) {
        TeamsPair key = constructKey(teamHome, teamGuest);
        Match existed = matches.remove(key);
        if (existed == null) {
            throw new MatchNotFoundException();
        }
        orderedMatches.remove(existed);
        teams.remove(existed.getTeamHome());
        teams.remove(existed.getTeamGuest());
    }

    @Override
    public List<Match> getScoreboard() {
        return new ArrayList<>(orderedMatches);
    }

    private static TeamsPair constructKey(String teamHome, String teamGuest) {
        return new TeamsPair(teamHome, teamGuest);
    }
}
