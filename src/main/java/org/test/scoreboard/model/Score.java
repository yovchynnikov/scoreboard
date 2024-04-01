package org.test.scoreboard.model;

import java.util.Objects;

public record Score(int teamHome, int teamGuest) {

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Score score = (Score) o;
        return teamHome == score.teamHome && teamGuest == score.teamGuest;
    }

    @Override
    public int hashCode() {
        return Objects.hash(teamHome, teamGuest);
    }

    public static final Score EMPTY_SCORE = new Score(0, 0);

    public int totalScore() {
        return teamHome + teamGuest;
    }

}
