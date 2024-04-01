package org.test.scoreboard;

import org.apache.commons.lang3.RandomStringUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.test.scoreboard.exceptions.MatchNotFoundException;
import org.test.scoreboard.exceptions.TeamAlreadyPlaysException;
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
        String teamHome = generateRandomTeamName();
        String teamGuest = generateRandomTeamName();
        Instant before = Instant.now();

        Match expected = new Match(before, teamHome, teamGuest, Score.EMPTY_SCORE);

        scoreboard.createMatch(teamHome, teamGuest);

        List<Match> results = scoreboard.getScoreboard();

        assertThat(results).isNotNull().hasSize(1);
        assertThat(results.get(0)).usingRecursiveComparison().ignoringFields("startedAt").isEqualTo(expected);
        assertThat(results.get(0).getStartedAt()).isNotNull().isBeforeOrEqualTo(Instant.now()).isAfterOrEqualTo(before);
    }

    @Test
    void givenNonEmptyScoreboard_andPrevMatchHasZeroScores_andNewMatchCreated_whenGetScoreboard_thenMatchInScoreboardWithCorrectOrder() {
        String teamHome = generateRandomTeamName();
        String teamGuest = generateRandomTeamName();
        Instant before = Instant.now();

        Match expected = new Match(before, teamHome, teamGuest, Score.EMPTY_SCORE);
        scoreboard.createMatch(generateRandomTeamName(), generateRandomTeamName());
        scoreboard.createMatch(teamHome, teamGuest);

        List<Match> results = scoreboard.getScoreboard();

        assertThat(results).isNotNull().hasSize(2);
        assertThat(results.get(0)).usingRecursiveComparison().ignoringFields("startedAt").isEqualTo(expected);
        assertThat(results.get(0).getStartedAt()).isNotNull().isBeforeOrEqualTo(Instant.now()).isAfterOrEqualTo(before);
    }

    @Test
    void givenNonEmptyScoreboard_andPrevMatchHasNonZeroScores_andNewMatchCreated_whenGetScoreboard_thenMatchInScoreboardWithCorrectOrder() {
        String teamHome = generateRandomTeamName();
        String teamGuest = generateRandomTeamName();
        String prevTeamHome = generateRandomTeamName();
        String prevTeamGuest = generateRandomTeamName();
        Instant before = Instant.now();

        scoreboard.createMatch(prevTeamHome, prevTeamGuest);
        scoreboard.updateMatch(prevTeamHome, prevTeamGuest, new Score(1, 1));

        Match expected = new Match(before, teamHome, teamGuest, Score.EMPTY_SCORE);
        scoreboard.createMatch(teamHome, teamGuest);

        List<Match> results = scoreboard.getScoreboard();

        assertThat(results).isNotNull().hasSize(2);
        assertThat(results.get(1)).usingRecursiveComparison().ignoringFields("startedAt").isEqualTo(expected);
        assertThat(results.get(1).getStartedAt()).isNotNull().isBeforeOrEqualTo(Instant.now()).isAfterOrEqualTo(before);
    }

    @Test
    void givenNonEmptyScoreboard_whenCreateMatch_andTeamHomeAlreadyPlays_thenThrowsException() {
        String teamHome = generateRandomTeamName();
        String teamGuest = generateRandomTeamName();

        scoreboard.createMatch(generateRandomTeamName(), teamHome);

        assertThrows(TeamAlreadyPlaysException.class, () -> scoreboard.createMatch(teamHome, teamGuest));
    }

    @Test
    void givenNonEmptyScoreboard_whenCreateMatch_andTeamGuestAlreadyPlays_thenThrowsException() {
        String teamHome = generateRandomTeamName();
        String teamGuest = generateRandomTeamName();

        scoreboard.createMatch(teamGuest, generateRandomTeamName());

        assertThrows(TeamAlreadyPlaysException.class, () -> scoreboard.createMatch(teamHome, teamGuest));
    }

    @Test
    void givenMatchExists_whenMatchUpdated_andGetScoreboard_thenScoreboardReflectsChanges() {
        String teamHome = generateRandomTeamName();
        String teamGuest = generateRandomTeamName();

        Match expected = new Match(Instant.now(), teamHome, teamGuest, new Score(1, 0));

        Match matchCreated = scoreboard.createMatch(teamHome, teamGuest);
        scoreboard.updateMatch(teamHome, teamGuest, new Score(1, 0));

        List<Match> results = scoreboard.getScoreboard();

        assertThat(results).isNotNull().hasSize(1);
        assertThat(results.get(0)).usingRecursiveComparison().ignoringFields("startedAt").isEqualTo(expected);
        assertThat(results.get(0).getStartedAt()).isNotNull().isEqualTo(matchCreated.getStartedAt());
    }

    @Test
    void givenMatchExists_whenMatchUpdated_andGetScoreboard_thenMatchShouldPopupInScoreboard() {
        String teamHome = generateRandomTeamName();
        String teamGuest = generateRandomTeamName();

        Match expected = new Match(Instant.now(), teamHome, teamGuest, new Score(1, 0));

        scoreboard.createMatch(teamHome, teamGuest);
        scoreboard.createMatch(generateRandomTeamName(), generateRandomTeamName());
        scoreboard.updateMatch(teamHome, teamGuest, new Score(1, 0));
        List<Match> results = scoreboard.getScoreboard();

        assertThat(results).as("Scoreboard should have two matches").isNotNull().hasSize(2);
        assertThat(results.get(0)).as("Scoreboard should have expected first match")
                .usingRecursiveComparison()
                .ignoringFields("startedAt")
                .isEqualTo(expected);
    }

    @Test
    void givenMatchNotExists_whenUpdateMatch_thenThrowsException_andMatchNotInScoreboard() {
        String teamHome = generateRandomTeamName();
        String teamGuest = generateRandomTeamName();

        assertThrows(MatchNotFoundException.class, () -> scoreboard.updateMatch(teamHome, teamGuest, new Score(1, 0)));

        List<Match> results = scoreboard.getScoreboard();
        assertThat(results).isNotNull().isEmpty();
    }

    @Test
    void givenMatchExists_whenFinishMatch_thenMatchNotInScoreboard() {
        String teamHome = generateRandomTeamName();
        String teamGuest = generateRandomTeamName();

        scoreboard.createMatch(teamHome, teamGuest);
        scoreboard.finishMatch(teamHome, teamGuest);

        List<Match> results = scoreboard.getScoreboard();
        assertThat(results).isNotNull().isEmpty();
    }

    @Test
    void givenMatchNotExists_whenFinishMatch_thenThrowsException() {
        String teamHome = generateRandomTeamName();
        String teamGuest = generateRandomTeamName();

        assertThrows(MatchNotFoundException.class, () -> scoreboard.finishMatch(teamHome, teamGuest));
    }

    @Test
    void givenMatchFinished_whenCreateMatch_thenMatchInScoreboard() {
        String teamHome = generateRandomTeamName();
        String teamGuest = generateRandomTeamName();

        Match expected = new Match(Instant.now(), teamHome, teamGuest, Score.EMPTY_SCORE);

        scoreboard.createMatch(teamHome, teamGuest);
        scoreboard.finishMatch(teamHome, teamGuest);
        scoreboard.createMatch(teamHome, teamGuest);

        List<Match> results = scoreboard.getScoreboard();

        assertThat(results).isNotNull().hasSize(1);
        assertThat(results.get(0)).usingRecursiveComparison().ignoringFieldsOfTypes(Instant.class).isEqualTo(expected);
    }

    @Test
    void givenMatchFinished_whenCreateMatch_amdTeamNamesInterfered_thenMatchInScoreboard() {
        String teamHome = "a_a";
        String teamGuest = "b";

        Match expected = new Match(Instant.now(), teamHome, teamGuest, Score.EMPTY_SCORE);

        scoreboard.createMatch(teamHome, teamGuest);
        scoreboard.finishMatch(teamHome, teamGuest);
        scoreboard.createMatch(teamHome, teamGuest);

        List<Match> results = scoreboard.getScoreboard();

        assertThat(results).isNotNull().hasSize(1);
        assertThat(results.get(0)).usingRecursiveComparison().ignoringFieldsOfTypes(Instant.class).isEqualTo(expected);
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

    private static String generateRandomTeamName() {
        return RandomStringUtils.randomAlphanumeric(20);
    }

}