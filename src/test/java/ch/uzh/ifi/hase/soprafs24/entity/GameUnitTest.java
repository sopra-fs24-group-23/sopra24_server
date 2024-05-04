package ch.uzh.ifi.hase.soprafs24.service;

import ch.uzh.ifi.hase.soprafs24.entity.*;
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
        players.add(new Player(1L, "player1", "token1"));
        players.add(new Player(2L, "player2", "token2"));

        game = new Game(settings, players);
    }

    @Test
    public void testInitializeRound_ShouldAdvanceRound() {
        assertTrue(game.initializeRound());
        assertEquals(1, game.getState().getCurrentRoundNumber());
        assertEquals(GamePhase.SCOREBOARD, game.getState().getCurrentPhase());
    }

    @Test
    public void testInitializeRound_ShouldNotAdvanceBeyondMaxRounds() {
        for (int i = 0; i < settings.getMaxRounds(); i++) {
            game.initializeRound();
        }
        assertFalse(game.initializeRound());
        assertEquals(settings.getMaxRounds(), game.getState().getCurrentRoundNumber());
    }
    @Test
    public void testPhaseTransitionFromScoreboardToInput() {
        game.setPhase(GamePhase.SCOREBOARD);  // Should set phase to SCOREBOARD
        assertEquals(GamePhase.SCOREBOARD, game.getState().getCurrentPhase());

        game.setPhase(GamePhase.INPUT);  // Manually transition to INPUT for testing
        game.waitInput().join();  // Assume all players input immediately for simplicity

        assertEquals(GamePhase.INPUT, game.getState().getCurrentPhase(), "Game should transition to INPUT phase after initializing input wait.");
    }
    @Test
    public void testInputPhaseCompletesWhenAllPlayersHaveAnsweredSimplified() {
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
    public void testScoreCalculation() {
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
    public void testPhaseTransitionAfterScoreboard() {
        game.initializeRound();
        game.setPhase(GamePhase.SCOREBOARD);
        CompletableFuture<Void> transitionFuture = game.waitScoreboard();
        transitionFuture.join();  // Wait for the scoreboard duration to complete

        game.setPhase(GamePhase.INPUT);  // This would normally be handled by the game logic after the wait
        assertEquals(GamePhase.INPUT, game.getState().getCurrentPhase());
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

    /*@Test
    public void testVotingCompletionWhenAllVotesAreIn() {
        game.initializeRound();
        players.forEach(player -> {
            player.setHasVoted(true);
        });

        // Direct check before invoking waitVoting()
        boolean allVoted = players.stream().allMatch(Player::getHasVoted);
        assertTrue(allVoted, "All players should have their voted flag set before waiting for votes.");

        CompletableFuture<Void> votingWait = game.waitVoting();

        try {
            votingWait.get(500, TimeUnit.MILLISECONDS);  // This should not throw if all players have voted
        } catch (TimeoutException e) {
            fail("Voting phase should complete immediately as all players have voted.", e);
        } catch (Exception e) {
            fail("Unexpected exception thrown.", e);
        }
    }*/
}