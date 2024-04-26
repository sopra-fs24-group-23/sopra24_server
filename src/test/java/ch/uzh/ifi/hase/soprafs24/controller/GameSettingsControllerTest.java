package ch.uzh.ifi.hase.soprafs24.controller;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import ch.uzh.ifi.hase.soprafs24.entity.GameSettings;
import ch.uzh.ifi.hase.soprafs24.rest.dto.GameSettingsDTO;
import ch.uzh.ifi.hase.soprafs24.rest.mapper.DTOMapper;
import ch.uzh.ifi.hase.soprafs24.service.GameService;
import ch.uzh.ifi.hase.soprafs24.service.GameSettingsService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.messaging.simp.SimpMessagingTemplate;

import java.util.ArrayList;

public class GameSettingsControllerTest {

    @Mock
    private GameSettingsService gameSettingsService;
    @Mock
    private GameService gameService;
    @Mock
    private SimpMessagingTemplate messagingTemplate;
    @InjectMocks
    private GameSettingsController controller;
    private String lobbyId;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
        controller.setMessagingTemplate(messagingTemplate);
        lobbyId = "ABCD";
    }

    @Test
    public void testUpdateSettings() {
        GameSettingsDTO gameSettingsDTO = new GameSettingsDTO();

        controller.updateSettings(lobbyId, gameSettingsDTO);

        Mockito.verify(messagingTemplate, Mockito.times(1)).convertAndSend(
                eq("/topic/lobbies/ABCD/settings"), any(GameSettingsDTO.class)
        );
    }

    @Test
    public void testInformClients() {
        GameSettings settings = new GameSettings();

        Mockito.when(gameService.getSettings(lobbyId)).thenReturn(settings);

        controller.informClients(lobbyId);

        Mockito.verify(messagingTemplate, Mockito.times(1)).convertAndSend(
                eq("/topic/lobbies/ABCD/settings"), any(GameSettingsDTO.class)
        );
    }

}
