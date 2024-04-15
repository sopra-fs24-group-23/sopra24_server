package ch.uzh.ifi.hase.soprafs24.controller;

import ch.uzh.ifi.hase.soprafs24.entity.GameSettings;
import ch.uzh.ifi.hase.soprafs24.rest.dto.GameSettingsDTO;
import ch.uzh.ifi.hase.soprafs24.rest.mapper.DTOMapper;
import ch.uzh.ifi.hase.soprafs24.service.GameSettingsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class GameSettingsController {

    private final GameSettingsService gameSettingsService;

    @Autowired
    private SimpMessagingTemplate msgTemplate;

    public GameSettingsController(GameSettingsService gameSettingsService) {
        this.gameSettingsService = gameSettingsService;
    }

    @MessageMapping("/lobbies/{lobbyID}/settings")
    public void updateSettings(@DestinationVariable String lobbyId, @Payload GameSettingsDTO settingsDTO) {
        GameSettings settings = DTOMapper.INSTANCE.convertGameSettingsDTOToGameSettings(settingsDTO);

        gameSettingsService.updateSettings(lobbyId, settings);
        this.sendSettings(lobbyId, settings);
    }

    private void sendSettings(String lobbyId, GameSettings settings) {
        GameSettingsDTO gameSettingsDTO = DTOMapper.INSTANCE.convertGameSettingsToGameSettingsDTO(settings);

        msgTemplate.convertAndSend(
                String.format("/topic/lobbies/%s/settings", lobbyId),
                gameSettingsDTO
        );
    }
}


// messagemapping
// eventlistener