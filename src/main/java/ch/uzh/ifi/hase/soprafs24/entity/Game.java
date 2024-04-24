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
    private Set<String> answerSet;
    private volatile boolean playerHasAnswered = false;
    private boolean inputPhaseClosed = false;
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

    // to make finding users and answers easier and more efficient, we should probably introduce answer IDs, and
    // change the player list to a map or similar.

    public Game(GameSettings settings, List<Player> players) {
        this.settings = settings;
        this.players = players;
        this.answerSet = new HashSet<>();
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
            answerSet = new HashSet<>();
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

    public CompletableFuture<Void> calculateScores(){
        List<CompletableFuture<Void>> scoreFutures = new ArrayList<>();

        for (Player player : players) {
            for (Answer answer : player.getCurrentAnswers()) {
                CompletableFuture<Void> checkFuture = answer.checkAnswer()
                    .thenApply((isCorrect) -> {
                        answer.setIsCorrect(isCorrect);

                        if (answerSet.contains(answer.getAnswer())) {
                            answer.setIsUnique(false);
                        } else {
                            answer.setIsUnique(true);
                        }

                        // Calculate and update the score
                        int score = answer.calculateScore(this.currentLetter);
                        player.setCurrentScore(player.getCurrentScore() + score);

                        return null;
                    });

                scoreFutures.add(checkFuture);
            }
        }

        System.out.println("calculateScores has finished called.");
        return CompletableFuture.allOf(scoreFutures.toArray(new CompletableFuture[0]));
    }

    public CompletableFuture<Void> waitScoreboard() {
        return waitForDuration(settings.getScoreboardDuration().longValue());
    }

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

    public CompletableFuture<Void> waitForAnswers() {
        CompletableFuture<Void> future = new CompletableFuture<>();

        ScheduledFuture<?>[] holder = new ScheduledFuture<?>[1];

        // schedule check every second; complete future if all answers received
        holder[0] = scheduler.scheduleAtFixedRate(() -> {
            boolean allAnswersReceived = true;
            for (Player player : players ) {
                if (!player.getHasAnswered()) {
                    allAnswersReceived = false;
                    break;
                }
            }

            if (allAnswersReceived) {
                holder[0].cancel(false);
                future.complete(null);
            }

        }, 0, 1, TimeUnit.SECONDS);
        return future;
    }

    public CompletableFuture<Void> waitVoting() {
        return waitForDuration(settings.getVotingDuration().longValue());
    }

    public CompletableFuture<Void> waitForVotes() {
        CompletableFuture<Void> future = new CompletableFuture<>();

        final ScheduledFuture<?>[] holder = new ScheduledFuture<?>[1];

        // schedule check every second; complete future if all votes received
        holder[0] = scheduler.scheduleAtFixedRate(() -> {
            boolean allVotesReceived = true;
            for (Player player : players ) {
                if (!player.getHasVoted()) {
                    allVotesReceived = false;
                    break;
                }
            }

            if (allVotesReceived) {
                holder[0].cancel(false);
                future.complete(null);
            }

        }, 0, 1, TimeUnit.SECONDS);

        return future;
    }

    /* HELPER METHODS */

    /** Generate a random uppercase letter **/

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

        answers.forEach((answer) -> answerSet.add(answer.getAnswer()));

        for (Player p : players) {
            if (p.getUsername().equals(username)) {
                p.setHasAnswered(true);
                p.setCurrentAnswers(answers);
            }
        }
    }

    // this is pretty disgusting... wopsieee
    public void doubtAnswers(String username, List<Vote> votes) throws PlayerNotFoundException {
        for (Vote vote : votes) {
            Player player = null;
            for (Player p : players) {
                if (vote.getUsername().equals(p.getUsername())) {
                    player = p;
                }
                if (p.getUsername().equals(username)) {
                    p.setHasVoted(true);
                }
            }
            if (player != null) {
                for (Answer a : player.getCurrentAnswers()) {
                    if (a.getCategory().equals(vote.getCategory())) {
                        a.setIsDoubted(true);
                    }
                    else {
                        a.setIsDoubted(false);
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
