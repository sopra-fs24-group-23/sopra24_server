package ch.uzh.ifi.hase.soprafs24.controller;

import ch.uzh.ifi.hase.soprafs24.constant.GamePhase;
import ch.uzh.ifi.hase.soprafs24.entity.GameState;
import ch.uzh.ifi.hase.soprafs24.entity.Player;
import ch.uzh.ifi.hase.soprafs24.events.GameStateChangeEvent;
import ch.uzh.ifi.hase.soprafs24.rest.dto.GameStateDTO;
import ch.uzh.ifi.hase.soprafs24.rest.dto.PlayerGetDTO;
import ch.uzh.ifi.hase.soprafs24.rest.mapper.DTOMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import java.util.ArrayList;
import java.util.List;

@Controller
public class GameWebsocketController {

    @Autowired
    private SimpMessagingTemplate msgTemplate;

    @EventListener
    public void handleGameStateChange(GameStateChangeEvent gameStateChangeEvent) {
        this.updateGameState(gameStateChangeEvent.getGameId(), gameStateChangeEvent.getGameState());
    }

    /** Server to client(s) communication **/
    private void updateGameState(String lobbyId, GameState gameState) {
        GameStateDTO gameStateDTO = new GameStateDTO();
        // convert Players
        List<PlayerGetDTO> playerGetDTOS = new ArrayList<PlayerGetDTO>();
        for (Player player : gameState.getPlayers()) {
            playerGetDTOS.add(DTOMapper.INSTANCE.convertEntityToPlayerGetDTO(player));
        }
        // set other fields
        gameStateDTO.setGamePhase(gameState.getCurrentPhase());
        gameStateDTO.setCurrentLetter(gameState.getCurrentLetter());
        gameStateDTO.setPlayers(playerGetDTOS);
        gameStateDTO.setCurrentRoundNumber(gameState.getCurrentRoundNumber());

        // send DTO
        msgTemplate.convertAndSend(
                String.format("/topic/games/%s/phase", lobbyId),
                gameStateDTO
        );
    }
}
