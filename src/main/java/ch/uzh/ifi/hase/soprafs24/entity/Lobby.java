package ch.uzh.ifi.hase.soprafs24.entity;

import ch.uzh.ifi.hase.soprafs24.exceptions.LobbyFullException;

import java.util.ArrayList;
import java.util.List;

public class Lobby {
    private List<Player> players;
    private Player host;
    private GameSettings settings;
    private Boolean isGameRunning;

    public Lobby(Player host) {
        // initialize fields
        this.host = host;
        this.settings = new GameSettings();
        this.isGameRunning = false;
        // initialize player list and add host directly
        this.players = new ArrayList<>();
        this.players.add(host);
    }

    public void startGame() { // removed parameters here (see UML); can be got from attributes

    }

    public List<Player> getPlayers() {
        return this.players;
    }

    public void addPlayer(Player player) throws LobbyFullException {
        // if lobby is not full - add player
        if (this.players.size() > this.settings.getMaxPlayers()) {
            this.players.add(player);
        }
        // if lobby is full - throw exception
        else {
            throw new LobbyFullException("The lobby is full, the player could not be added.");
        }
    }

    public void removePlayer(Player player) {
        // TODO: check if exception handling necessary
        this.players.remove(player);
    }

    public Player getHost() {
        return host;
    }

    public void setHost(Player host) {
        this.host = host;
    }

    public GameSettings getSettings() {
        return settings;
    }

    public void setSettings(GameSettings settings) {
        this.settings = settings;
    }

    public Boolean getGameRunning() {
        return isGameRunning;
    }

    public void setGameRunning(Boolean gameRunning) {
        isGameRunning = gameRunning;
    }
}
