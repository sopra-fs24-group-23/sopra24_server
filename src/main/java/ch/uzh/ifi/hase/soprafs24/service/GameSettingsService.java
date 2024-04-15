package ch.uzh.ifi.hase.soprafs24.service;

import ch.uzh.ifi.hase.soprafs24.entity.Game;
import ch.uzh.ifi.hase.soprafs24.entity.GameSettings;
import ch.uzh.ifi.hase.soprafs24.rest.dto.GameSettingsDTO;
import org.springframework.stereotype.Service;

@Service
public class GameSettingsService {

    public GameSettings getSettings(Game game){
        return game.getSettings();
    }
    public GameSettings updateSettings(GameSettingsDTO settingsDTO) {
        GameSettings settings = convertDTOToEntity(settingsDTO);
        return settings;
    }

    public GameSettings convertDTOToEntity(GameSettingsDTO settingsDTO){
        GameSettings settings = new GameSettings();
        //settings.setCategories(settingsDTO.getCategories());
        settings.setMaxRounds(settingsDTO.getMaxRounds());
        settings.setVotingDuration(settingsDTO.getVotingDuration());
        settings.setMaxRoundsDuration(settingsDTO.getMaxRoundsDuration());
        settings.setScoreboardDuration(settingsDTO.getScoreboardDuration());
        settings.setMaxPlayers(settingsDTO.getMaxPlayers());

        return settings;
    }
    public GameSettingsDTO convertEntityToDTO(GameSettings settings){
        GameSettingsDTO settingsDTO = new GameSettingsDTO();
        //GameSettingsDTO.setCategories(settings.getCategories());
        settingsDTO.setMaxRounds(settings.getMaxRounds());
        settingsDTO.setVotingDuration(settings.getVotingDuration());
        settingsDTO.setMaxRoundsDuration(settings.getMaxRoundsDuration());
        settingsDTO.setScoreboardDuration(settings.getScoreboardDuration());
        settingsDTO.setMaxPlayers(settings.getMaxPlayers());

        return settingsDTO;
    }
}