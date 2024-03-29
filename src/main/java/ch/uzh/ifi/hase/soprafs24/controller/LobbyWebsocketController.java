package ch.uzh.ifi.hase.soprafs24.controller;

import ch.uzh.ifi.hase.soprafs24.entity.Player;
import ch.uzh.ifi.hase.soprafs24.entity.User;
import ch.uzh.ifi.hase.soprafs24.rest.dto.PlayerGetDTO;
import ch.uzh.ifi.hase.soprafs24.rest.dto.UserTokenDTO;
import ch.uzh.ifi.hase.soprafs24.rest.mapper.DTOMapper;
import ch.uzh.ifi.hase.soprafs24.service.LobbyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import java.util.ArrayList;
import java.util.List;

@Controller
public class LobbyWebsocketController {

    @Autowired
    private SimpMessagingTemplate msgTemplate;

    private final LobbyService lobbyService;

    public LobbyWebsocketController(LobbyService lobbyService) {
        this.lobbyService = lobbyService;
    }

    @MessageMapping("/{lobbyId}/players/add")
    public void addPlayer(@DestinationVariable String lobbyId, UserTokenDTO userTokenDTO) {
        User userToAdd = DTOMapper.INSTANCE.convertUserTokenDTOtoEntity(userTokenDTO);
        lobbyService.addPlayer(lobbyId, userToAdd);
    }

    /** Server to client(s) communication **/
    public void updatePlayerList(String lobbyId, List<Player> players) {
        // convert player list to DTO list
        List<PlayerGetDTO> playerGetDTOS = new ArrayList<PlayerGetDTO>();
        for (Player player : players) {
            playerGetDTOS.add(DTOMapper.INSTANCE.convertEntityToPlayerGetDTO(player));
        }

        // send DTOs of updated player-list to clients
        msgTemplate.convertAndSend(
                String.format("/lobbies/%d/players", lobbyId),
                playerGetDTOS
        );
    }
}
