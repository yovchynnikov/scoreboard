package org.test.scoreboard.model;

import java.time.Instant;
import java.util.Objects;

public class Match {

    private final Instant startedAt;

    private final String teamHome;
    private final String teamGuest;

    private final Score score;

    public Match(Instant startedAt, String teamHome, String teamGuest) {
        this(startedAt, teamHome, teamGuest, Score.EMPTY_SCORE);
    }

    public Match(Instant startedAt, String teamHome, String teamGuest, Score score) {
        this.startedAt = startedAt;
        this.teamHome = teamHome;
        this.teamGuest = teamGuest;
        this.score = score;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Match match = (Match) o;
        return Objects.equals(startedAt, match.startedAt) && Objects.equals(teamHome, match.teamHome) && Objects.equals(teamGuest, match.teamGuest) && Objects.equals(score, match.score);
    }

    @Override
    public int hashCode() {
        return Objects.hash(startedAt, teamHome, teamGuest, score);
    }

    public Instant getStartedAt() {
        return startedAt;
    }

    public String getTeamHome() {
        return teamHome;
    }

    public String getTeamGuest() {
        return teamGuest;
    }

    public Score getScore() {
        return score;
    }
}
