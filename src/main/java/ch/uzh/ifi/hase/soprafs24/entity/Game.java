package ch.uzh.ifi.hase.soprafs24.entity;

import java.util.List;

public class Game {

    private Long GameId;

    private Lobby lobby;

    private List<Player> players;

    private GameSettings settings;

    private Round[] rounds;

    private int currentRound;

    private boolean gameEnded = false;


    public Game(Lobby lobby) {
        this.settings = lobby.getSettings();
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

    // If a player leaves a game
    public void removePlayer(Player player) {
        // TODO: check if exception handling necessary
        this.players.remove(player);
    }

    public boolean isGameEnded() {
        return gameEnded;
    }

    public void setGameEnded(boolean gameEnded) {
        this.gameEnded = gameEnded;
    }

    public boolean startNextRound() {
        if (currentRound < this.settings.getMaxRounds()) {
            currentRound++;
            // round specific data here
            rounds[currentRound] = new Round(currentRound + 1, this);
            return true;
        }
        else {
            // end of game logic here
            return false;
        }
    }
}
