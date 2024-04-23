package ch.uzh.ifi.hase.soprafs24.entity;

import ch.uzh.ifi.hase.soprafs24.constant.GamePhase;
import ch.uzh.ifi.hase.soprafs24.exceptions.PlayerNotFoundException;
import org.springframework.scheduling.annotation.Async;

import java.util.*;
import java.util.concurrent.*;

public class Game {

    private List<Player> players;
    private GameSettings settings;
    private Integer currentRoundNumber;
    private GamePhase currentPhase;
    private String currentLetter;
    private volatile boolean playerHasAnswered = false;
    private boolean inputPhaseClosed = false;
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

    // to make finding users and answers easier and more efficient, we should probably introduce answer IDs, and
    // change the player list to a map or similar.

    public Game(GameSettings settings, List<Player> players) {
        this.settings = settings;
        this.players = players;
        this.currentRoundNumber = 0;
        this.playerHasAnswered = false;
    }

    public GameSettings getSettings() {
        return this.settings;
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
        return String.valueOf((char) ('A' + random.nextInt(26)));
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

    public void setPlayerAnswers(String username, List<Answer> answers) {
        System.out.printf("Setting answers for %s \n", username);
        answers.forEach((answer) -> System.out.println(answer.toString()));
        for (Player p : players) {
            if (p.getUsername().equals(username)) {
                p.setHasAnswered(true);
                p.setCurrentAnswers(answers);
            }
        }
    }

    // this is pretty disgusting... wopsieee
    public void doubtAnswers(List<Vote> votes) throws PlayerNotFoundException {
        for (Vote vote : votes) {
            Player player = null;
            for (Player p : players) {
                if (vote.getUsername().equals(p.getUsername())) {
                    player = p;
                }
            }
            if (player != null) {
                for (Answer a : player.getCurrentAnswers()) {
                    if (a.getCategory().equals(vote.getCategory())) {
                        a.setDoubted(true);
                    }
                }
            }
            else {
                throw new PlayerNotFoundException("A vote referenced a non-existent player");
            }
        }
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
