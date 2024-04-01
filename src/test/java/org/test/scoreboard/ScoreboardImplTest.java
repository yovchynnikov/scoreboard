package org.test.scoreboard;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.test.scoreboard.exceptions.MatchNotFound;
import org.test.scoreboard.exceptions.TeamAlreadyPlays;
import org.test.scoreboard.model.Match;
import org.test.scoreboard.model.Score;

import java.time.Instant;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

class ScoreboardImplTest {

    private Scoreboard scoreboard;

    @BeforeEach
    void setUp() {
        scoreboard = new ScoreboardImpl();
    }

    @Test
    void givenEmptyScoreboard_whenGetScoreboard_thenScoreboardIsEmpty() {
        List<Match> results = scoreboard.getScoreboard();

        assertThat(results).isNotNull().isEmpty();
    }

    @Test
    void givenEmptyScoreboard_andNewMatchCreated_whenGetScoreboard_thenMatchInScoreboard() {
        String teamHome = "Team A";
        String teamGuest = "Team B";
        Instant before = Instant.now();

        Match expected = new Match(before, teamHome, teamGuest, Score.EMPTY_SCORE);

        scoreboard.createMatch(teamHome, teamGuest);

        List<Match> results = scoreboard.getScoreboard();

        assertThat(results).isNotNull().hasSize(1);
        assertThat(results.get(0)).usingRecursiveAssertion().ignoringFields("startedAt").isEqualTo(expected);
        assertThat(results.get(0).getStartedAt()).isNotNull().isBeforeOrEqualTo(Instant.now()).isAfterOrEqualTo(before);
    }

    @Test
    void givenNonEmptyScoreboard_andPrevMatchHasZeroScores_andNewMatchCreated_whenGetScoreboard_thenMatchInScoreboardWithCorrectOrder() {
        String teamHome = "Team A";
        String teamGuest = "Team B";
        Instant before = Instant.now();

        Match expected = new Match(before, teamHome, teamGuest, Score.EMPTY_SCORE);
        scoreboard.createMatch("prevMatchTeamHome", "prevMatchTeamGuest");
        scoreboard.createMatch(teamHome, teamGuest);

        List<Match> results = scoreboard.getScoreboard();

        assertThat(results).isNotNull().hasSize(2);
        assertThat(results.get(0)).usingRecursiveAssertion().ignoringFields("startedAt").isEqualTo(expected);
        assertThat(results.get(0).getStartedAt()).isNotNull().isBeforeOrEqualTo(Instant.now()).isAfterOrEqualTo(before);
    }

    @Test
    void givenNonEmptyScoreboard_andPrevMatchHasNonZeroScores_andNewMatchCreated_whenGetScoreboard_thenMatchInScoreboardWithCorrectOrder() {
        String teamHome = "Team A";
        String teamGuest = "Team B";
        Instant before = Instant.now();

        scoreboard.createMatch("prevMatchTeamHome", "prevMatchTeamGuest");
        scoreboard.updateMatch("prevMatchTeamHome", "prevMatchTeamGuest", new Score(1, 1));

        Match expected = new Match(before, teamHome, teamGuest, Score.EMPTY_SCORE);
        scoreboard.createMatch(teamHome, teamGuest);

        List<Match> results = scoreboard.getScoreboard();

        assertThat(results).isNotNull().hasSize(2);
        assertThat(results.get(1)).usingRecursiveAssertion().ignoringFields("startedAt").isEqualTo(expected);
        assertThat(results.get(1).getStartedAt()).isNotNull().isBeforeOrEqualTo(Instant.now()).isAfterOrEqualTo(before);
    }

    @Test
    void givenNonEmptyScoreboard_whenCreateMatch_andTeamHomeAlreadyPlays_thenThrowsException() {
        String teamHome = "Team A";
        String teamGuest = "Team B";

        scoreboard.createMatch("prevMatchTeamGuest", teamHome);

        assertThrows(TeamAlreadyPlays.class, () -> scoreboard.createMatch(teamHome, teamGuest));
    }

    @Test
    void givenNonEmptyScoreboard_whenCreateMatch_andTeamGuestAlreadyPlays_thenThrowsException() {
        String teamHome = "Team A";
        String teamGuest = "Team B";

        scoreboard.createMatch(teamGuest, "prevMatchTeamHome");

        assertThrows(TeamAlreadyPlays.class, () -> scoreboard.createMatch(teamHome, teamGuest));
    }

    @Test
    void givenMatchExists_whenMatchUpdated_andGetScoreboard_thenScoreboardReflectsChanges() {
        String teamHome = "Team A";
        String teamGuest = "Team B";

        Match expected = new Match(Instant.now(), teamHome, teamGuest, new Score(1, 0));

        Match matchCreated = scoreboard.createMatch(teamHome, teamGuest);
        scoreboard.updateMatch(teamHome, teamGuest, new Score(1, 0));

        List<Match> results = scoreboard.getScoreboard();

        assertThat(results).isNotNull().hasSize(1);
        assertThat(results.get(0)).usingRecursiveAssertion().ignoringFields("startedAt").isEqualTo(expected);
        assertThat(results.get(0).getStartedAt()).isNotNull().isEqualTo(matchCreated.getStartedAt());
    }

    @Test
    void givenMatchExists_whenMatchUpdated_andGetScoreboard_thenMatchShouldPopupInScoreboard() {
        String teamHome = "Team A";
        String teamGuest = "Team B";

        Match expected = new Match(Instant.now(), teamHome, teamGuest, new Score(1, 0));

        scoreboard.createMatch(teamHome, teamGuest);
        scoreboard.createMatch("newTeamHome", "newTeamGuest");
        scoreboard.updateMatch(teamHome, teamGuest, new Score(1, 0));

        List<Match> results = scoreboard.getScoreboard();

        assertThat(results).isNotNull().hasSize(2);
        assertThat(results.get(0)).usingRecursiveAssertion().ignoringFields("startedAt").isEqualTo(expected);
    }

    @Test
    void givenMatchNotExists_whenUpdateMatch_thenThrowsException_andMatchNotInScoreboard() {
        String teamHome = "Team A";
        String teamGuest = "Team B";

        assertThrows(MatchNotFound.class, () -> scoreboard.updateMatch(teamHome, teamGuest, new Score(1, 0)));

        List<Match> results = scoreboard.getScoreboard();
        assertThat(results).isNotNull().isEmpty();
    }

    @Test
    void givenMatchExists_whenFinishMatch_thenMatchNotInScoreboard() {
        String teamHome = "Team A";
        String teamGuest = "Team B";

        scoreboard.createMatch(teamHome, teamGuest);
        scoreboard.finishMatch(teamHome, teamGuest);

        List<Match> results = scoreboard.getScoreboard();
        assertThat(results).isNotNull().isEmpty();
    }

    @Test
    void givenMatchNotExists_whenFinishMatch_thenThrowsException() {
        String teamHome = "Team A";
        String teamGuest = "Team B";

        assertThrows(MatchNotFound.class, () -> scoreboard.finishMatch(teamHome, teamGuest));
    }

    @Test
    void givenMatchFinished_whenCreateMatch_thenMatchInScoreboard() {
        String teamHome = "Team A";
        String teamGuest = "Team B";

        Match expected = new Match(Instant.now(), teamHome, teamGuest, Score.EMPTY_SCORE);

        scoreboard.createMatch(teamHome, teamGuest);
        scoreboard.finishMatch(teamHome, teamGuest);
        scoreboard.createMatch(teamHome, teamGuest);

        List<Match> results = scoreboard.getScoreboard();

        assertThat(results).isNotNull().hasSize(1);
        assertThat(results.get(0)).usingRecursiveAssertion().ignoringFields("startedAt").isEqualTo(expected);
    }

    @ParameterizedTest
    @CsvSource({
            ", teamGuest",
            "'', teamGuest",
            "teamHome,",
            "teamHome,''",
            ","
    })
    void whenCreateMatch_andTeamNameIsInvalid_thenThrowsException(String teamHome, String teamGuest) {
        assertThrows(IllegalArgumentException.class, () -> scoreboard.createMatch(teamHome, teamGuest));
    }


}