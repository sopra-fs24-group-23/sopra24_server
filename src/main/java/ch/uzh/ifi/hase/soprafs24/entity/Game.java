package ch.uzh.ifi.hase.soprafs24.entity;

import ch.uzh.ifi.hase.soprafs24.constant.GamePhase;
import org.springframework.scheduling.annotation.Async;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.*;
import java.util.Map;

public class Game {

    private List<Player> players;
    private GameSettings settings;
    private Integer currentRoundNumber;
    private GamePhase currentPhase;
    private String currentLetter;
    private boolean playerHasAnswered = false;
    private boolean inputPhaseClosed = false;
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

            // reset player answers and flags
            for(Player player : players) {
                player.setCurrentAnswers(new ArrayList<>());
                player.setHasAnswered(false);
                player.setHasVoted(false);
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
        return waitForDuration(settings.getScoreboardDuration().longValue());
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
    public CompletableFuture<Void> waitForAnswers() {
        CompletableFuture<Void> future = new CompletableFuture<>();

        // schedule check every second; complete future if all answers received
        ScheduledFuture<?> scheduledFuture = scheduler.scheduleAtFixedRate(() -> {
            boolean allAnswersReceived = true;
            for (Player player : players ) {
                if (!player.getHasAnswered()) {
                    allAnswersReceived = false;
                    break;
                }
            }

            if (allAnswersReceived) {
                future.complete(null);
            }

        }, 0, 1, TimeUnit.SECONDS);

        return future;
    }
    @Async
    public CompletableFuture<Void> waitVoting() {
        return waitForDuration(settings.getVotingDuration().longValue());
    }

    @Async
    public CompletableFuture<Void> waitForVotes() {
        CompletableFuture<Void> future = new CompletableFuture<>();

        // schedule check every second; complete future if all votes received
        ScheduledFuture<?> scheduledFuture = scheduler.scheduleAtFixedRate(() -> {
            boolean allVotesReceived = true;
            for (Player player : players ) {
                if (!player.getHasVoted()) {
                    allVotesReceived = false;
                    break;
                }
            }

            if (allVotesReceived) {
                future.complete(null);
            }

        }, 0, 1, TimeUnit.SECONDS);

        return future;
    }

    public void handleAnswers(Map<String, List<Answer>> answers) {
        for (Map.Entry<String, List<Answer>> entry : answers.entrySet()) {
            String playerId = entry.getKey();
            List<Answer> playerAnswers = entry.getValue();
            // Find player by ID and set their answers
            for (Player player : players) {
                if (player.getId().equals(playerId)) {
                    player.setCurrentAnswers(playerAnswers);
                    player.setHasAnswered(true);
                    break;
                }
            }
        }
        // Check if all players have answered to move to the next phase
        boolean allAnswered = players.stream().allMatch(Player::getHasAnswered);
        if (allAnswered) {
            setPhase(GamePhase.VOTING);
            // Inform clients about the phase change
            //updateClients(/* gameId, gameState */);
        }
    }


    /* HELPER METHODS */

    /** Generate a random uppercase letter **/

    @Async
    public CompletableFuture<Void> waitForDuration(Long duration) {
        CompletableFuture<Void> future = new CompletableFuture<>();

        // schedule timeout logic after X seconds
        scheduler.schedule(() -> {
            future.complete(null);
        }, duration, TimeUnit.SECONDS);

        return future;
    }
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

    public boolean getPlayerHasAnswered() {
        return this.playerHasAnswered;
    }

    public boolean isInputPhaseClosed() {
        return inputPhaseClosed;
    }

    public void setInputPhaseClosed(boolean inputPhaseClosed) {
        this.inputPhaseClosed = inputPhaseClosed;
    }

}
