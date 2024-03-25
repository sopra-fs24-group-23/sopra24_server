package ch.uzh.ifi.hase.soprafs24.controller;

import ch.uzh.ifi.hase.soprafs24.entity.Player;
import ch.uzh.ifi.hase.soprafs24.rest.dto.PlayerGetDTO;
import ch.uzh.ifi.hase.soprafs24.rest.dto.PlayerPostDTO;
import ch.uzh.ifi.hase.soprafs24.rest.mapper.DTOMapper;
import ch.uzh.ifi.hase.soprafs24.service.PlayerService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

/**
 * User Controller
 * This class is responsible for handling all REST request that are related to
 * the user.
 * The controller will receive the request and delegate the execution to the
 * UserService and finally return the result.
 */
@RestController
public class PlayerController {

  private final PlayerService playerController;

  PlayerController(PlayerService playerController) {
    this.playerController = playerController;
  }

  @GetMapping("/users")
  @ResponseStatus(HttpStatus.OK)
  @ResponseBody
  public List<PlayerGetDTO> getAllUsers() {
    // fetch all users in the internal representation
    List<Player> players = playerController.getUsers();
    List<PlayerGetDTO> playerGetDTOS = new ArrayList<>();

    // convert each user to the API representation
    for (Player player : players) {
      playerGetDTOS.add(DTOMapper.INSTANCE.convertEntityToPlayerGetDTO(player));
    }
    return playerGetDTOS;
  }

  @PostMapping("/users")
  @ResponseStatus(HttpStatus.CREATED)
  @ResponseBody
  public PlayerGetDTO createUser(@RequestBody PlayerPostDTO playerPostDTO) {
    // convert API user to internal representation
    Player playerInput = DTOMapper.INSTANCE.convertPlayerPostDTOtoEntity(playerPostDTO);

    // create user
    Player createdPlayer = playerController.createUser(playerInput);
    // convert internal representation of user back to API
    return DTOMapper.INSTANCE.convertEntityToPlayerGetDTO(createdPlayer);
  }
}
