package org.test.scoreboard.model;

public record Score(int teamHome, int teamGuest) {

    public static final Score EMPTY_SCORE = new Score(0, 0);

}
