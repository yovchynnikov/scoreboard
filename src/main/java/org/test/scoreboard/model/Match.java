package org.test.scoreboard.model;

import java.time.Instant;
import java.util.Objects;

public class Match {

    private final Instant startedAt;

    private final String teamHome;
    private final String teamGuest;

    private final Score score;

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

    @Override
    public String toString() {
        return "Match{" +
                "startedAt=" + startedAt +
                ", teamHome='" + teamHome + '\'' +
                ", teamGuest='" + teamGuest + '\'' +
                ", score=" + score +
                '}';
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

    public Match withScore(Score score) {
        return new Match(this.startedAt, this.teamHome, this.teamGuest, score);
    }
}
