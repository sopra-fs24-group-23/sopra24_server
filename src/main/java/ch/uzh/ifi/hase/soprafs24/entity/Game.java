package ch.uzh.ifi.hase.soprafs24.entity;

import ch.uzh.ifi.hase.soprafs24.categories.Category;
import ch.uzh.ifi.hase.soprafs24.categories.CategoryFactory;
import ch.uzh.ifi.hase.soprafs24.constant.GamePhase;
import ch.uzh.ifi.hase.soprafs24.exceptions.PlayerNotFoundException;

import java.util.*;
import java.util.concurrent.*;

public class Game {

    private final List<Player> players;
    private final GameSettings settings;
    private Integer currentRoundNumber;
    private GamePhase currentPhase;

    public void setCurrentLetter(String currentLetter) {
        this.currentLetter = currentLetter;
    }

    private String currentLetter;
    private final HashMap<String, Integer> answerMap;
    private volatile boolean playerHasAnswered;
    private boolean inputPhaseClosed = false;
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
    private List<String> recentLetters = new ArrayList<>();

    // to make finding users and answers easier and more efficient, we should probably introduce answer IDs, and
    // change the player list to a map or similar.

    public Game(GameSettings settings, List<Player> players) {
        this.settings = settings;
        this.players = players;
        this.answerMap = new HashMap<>();
        this.currentRoundNumber = 0;
        this.playerHasAnswered = false;
    }

    public GameSettings getSettings() {
        return this.settings;
    }

    public List<Player> getPlayers() {
        return this.players;
    }

    public boolean initializeRound() {
        if (currentRoundNumber < settings.getMaxRounds()) {
            currentRoundNumber++;
            playerHasAnswered = false;
            answerMap.clear();
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

                Category answerCategory = CategoryFactory.createCategory(answer.getCategory());

                CompletableFuture<Void> checkFuture = answer.checkAnswer(answerCategory, this.currentLetter)
                    .thenApply((isCorrect) -> {
                        answer.setIsCorrect(isCorrect);

                        answer.setIsUnique(answerMap.get(answer.getAnswer()) <= 1);

                        // Calculate and update the score
                        int score = answer.calculateScore();
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
        String letter;
        int attempts = 0;
        do {
            letter = String.valueOf((char) ('A' + random.nextInt(26)));
            attempts++;
            if (attempts > 10) { // Fallback to sequential search after 10 attempts
                for (int i = 0; i < 26; i++) {
                    letter = String.valueOf((char) ('A' + i));
                    if (!recentLetters.contains(letter)) {
                        break;
                    }
                }
            }
        } while (recentLetters.contains(letter));
    
        // Update recent letters list
        if (recentLetters.size() >= 10) { // Assuming we keep track of the last 10 letters
            recentLetters.remove(0); // Remove the oldest letter
        }
        recentLetters.add(letter);
    
        return letter;
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

        answers.forEach((answer) -> {
            Integer count = answerMap.get(answer.getAnswer());
            if (count == null) {
                answerMap.put(answer.getAnswer(), 1);
            }
            else {
                count++;
                answerMap.put(answer.getAnswer(), count);
            }
        });

        for (Player p : players) {
            if (p.getUsername().equals(username)) {
                p.setHasAnswered(true);
                p.setCurrentAnswers(answers);
            }
        }
    }

    // this is pretty disgusting... wopsieee
    public void doubtAnswers(String username, List<Vote> votes) throws PlayerNotFoundException {
        boolean playerExists = false;
        for (Player p : players) {
            if (p.getUsername().equals(username)) {
                playerExists = true;
                break;
            }
        }
        if (!playerExists) {
            throw new PlayerNotFoundException("Player not found");
        }
        for (Vote vote : votes) {
            for (Player p : players) {
                if (p.getUsername().equals(vote.getUsername())) {
                    for (Answer a : p.getCurrentAnswers()) {
                        if (a.getCategory().equals(vote.getCategory())) {
                            a.setIsDoubted(true);
                        }
                    }
                }
            }
        }
        for (Player p : players) {
            if (p.getUsername().equals(username)) {
                p.setHasVoted(true);
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
