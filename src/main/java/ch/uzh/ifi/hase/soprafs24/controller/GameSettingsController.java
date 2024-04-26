package ch.uzh.ifi.hase.soprafs24.controller;

import ch.uzh.ifi.hase.soprafs24.entity.GameSettings;
import ch.uzh.ifi.hase.soprafs24.service.GameService;
import ch.uzh.ifi.hase.soprafs24.rest.dto.GameSettingsDTO;
import ch.uzh.ifi.hase.soprafs24.rest.mapper.DTOMapper;
import ch.uzh.ifi.hase.soprafs24.service.GameSettingsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class GameSettingsController {

    private final GameSettingsService gameSettingsService;
    private final GameService gameService;

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    public GameSettingsController(GameSettingsService gameSettingsService, GameService gameService) {
        this.gameSettingsService = gameSettingsService;
        this.gameService = gameService;
    }

    @MessageMapping("/games/{lobbyId}/settings")
    public void informClients(@DestinationVariable String lobbyId) {
        GameSettings settings = gameService.getSettings(lobbyId);
        this.sendSettings(lobbyId, settings);
    }

    @MessageMapping("/lobbies/{lobbyId}/settings")
    public void updateSettings(@DestinationVariable String lobbyId, @Payload GameSettingsDTO settingsDTO) {
        GameSettings settings = DTOMapper.INSTANCE.convertGameSettingsDTOToGameSettings(settingsDTO);

        gameSettingsService.updateSettings(lobbyId, settings);
        this.sendSettings(lobbyId, settings);
    }

    private void sendSettings(String lobbyId, GameSettings settings) {
        GameSettingsDTO gameSettingsDTO = DTOMapper.INSTANCE.convertGameSettingsToGameSettingsDTO(settings);

        messagingTemplate.convertAndSend(
                String.format("/topic/lobbies/%s/settings", lobbyId),
                gameSettingsDTO
        );
    }

    /* Setter for testability */
    public void setMessagingTemplate(SimpMessagingTemplate messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
    }
}