package ch.uzh.ifi.hase.soprafs24.rest.dto;

public class LobbyStateDTO {
    private Boolean isLobbyFull;
    private Boolean isGameRunning;

    public Boolean getLobbyFull() {
        return isLobbyFull;
    }

    public void setLobbyFull(Boolean lobbyFull) {
        isLobbyFull = lobbyFull;
    }

    public Boolean getGameRunning() {
        return isGameRunning;
    }

    public void setGameRunning(Boolean gameRunning) {
        isGameRunning = gameRunning;
    }
}
