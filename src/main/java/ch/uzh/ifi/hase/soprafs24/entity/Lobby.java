package ch.uzh.ifi.hase.soprafs24.entity;

import java.util.List;

public class Lobby {
    private List<Player> players;
    private Player host;
    private GameSettings settings;
    private Boolean isGameRunning;

    public Lobby(Player host) {
        this.host = host;
        this.addPlayer(host);
        this.settings = new GameSettings();
        this.isGameRunning = false;
    }

    public void startGame() { // removed parameters here (see UML); can be got from attributes

    }

    public List<Player> getPlayers() {
        return this.players;
    }

    public void addPlayer(Player player) {
        this.players.add(player);
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
