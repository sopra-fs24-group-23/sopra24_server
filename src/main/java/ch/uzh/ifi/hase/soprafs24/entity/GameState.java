package ch.uzh.ifi.hase.soprafs24.entity;

import ch.uzh.ifi.hase.soprafs24.constant.GamePhase;

import java.util.List;

public class GameState {
    private final String currentLetter;
    private final GamePhase currentPhase;
    private final List<Player> players;
    private final Integer currentRoundNumber;

    public GameState(
            String currentLetter,
            GamePhase currentPhase,
            List<Player> players,
            Integer currentRoundNumber
    ) {
        this.currentLetter = currentLetter;
        this.currentPhase = currentPhase;
        this.players = players;
        this.currentRoundNumber = currentRoundNumber;
    }


    public String getCurrentLetter() {
        return currentLetter;
    }

    public GamePhase getCurrentPhase() {
        return currentPhase;
    }

    public List<Player> getPlayers() {
        return players;
    }

    public Integer getCurrentRoundNumber() {
        return currentRoundNumber;
    }
}
