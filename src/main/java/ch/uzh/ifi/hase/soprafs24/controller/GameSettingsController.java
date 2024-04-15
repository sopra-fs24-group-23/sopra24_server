package ch.uzh.ifi.hase.soprafs24.controller;

import ch.uzh.ifi.hase.soprafs24.rest.dto.GameSettingsDTO;
import ch.uzh.ifi.hase.soprafs24.service.GameSettingsService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class GameSettingsController {

    private final GameSettingsService gameSettingsService;

    public GameSettingsController(GameSettingsService gameSettingsService) {
        this.gameSettingsService = gameSettingsService;
    }

    @PostMapping("/game-settings")
    public ResponseEntity<Void> updateGameSettings(@RequestBody GameSettingsDTO gameSettingsDTO) {
        gameSettingsService.updateSettings(gameSettingsDTO);
        return ResponseEntity.ok().build();
    }
}