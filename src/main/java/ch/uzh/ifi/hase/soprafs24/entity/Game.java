package ch.uzh.ifi.hase.soprafs24.entity;

import java.util.ArrayList;
import java.util.List;
import javax.persistence.*;
import java.io.Serial;
import java.io.Serializable;

public class Game {

    private Long GameId;

    private Lobby lobby;

    private List<Player> players;

    private GameSettings settings;

    private Round[] rounds;

    private int currentRound;


    public Game(GameSettings settings, Lobby lobby) {
        this.settings = settings;
        this.lobby = lobby;
        this.players = lobby.getPlayers();
        this.rounds = new Round[settings.getMaxRounds()];
        this.currentRound = 0;
    }

    public void startGame() throws IllegalStateException {
        if (this.players.size() < 2 ) {
            throw new IllegalStateException("Cannot start game: Not enough players.");
        }
        if (this.lobby.getIsGameRunning()) {
            throw new IllegalStateException("Game is already running.");
        }
        // Start the game logic
        this.lobby.setIsGameRunning(true);
        // Initialize game rounds etc.
    }

    public void endGame() {

    }

    public void addPlayer(Player player) {
        players.add(player);
    }

    public void removePlayer(Player player) {
        players.remove(player);
    }
}
