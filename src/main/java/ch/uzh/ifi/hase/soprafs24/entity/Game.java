package ch.uzh.ifi.hase.soprafs24.entity;

import ch.uzh.ifi.hase.soprafs24.constant.GamePhase;
import org.springframework.scheduling.annotation.Async;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.*;

public class Game {

    private List<Player> players;
    private GameSettings settings;
    private Integer currentRoundNumber;
    private GamePhase currentPhase;
    private String currentLetter;
    private boolean playerHasAnswered;
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);


    public Game(GameSettings settings, List<Player> players) {
        this.settings = settings;
        this.players = players;
        this.currentRoundNumber = 0;
        this.playerHasAnswered = false;
    }

    public boolean initializeRound() {
        if (currentRoundNumber < settings.getMaxRounds()) {
            currentRoundNumber++;
            playerHasAnswered = false;
            currentPhase = GamePhase.SCOREBOARD;
            currentLetter = generateRandomLetter();

            // reset player answers
            for(Player player : players) {
                player.setCurrentAnswers(new ArrayList<>());
            }

            return true;
        }
        return false;
    }

    @Async
    public CompletableFuture<Void> calculateScores(){
        CompletableFuture<Void> future = new CompletableFuture<>();

        for (Player player : players) {
            for (Answer answer : player.getCurrentAnswers()) {
                player.setCurrentScore(
                        player.getCurrentScore() + answer.calculateScore()
                );
            }
        }
        return future;
    }

    @Async
    public CompletableFuture<Void> waitScoreboard() {
        CompletableFuture<Void> future = new CompletableFuture<>();

        // schedule timeout logic after X seconds
        scheduler.schedule(() -> {
            future.complete(null);
        }, settings.getScoreboardDuration().longValue(), TimeUnit.SECONDS);

        return future;
    }

    @Async
    public CompletableFuture<Void> waitInput() {
        CompletableFuture<Void> future = new CompletableFuture<>();

        // schedule condition check every second
        ScheduledFuture<?> scheduledFuture = scheduler.scheduleAtFixedRate(() -> {
            if (playerHasAnswered) {
                future.complete(null);
            }
        }, 0, 1, TimeUnit.SECONDS);

        // schedule timeout logic after X seconds
        scheduler.schedule(() -> {
            scheduledFuture.cancel(false);
            if (!future.isDone()) {
                future.complete(null);
            }
        }, settings.getInputDuration().longValue(), TimeUnit.SECONDS);

        return future;
    }

    @Async
    public CompletableFuture<Void> waitVoting() {
        CompletableFuture<Void> future = new CompletableFuture<>();

        // schedule timeout logic after X seconds
        scheduler.schedule(() -> {
            future.complete(null);
        }, settings.getVotingDuration().longValue(), TimeUnit.SECONDS);

        return future;
    }

    /* HELPER METHODS */

    /** Generate a random uppercase letter **/
    private String generateRandomLetter() {
        Random random = new Random();
        return ("A" + random.nextInt(26));
    }

    /* GETTERS / SETTERS */

    public GameState getState() {
        return new GameState(
                currentLetter,
                currentPhase,
                players,
                currentRoundNumber
        );
    }

    public void setPhase(GamePhase phase) {
        this.currentPhase = phase;
    }

    public void setPlayerHasAnswered(boolean input) {
        this.playerHasAnswered = input;
    }

}
