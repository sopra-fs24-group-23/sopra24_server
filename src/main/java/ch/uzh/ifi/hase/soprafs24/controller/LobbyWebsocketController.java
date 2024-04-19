package ch.uzh.ifi.hase.soprafs24.controller;

import ch.uzh.ifi.hase.soprafs24.entity.GameSettings;
import ch.uzh.ifi.hase.soprafs24.entity.Player;
import ch.uzh.ifi.hase.soprafs24.entity.User;
import ch.uzh.ifi.hase.soprafs24.events.LobbyClosedEvent;
import ch.uzh.ifi.hase.soprafs24.rest.dto.*;
import ch.uzh.ifi.hase.soprafs24.rest.mapper.DTOMapper;
import ch.uzh.ifi.hase.soprafs24.service.LobbyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
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

    @EventListener
    public void handleLobbyClose(LobbyClosedEvent lobbyClosedEvent) {
        msgTemplate.convertAndSend(
                String.format("/topic/lobbies/%s/close", lobbyClosedEvent.getLobbyId()),
                "Lobby Closed."
        );
    }

    @MessageMapping("/lobbies/{lobbyId}/join")
    public void addPlayer(@DestinationVariable String lobbyId, @Payload UserTokenDTO userTokenDTO) {
        // convert tokenDTO to user
        User userToAdd = DTOMapper.INSTANCE.convertUserTokenDTOtoEntity(userTokenDTO);
        // add user to lobby as a player
        List<Player> players = lobbyService.addPlayer(lobbyId, userToAdd);
        // update clients with new player-list
        this.updatePlayerList(lobbyId, players);
        // update clients with current settings
        GameSettings settings = lobbyService.getLobbyById(lobbyId).getSettings();
        this.updateSettings(lobbyId, settings);
    }

    @MessageMapping("/lobbies/{lobbyId}/leave")
    public void removePlayer(@DestinationVariable String lobbyId, @Payload UserTokenDTO userTokenDTO) {
        // convert tokenDTO to user
        User userToRemove = DTOMapper.INSTANCE.convertUserTokenDTOtoEntity(userTokenDTO);
        // remove user from lobby
        List<Player> players = lobbyService.removePlayer(lobbyId, userToRemove);
        // update clients with new player-list
        this.updatePlayerList(lobbyId, players);
    }

    @MessageMapping("/lobbies/{lobbyId}/kick/{usernameToKick}")
    public void kickPlayer(@DestinationVariable String lobbyId,
                           @DestinationVariable String usernameToKick,
                           @Payload UserTokenDTO hostTokenDTO) {
        User host = DTOMapper.INSTANCE.convertUserTokenDTOtoEntity(hostTokenDTO);

        List<Player> updatedPlayers = lobbyService.kickPlayer(lobbyId, host, usernameToKick);

        // updated kicked client to redirect
        this.sendKickMessage(lobbyId, usernameToKick);
        // update remaining clients with new playerlist
        this.updatePlayerList(lobbyId, updatedPlayers);
    }

    /** Server to client(s) communication **/
    public void updatePlayerList(String lobbyId, List<Player> players) {
        System.out.println("Sending updated player list to clients");
        // convert player list to DTO list
        List<PlayerGetDTO> playerGetDTOS = new ArrayList<>();
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

    public void sendKickMessage(String lobbyId, String usernameToKick) {
        msgTemplate.convertAndSend(
                String.format("/queue/lobbies/%s/kick/%s", lobbyId, usernameToKick),
                "player kicked"
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
