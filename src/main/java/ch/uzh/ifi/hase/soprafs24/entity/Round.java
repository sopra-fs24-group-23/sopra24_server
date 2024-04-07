package ch.uzh.ifi.hase.soprafs24.entity;

import java.util.List;

public class Round {
    private Integer roundNumber;
    private String letter;
    private Integer timer;
    private List<String> answers;

    private Game mainGame;

    public Round(Integer roundNumber, String letter, Game mainGame) {
        this.roundNumber = roundNumber;
        this.letter = letter;
        this.mainGame = mainGame;
    }

    public void startRound() {

    }
    public void endRound() {

    }
    public void displayAnswers() {}
    public void checkAnswers() {}
    public void calculateScores() {}
}
