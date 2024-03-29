package ch.uzh.ifi.hase.soprafs24.rest.dto;

public class LobbyStateDTO {
    private Boolean isLobbyFull;
    private Boolean isGameRunning;

    public Boolean getIsLobbyFull() {
        return isLobbyFull;
    }

    public void setIsLobbyFull(Boolean lobbyFull) {
        isLobbyFull = lobbyFull;
    }

    public Boolean getIsGameRunning() {
        return isGameRunning;
    }

    public void setIsGameRunning(Boolean gameRunning) {
        isGameRunning = gameRunning;
    }
}
