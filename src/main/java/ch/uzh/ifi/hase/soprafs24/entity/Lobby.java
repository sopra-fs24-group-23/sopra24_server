package ch.uzh.ifi.hase.soprafs24.entity;

import ch.uzh.ifi.hase.soprafs24.exceptions.LobbyFullException;
import ch.uzh.ifi.hase.soprafs24.exceptions.UnauthorizedException;

import java.util.*;

public class Lobby {

    private String id;
    private HashMap<String, Player> players;
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
        this.players = new HashMap<String, Player>();
    }

    public List<Player> getPlayers() {
        return new ArrayList<>(this.players.values());
    }

    public void addPlayer(Player player) throws LobbyFullException {
        // if lobby is not full - add player
        if (!isLobbyFull & !isGameRunning) {
            this.players.put(player.getToken(), player);
            // if this player took the last spot, set lobby full
            if (this.players.size() == this.settings.getMaxPlayers()) {
                this.setIsLobbyFull(true);
            }
        }
        // if lobby is full - throw exception
        else {
            throw new LobbyFullException("Sorry, the lobby you are trying to join is full.");
        }
    }

    public Player removePlayer(String token) {
        return this.players.remove(token);
    }

    public void kickPlayer(String hostToken, String usernameToKick) throws UnauthorizedException {
        if (this.host.getToken().equals(hostToken)) {
            for (Map.Entry<String, Player> entry : players.entrySet()) {
                if (entry.getValue().getUsername().equals(usernameToKick)) {
                    players.remove(entry.getKey());
                }
            }
        }
        else {
            throw new UnauthorizedException("You tried to kick a player, but you are not the host of the lobby.");
        }
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

    public Boolean getIsGameRunning() {
        return isGameRunning;
    }

    public void setIsGameRunning(Boolean gameRunning) {
        isGameRunning = gameRunning;
    }

    public Boolean getIsLobbyFull() {
        return isLobbyFull;
    }

    public void setIsLobbyFull(Boolean lobbyFull) {
        isLobbyFull = lobbyFull;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
