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
import org.mockito.MockitoAnnotations;
import org.springframework.messaging.simp.SimpMessagingTemplate;

public class GameSettingsControllerTest {

    @Mock
    private GameSettingsService gameSettingsService;

    @Mock
    private GameService gameService;

    @Mock
    private SimpMessagingTemplate messagingTemplate;

    @InjectMocks
    private GameSettingsController controller;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        controller = new GameSettingsController(gameSettingsService, gameService);
    }


    @Test
    void testInformClients() {
        // Given
        String lobbyId = "lobbyId";
        GameSettings settings = new GameSettings();
        settings.setMaxPlayers(5);
        when(gameService.getSettings(lobbyId)).thenReturn(settings);

        // When
        controller.informClients(lobbyId);


        // Then
        verify(messagingTemplate).convertAndSend(eq("/topic/lobbies/lobbyId/settings"), any(GameSettingsDTO.class));
        verify(gameService).getSettings(lobbyId);
    }
    @Test
    void testUpdateSettings() {
        // Given
        String lobbyId = "lobbyId";
        GameSettingsDTO settingsDTO = new GameSettingsDTO();
        settingsDTO.setMaxPlayers(4);
        GameSettings settings = new GameSettings();
        settings.setMaxPlayers(4);

        when(DTOMapper.INSTANCE.convertGameSettingsDTOToGameSettings(settingsDTO)).thenReturn(settings);
        doNothing().when(gameSettingsService).updateSettings(eq(lobbyId), any(GameSettings.class));

        // When
        controller.updateSettings(lobbyId, settingsDTO);

        // Then
        verify(gameSettingsService).updateSettings(lobbyId, settings);
        verify(messagingTemplate).convertAndSend(eq("/topic/lobbies/lobbyId/settings"), any(GameSettingsDTO.class));
    }

}
