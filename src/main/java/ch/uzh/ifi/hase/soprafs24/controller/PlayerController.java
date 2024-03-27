package ch.uzh.ifi.hase.soprafs24.controller;

import ch.uzh.ifi.hase.soprafs24.service.PlayerService;
import org.springframework.web.bind.annotation.*;

/**
 * Player Controller
 * This class is responsible for receiving requests regarding player objects
 * - not sure if this is needed, as player objects are non-persisted entities
 */
@RestController
public class PlayerController {

  private final PlayerService playerService;

  PlayerController(PlayerService playerService) {
    this.playerService = playerService;
  }

}

