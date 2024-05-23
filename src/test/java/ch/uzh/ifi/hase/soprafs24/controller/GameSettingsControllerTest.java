package ch.uzh.ifi.hase.soprafs24.controller;

import static org.mockito.Mockito.*;

import ch.uzh.ifi.hase.soprafs24.entity.GameSettings;
import ch.uzh.ifi.hase.soprafs24.rest.dto.GameSettingsDTO;
import ch.uzh.ifi.hase.soprafs24.service.GameService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.messaging.simp.SimpMessagingTemplate;

public class GameSettingsControllerTest {

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
    public void updateSettings_validInputs_informClients() {
        GameSettingsDTO gameSettingsDTO = new GameSettingsDTO();
        gameSettingsDTO.setInputDuration(10);
        gameSettingsDTO.setScoreboardDuration(10);
        gameSettingsDTO.setVotingDuration(10);
        gameSettingsDTO.setMaxPlayers(5);
        gameSettingsDTO.setMaxRounds(5);
        gameSettingsDTO.setIsRandom(true);

        controller.updateSettings(lobbyId, gameSettingsDTO);

        Mockito.verify(messagingTemplate, Mockito.times(1)).convertAndSend(
                eq("/topic/lobbies/ABCD/settings"), any(GameSettingsDTO.class)
        );
    }

    @Test
    public void informClients_validInputs_sendsMessageToClients() {
        GameSettings settings = new GameSettings();

        Mockito.when(gameService.getSettings(lobbyId)).thenReturn(settings);

        controller.informClients(lobbyId);

        Mockito.verify(messagingTemplate, Mockito.times(1)).convertAndSend(
                eq("/topic/lobbies/ABCD/settings"), any(GameSettingsDTO.class)
        );
    }

}
