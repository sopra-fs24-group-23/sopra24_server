package ch.uzh.ifi.hase.soprafs24.entity;

import ch.uzh.ifi.hase.soprafs24.exceptions.PlayerNotFoundException;
import ch.uzh.ifi.hase.soprafs24.constant.GamePhase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import static org.junit.jupiter.api.Assertions.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class GameUnitTest {

    private Game game;
    private GameSettings settings;
    private List<Player> players;

    @BeforeEach
    public void setup() {
        settings = new GameSettings();
        settings.setMaxRounds(5);
        settings.setInputDuration(2); // duration for input in seconds
        settings.setVotingDuration(2); // duration for voting in seconds
        settings.setScoreboardDuration(2); // duration for scoreboard display in seconds

        players = new ArrayList<>();
        players.add(new Player(1L, "player1", "token1", "#000000"));
        players.add(new Player(2L, "player2", "token2", "#000000"));

        game = new Game(settings, players);
    }

    @Test
    public void initializeRound_normalSetting_setsCorrectValues() {
        assertTrue(game.initializeRound());
        assertEquals(1, game.getState().getCurrentRoundNumber());
        assertEquals(GamePhase.SCOREBOARD, game.getState().getCurrentPhase());
    }

    @Test
    public void initializeRound_maxRoundsReached_willNotProceed() {
        for (int i = 0; i < settings.getMaxRounds(); i++) {
            game.initializeRound();
        }
        assertFalse(game.initializeRound());
        assertEquals(settings.getMaxRounds(), game.getState().getCurrentRoundNumber());
    }

    @Test
    public void waitInput_ifAllAnswered_completeFuture() {
        game.initializeRound();
        players.forEach(player -> {
            game.setPlayerAnswers(player.getUsername(), Arrays.asList(new Answer("Category", "Answer")));
        });

        // Directly set playerHasAnswered to simulate immediate response
        game.setPlayerHasAnswered(true);

        CompletableFuture<Void> inputWait = game.waitInput();

        // Check the future without delay since we're simulating immediate completion
        assertDoesNotThrow(() -> inputWait.get(500, TimeUnit.MILLISECONDS), "Input phase should complete immediately as all players have answered.");
    }

    @Test
    public void waitScoreboard_ifAllPlayersReady_skipsTimer() {
        game.initializeRound();
        CompletableFuture<Void> scoreboardWait = game.waitScoreboard();

        players.forEach(player -> game.setPlayerReady(player.getUsername()));

        assertDoesNotThrow(() -> scoreboardWait.get(2, TimeUnit.SECONDS));
    }

    @Test
    public void calculateScores_validInputs_calculatesCorrectScores() {
        game.initializeRound();
        game.setCurrentLetter("D");
        Player player1 = players.get(0);
        Player player2 = players.get(1);
        Answer answer1 = new Answer("Country", "Denmark");
        Answer answer2 = new Answer("Country", "Denmark");  // Duplicate answer to test uniqueness
        answer2.setIsJoker(false);
        answer1.setIsJoker(false);
        answer2.setIsDoubted(false);
        answer1.setIsDoubted(false);
        game.setPlayerAnswers(player1.getUsername(), Arrays.asList(answer1));
        game.setPlayerAnswers(player2.getUsername(), Arrays.asList(answer2));


        CompletableFuture<Void> scoreFuture = game.calculateScores();
        scoreFuture.join();  // Ensure the future completes

        // Assuming a scoring mechanism where correct, unique answers get a score of 10 and non-unique ones get 5
        assertEquals(5, player1.getCurrentScore());
        assertEquals(5, player2.getCurrentScore());
    }

    @Test
    public void testVotingForNonExistentPlayer() {
        game.initializeRound();
        Exception exception = assertThrows(PlayerNotFoundException.class, () -> {
            game.doubtAnswers("nonExistentPlayer", Arrays.asList(new Vote()));
        });

        assertTrue(exception.getMessage().contains("Player not found"));
    }

    @Test
    public void testVotingForValidPlayerWhoVoted() throws PlayerNotFoundException {
        game.initializeRound();
        Vote vote = new Vote();

        Answer answer1 = new Answer("Country", "Denmark");
        game.setPlayerAnswers(players.get(0).getUsername(), Arrays.asList(answer1));

        vote.setUsername(players.get(0).getUsername());
        vote.setCategory("Country");

        game.doubtAnswers(players.get(0).getUsername(), Arrays.asList(vote));
    }

    @Test
    public void testCapitalizedDuplicateAnswerNonUnique() {
        List<Answer> player1Answers = new ArrayList<>();
        List<Answer> player2Answers = new ArrayList<>();

        Answer city1 = new Answer("City", "Paris");
        Answer city2 = new Answer("City", "paris");

        Answer country1 = new Answer("Country", "poLand");
        Answer country2 = new Answer("Country", "Poland");

        player1Answers.add(city1);
        player1Answers.add(country1);

        player2Answers.add(city2);
        player2Answers.add(country2);

        game.setPlayerAnswers("player1", player1Answers);
        game.setPlayerAnswers("player2", player2Answers);

        game.setCurrentLetter("P");

        game.calculateScores();

        List<Player> players = game.getPlayers();

        for (Player player : players) {
            for (Answer answer : player.getCurrentAnswers()) {
                assertFalse(answer.getIsUnique());
            }
        }
    }

    @Test
    public void testLeadingOrTrailingSpacesAnswerNonUnique() {
        List<Answer> player1Answers = new ArrayList<>();
        List<Answer> player2Answers = new ArrayList<>();

        Answer city1 = new Answer("City", " Paris");
        Answer city2 = new Answer("City", "Paris ");

        Answer country1 = new Answer("Country", "  Poland");
        Answer country2 = new Answer("Country", "Poland");

        player1Answers.add(city1);
        player1Answers.add(country1);

        player2Answers.add(city2);
        player2Answers.add(country2);

        game.setPlayerAnswers("player1", player1Answers);
        game.setPlayerAnswers("player2", player2Answers);

        game.setCurrentLetter("P");

        game.calculateScores();

        List<Player> players = game.getPlayers();

        for (Player player : players) {
            for (Answer answer : player.getCurrentAnswers()) {
                assertFalse(answer.getIsUnique());
            }
        }
    }

    @Test
    public void testWaitForVotesPassWhenAllHaveVoted() {
        List<Vote> votes = new ArrayList<>();

        assertDoesNotThrow(() -> {
            game.doubtAnswers("player1", votes);
            game.doubtAnswers("player2", votes);
        });

        CompletableFuture<Void> votingWait = game.waitForVotes();

        try {
            votingWait.get(1, TimeUnit.SECONDS);
        }
        catch (Exception e) {
            fail("waitForVotes did not resolve correctly.", e);
        }
    }

    @Test
    public void testSetPlayerReady() {
        game.setPlayerReady("player1");
        game.setPlayerReady("player2");

        List<Player> players = game.getPlayers();

        for (Player player : players) {
            assertTrue(player.isReady());
        }
    }

    @Test
    public void testRemovePlayer() {
        User user = new User();
        user.setToken("token1");

        game.removePlayer(user);

        List<Player> players = game.getPlayers();

        for (Player player : players) {
            assertNotEquals(player.getUsername(), "player1");
        }
    }
}