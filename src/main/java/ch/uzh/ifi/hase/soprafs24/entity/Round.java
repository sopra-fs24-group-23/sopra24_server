package ch.uzh.ifi.hase.soprafs24.entity;

import java.util.List;
import java.util.Random;

public class Round {
    private Integer roundNumber;
    private String[] letters;
    private Integer timer;
    private List<String> answers;
    private String startLetter;

    private Game mainGame;

    public Round(Integer roundNumber, Game mainGame) {
        this.roundNumber = roundNumber;
        this.letters = new String[] {"A", "B", "C", "D"};
        this.startLetter = generateRandomLetter();
        this.mainGame = mainGame;
    }

    public void startRound() {

    }
    public void endRound() {

    }

    public String generateRandomLetter() {
        Random rand = new Random();
        int randomIndex = rand.nextInt(26);

        return letters[randomIndex];
    }

    public void displayAnswers() {}
    public void checkAnswers() {}
    public void calculateScores() {}
}
