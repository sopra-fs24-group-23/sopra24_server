package ch.uzh.ifi.hase.soprafs24.service;

import ch.uzh.ifi.hase.soprafs24.entity.Game;
import ch.uzh.ifi.hase.soprafs24.entity.GameSettings;
import ch.uzh.ifi.hase.soprafs24.entity.Lobby;
import ch.uzh.ifi.hase.soprafs24.rest.dto.GameSettingsDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class GameSettingsService {

    private final LobbyService lobbyService;

    @Autowired
    public GameSettingsService(LobbyService lobbyService) {
        this.lobbyService = lobbyService;
    }

    public void updateSettings(String lobbyId, GameSettings settings) {
        // Get the game associated with the lobby
        Lobby lobby = lobbyService.getLobbyById(lobbyId);
        lobby.setSettings(settings);
    }
}
