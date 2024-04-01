package org.test.scoreboard.model;

import java.time.Instant;

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
