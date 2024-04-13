package ch.uzh.ifi.hase.soprafs24.controller;

import ch.uzh.ifi.hase.soprafs24.entity.GameSettings;
import ch.uzh.ifi.hase.soprafs24.entity.Player;
import ch.uzh.ifi.hase.soprafs24.entity.User;
import ch.uzh.ifi.hase.soprafs24.rest.dto.*;
import ch.uzh.ifi.hase.soprafs24.rest.mapper.DTOMapper;
import ch.uzh.ifi.hase.soprafs24.service.LobbyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

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

    @MessageMapping("/lobbies/{lobbyId}/join")
    public void addPlayer(@DestinationVariable String lobbyId, @Payload UserTokenDTO userTokenDTO) {
        // convert tokenDTO to user
        User userToAdd = DTOMapper.INSTANCE.convertUserTokenDTOtoEntity(userTokenDTO);
        // add user to lobby as a player
        List<Player> players = lobbyService.addPlayer(lobbyId, userToAdd);
        // update clients with new player-list
        this.updatePlayerList(lobbyId, players);
    }

    @MessageMapping("/lobbies/{lobbyID}/leave")
    public void removePlayer(@DestinationVariable String lobbyId, @Payload UserTokenDTO userTokenDTO) {
        // convert tokenDTO to user
        User userToRemove = DTOMapper.INSTANCE.convertUserTokenDTOtoEntity(userTokenDTO);
        // remove user from lobby
        List<Player> players = lobbyService.removePlayer(lobbyId, userToRemove);
        // update clients with new player-list
        this.updatePlayerList(lobbyId, players);
    }

    /** Server to client(s) communication **/
    public void updatePlayerList(String lobbyId, List<Player> players) {
        System.out.println("Sending updated player list to clients");
        // convert player list to DTO list
        List<PlayerGetDTO> playerGetDTOS = new ArrayList<PlayerGetDTO>();
        for (Player player : players) {
            playerGetDTOS.add(DTOMapper.INSTANCE.convertEntityToPlayerGetDTO(player));
        }
        // send DTOs of updated player-list to clients
        msgTemplate.convertAndSend(
                String.format("/topic/lobbies/%s/players", lobbyId),
                playerGetDTOS
        );
    }

    public void updateSettings(String lobbyId, GameSettings settings) {
        GameSettingsDTO gameSettingsDTO = DTOMapper.INSTANCE.convertGameSettingsToGameSettingsDTO(settings);

        msgTemplate.convertAndSend(
                String.format("/topic/lobbies/%s/settings", lobbyId),
                gameSettingsDTO
        );
    }

    public void updateLobbyState(String lobbyId, Boolean isGameRunning, Boolean isLobbyFull) {
        LobbyStateDTO lobbyStateDTO = new LobbyStateDTO();
        lobbyStateDTO.setIsGameRunning(isGameRunning);
        lobbyStateDTO.setIsLobbyFull(isLobbyFull);

        msgTemplate.convertAndSend(
                String.format("/topic/lobbies/%s/settings", lobbyId),
                lobbyStateDTO
        );
    }
}
