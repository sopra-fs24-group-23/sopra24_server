package ch.uzh.ifi.hase.soprafs24.entity;

import ch.uzh.ifi.hase.soprafs24.exceptions.LobbyFullException;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Lobby {

    private String id;
    private List<Player> players;
    private Player host;
    private GameSettings settings;
    private Boolean isGameRunning;
    private Boolean isLobbyFull;

    public Lobby(Player host) {
        // initialize fields
        this.id = UUID.randomUUID().toString();
        this.host = host;
        this.settings = new GameSettings();
        this.isGameRunning = false;
        this.isLobbyFull = false;
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
        if (!isLobbyFull & !isGameRunning) {
            this.players.add(player);
            // if this player took the last spot, set lobby full
            if (this.players.size() == this.settings.getMaxPlayers()) {
                this.setLobbyFull(true);
            }
        }
        // if lobby is full - throw exception
        else {
            throw new LobbyFullException("Sorry, the lobby you are trying to join is full.");
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

    public Boolean getLobbyFull() {
        return isLobbyFull;
    }

    public void setLobbyFull(Boolean lobbyFull) {
        isLobbyFull = lobbyFull;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
