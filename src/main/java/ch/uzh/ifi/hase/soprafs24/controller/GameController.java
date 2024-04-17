package ch.uzh.ifi.hase.soprafs24.controller;

import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.web.bind.annotation.PathVariable;
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
import ch.uzh.ifi.hase.soprafs24.entity.Player;
import ch.uzh.ifi.hase.soprafs24.entity.Game;
import ch.uzh.ifi.hase.soprafs24.entity.GameSettings;

@RestController
public class GameController {

    private final LobbyService lobbyService;
    private final GameService gameService;

    public GameController(LobbyService lobbyService, GameService gameService) {
        this.lobbyService = lobbyService;
        this.gameService = gameService;
    }

    @MessageMapping("/games/{lobbyId}/start")
    public void startGame(@DestinationVariable String lobbyId) {
        // Retrieve game settings and players from the lobby
        Lobby lobby = lobbyService.getLobbyById(lobbyId);
        GameSettings settings = lobby.getSettings();
        List<Player> players = lobby.getPlayers();
        // Start the game
        gameService.runGame(lobbyId, settings, players);
    }
/*
   @PostMapping("/games")
   @ResponseStatus(HttpStatus.CREATED)
   @ResponseBody
   public GameDTO createGame(@RequestBody LobbyIdDTO lobbyIdDTO) {
    Lobby lobby = lobbyService.getLobbyById(lobbyIdDTO.getId());
    Game createdGame = gameService.createGame(lobby);

    return DTOMapper.INSTANCE.convertEntityToGameDTO(createdGame);
   }

   @PostMapping("/games/{gameId}/rounds/{roundNumber}/end")
   public ResponseEntity<?> endRound(@PathVariable Long gameId, Integer roundNumber) {
       try {

           Game game = gameService.getGameById(gameId);

           // Check if roundNumber mathces the currentRound in the game
           if (game.getCurrentRound() != roundNumber) {
               return ResponseEntity.badRequest().body(String.format("Cannot end round %d, as the current round is %d.",
                       roundNumber, game.getCurrentRound()));
           }

           boolean nextRoundStarted = game.startNextRound();
           if (!nextRoundStarted) {
               game.setGameEnded(true);
               // Save points, or if a player has left etc.
               // gameService.saveGame(game);
               return ResponseEntity.ok().body("Game has ended. No more rounds to start.");
           }

           return ResponseEntity.ok()
                   .body(String.format("Round %d ended successfully. Next round started.", roundNumber));
       } catch (Exception e) {
           return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                   .body("An error occured while trying to end the round");
       }
   }
 */
}

