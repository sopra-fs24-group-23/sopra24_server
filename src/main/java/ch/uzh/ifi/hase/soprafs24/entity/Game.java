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


    public int getCurrentRound() {
        return currentRound;
    }

    public void setCurrentRound(int currentRound) {
        this.currentRound = currentRound;
    }

    public Round[] getRounds() {
        return rounds;
    }

    public void setRounds(Round[] rounds) {
        this.rounds = rounds;
    }

    public GameSettings getSettings() {
        return settings;
    }

    public void setSettings(GameSettings settings) {
        this.settings = settings;
    }

    public List<Player> getPlayers() {
        return players;
    }

    public void setPlayers(List<Player> players) {
        this.players = players;
    }

    public Lobby getLobby() {
        return lobby;
    }

    public void setLobby(Lobby lobby) {
        this.lobby = lobby;
    }

    public Long getGameId() {
        return GameId;
    }

    public void setGameId(Long gameId) {
        GameId = gameId;
    }
}
