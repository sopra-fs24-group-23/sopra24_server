package ch.uzh.ifi.hase.soprafs24.service;

import ch.uzh.ifi.hase.soprafs24.entity.Game;
import ch.uzh.ifi.hase.soprafs24.entity.GameSettings;
import ch.uzh.ifi.hase.soprafs24.rest.dto.GameSettingsDTO;

public class GameSettingsService {

    public GameSettings updateSettings(GameSettingsDTO settingsDTO) {
        GameSettings settings = convertDTOToEntity(settingsDTO);
        return settings;
    }

    private GameSettings convertDTOToEntity(GameSettingsDTO settingsDTO){
        GameSettings settings = new GameSettings();
        //settings.setCategories(settingsDTO.getCategories());
        settings.setMaxRounds(settingsDTO.getMaxRounds());
        settings.setVotingDuration(settingsDTO.getVotingDuration());
        settings.setMaxRoundsDuration(settingsDTO.getMaxRoundsDuration());
        settings.setScoreboardDuration(settingsDTO.getScoreboardDuration());
        settings.setMaxPlayers(settingsDTO.getMaxPlayers());

        return settings;
    }
}
