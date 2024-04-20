package ch.uzh.ifi.hase.soprafs24.controller;

import ch.uzh.ifi.hase.soprafs24.entity.User;
import ch.uzh.ifi.hase.soprafs24.rest.dto.PlayerGetDTO;
import ch.uzh.ifi.hase.soprafs24.rest.dto.UserGetDTO;
import ch.uzh.ifi.hase.soprafs24.rest.mapper.DTOMapper;
import ch.uzh.ifi.hase.soprafs24.service.UserService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@RestController
public class LeaderboardController {

    private final UserService userService;

    public LeaderboardController(UserService userService) {
        this.userService = userService;
    }

    /**  Endpoints to query specific range of leaderboards
     * return top 10 by default, take request-param to query custom range. **/

    @GetMapping("/leaderboards/games-played")
    public List<UserGetDTO> getGamesPlayedRanking(@RequestParam(required = false) Long range) {
        long queriedRange = range == null ? 10L : range;

        List<User> users = userService.getGamesPlayedRanking(queriedRange);

        return convertUsers(users);
    }

    @GetMapping("/leaderboards/total-score")
    public List<UserGetDTO> getTotalScoreRanking(@RequestParam(required = false) Long range) {
        long queriedRange = range == null ? 10L : range;

        List<User> users = userService.getTotalScoreRanking(queriedRange);

        return convertUsers(users);
    }

    @GetMapping("/leaderboards/games-won")
    public List<UserGetDTO> getGamesWonRanking(@RequestParam(required = false) Long range) {
        long queriedRange = range == null ? 10L : range;

        List<User> users = userService.getGamesWonRanking(queriedRange);

        return convertUsers(users);
    }

    /* HELPERS */
    private List<UserGetDTO> convertUsers(List<User> users) {
        ArrayList<UserGetDTO> userGetDTOS = new ArrayList<>();

        for (User user : users) {
            userGetDTOS.add(DTOMapper.INSTANCE.convertEntityToUserGetDTO(user));
        }

        return userGetDTOS;
    }
}
