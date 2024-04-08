package ch.uzh.ifi.hase.soprafs24.controller;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import ch.uzh.ifi.hase.soprafs24.rest.dto.GameDTO;

import ch.uzh.ifi.hase.soprafs24.rest.dto.LobbyIdDTO;
import ch.uzh.ifi.hase.soprafs24.rest.mapper.DTOMapper;
import ch.uzh.ifi.hase.soprafs24.service.LobbyService;
import ch.uzh.ifi.hase.soprafs24.service.GameService;
import ch.uzh.ifi.hase.soprafs24.entity.Lobby;
import ch.uzh.ifi.hase.soprafs24.entity.Game;

@RestController
public class GameController {

    private final LobbyService lobbyService;
    private final GameService gameService;

    public GameController(LobbyService lobbyService, GameService gameService) {
        this.lobbyService = lobbyService;
        this.gameService = gameService;
    }


   @PostMapping("/games")
   @ResponseStatus(HttpStatus.CREATED)
   @ResponseBody
   public GameDTO createGame(@RequestBody LobbyIdDTO lobbyIdDTO) {
    Lobby lobby = lobbyService.getLobbyById(lobbyIdDTO.getId());
    Game createdGame = gameService.createGame(lobby);

    return DTOMapper.INSTANCE.convertEntityToGameDTO(createdGame);
   }
}
