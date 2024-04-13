package ch.uzh.ifi.hase.soprafs24.controller;

import ch.uzh.ifi.hase.soprafs24.constant.GamePhase;
import ch.uzh.ifi.hase.soprafs24.entity.Player;
import ch.uzh.ifi.hase.soprafs24.rest.dto.GameStateDTO;
import ch.uzh.ifi.hase.soprafs24.rest.dto.PlayerGetDTO;
import ch.uzh.ifi.hase.soprafs24.rest.mapper.DTOMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import java.util.ArrayList;
import java.util.List;

@Controller
public class GameWebsocketController {

    @Autowired
    private SimpMessagingTemplate msgTemplate;

    /** Server to client(s) communication **/
    public void updateGameState(String lobbyId, GamePhase gamePhase, List<Player> players) {

        List<PlayerGetDTO> playerGetDTOS = new ArrayList<PlayerGetDTO>();
        for (Player player : players) {
            playerGetDTOS.add(DTOMapper.INSTANCE.convertEntityToPlayerGetDTO(player));
        }

        GameStateDTO gameStateDTO = new GameStateDTO();
        gameStateDTO.setGamePhase(gamePhase);
        gameStateDTO.setPlayers(playerGetDTOS);

        msgTemplate.convertAndSend(
                String.format("/topic/games/%s/phase", lobbyId),
                gameStateDTO
        );
    }
}
