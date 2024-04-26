package ch.uzh.ifi.hase.soprafs24.controller;

import ch.uzh.ifi.hase.soprafs24.entity.Lobby;
import ch.uzh.ifi.hase.soprafs24.entity.Player;
import ch.uzh.ifi.hase.soprafs24.entity.User;
import ch.uzh.ifi.hase.soprafs24.rest.dto.LobbyIdDTO;
import ch.uzh.ifi.hase.soprafs24.rest.dto.PlayerGetDTO;
import ch.uzh.ifi.hase.soprafs24.rest.dto.UserTokenDTO;
import ch.uzh.ifi.hase.soprafs24.rest.mapper.DTOMapper;
import ch.uzh.ifi.hase.soprafs24.service.LobbyService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
public class LobbyController {

    private final LobbyService lobbyService;

    public LobbyController(LobbyService lobbyService) {
        this.lobbyService = lobbyService;
    }

    @PostMapping("/lobbies")
    @ResponseStatus(HttpStatus.CREATED)
    @ResponseBody
    public LobbyIdDTO createLobby(@RequestBody UserTokenDTO userTokenDTO) {
        User host = DTOMapper.INSTANCE.convertUserTokenDTOtoEntity(userTokenDTO);
        Lobby createdLobby = lobbyService.createLobby(host);
        return DTOMapper.INSTANCE.convertEntityToLobbyIdDTO(createdLobby);
    }

    @DeleteMapping("/lobbies/{lobbyId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteLobby(@PathVariable String lobbyId, UserTokenDTO userTokenDTO) {
        User userToken = DTOMapper.INSTANCE.convertUserTokenDTOtoEntity(userTokenDTO);
        lobbyService.deleteLobby(lobbyId, userToken);
    }

    // throws a 404 if lobbyId is invalid, else does nothing
    @GetMapping("/lobbies/{lobbyId}")
    public void checkLobbyId(@PathVariable String lobbyId) {
        lobbyService.checkLobbyId(lobbyId);
    }

    @PostMapping("/lobbies/{lobbyId}/join")
    @ResponseStatus(HttpStatus.OK)
    public void addPlayer(@PathVariable String lobbyId, @RequestBody UserTokenDTO userTokenDTO) {
        User userToAdd = DTOMapper.INSTANCE.convertUserTokenDTOtoEntity(userTokenDTO);
        lobbyService.addPlayer(lobbyId, userToAdd);
    }

    @GetMapping("/lobbies/{lobbyId}/host")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public PlayerGetDTO getHostName(@PathVariable String lobbyId) {
        Player host = lobbyService.getHost(lobbyId);
        return DTOMapper.INSTANCE.convertEntityToPlayerGetDTO(host);
    }
}
