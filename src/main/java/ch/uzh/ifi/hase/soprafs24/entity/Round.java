package ch.uzh.ifi.hase.soprafs24.entity;

import java.util.List;
import java.util.Random;

public class Round {
    private Integer roundNumber;
    private String[] letters;
    private Integer timer;
    private List<String> answers;
    private String startLetter;
    private int points;
    private Game mainGame;
    private boolean roundEnded = false;

    public Round(Integer roundNumber, Game mainGame) {
        this.roundNumber = roundNumber;
        this.letters = new String[] {"A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L", "M",
        "N", "O", "P", "Q", "R", "S", "T", "U", "V", "W", "X", "Y", "Z"};
        this.startLetter = generateRandomLetter();
        this.mainGame = mainGame;
    }

    public void startRound() {
        // Reset or initialize points and answers for the new round
        this.points = 0;
        this.answers.clear();

        // Generate a new start letter for the round
        this.startLetter = generateRandomLetter();

        // Reset or set timer based on the game settings
        this.timer = mainGame.getSettings().getMaxRoundsDuration();

        // Set round as not ended
        this.roundEnded = false;

        // Notify the game/players that the round has started
       // mainGame.notifyPlayers("Round " + roundNumber + "started with letter: " + startLetter);
    }
    public void endRound() {
        // Set round as ended
        this.roundEnded = true;

        // Calculate the scores

        // Notify the game/players that the round has ended
        //mainGame.notifyPlayers("Round " + roundNumber + " ended.");
    }

    public String generateRandomLetter() {
        Random rand = new Random();
        int randomIndex = rand.nextInt(26);

        return letters[randomIndex];
    }

    public void displayAnswers() {}
    public void checkAnswers() {}
    public void calculateScores() {}

    public boolean isRoundEnded() {
        return roundEnded;
    }

    public void setRoundEnded(boolean gameEnded) {
        this.roundEnded = gameEnded;
    }
}
