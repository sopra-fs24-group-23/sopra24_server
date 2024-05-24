package ch.uzh.ifi.hase.soprafs24.controller;

import ch.uzh.ifi.hase.soprafs24.entity.User;
import ch.uzh.ifi.hase.soprafs24.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ch.uzh.ifi.hase.soprafs24.controller.LeaderboardController.class)
public class LeaderboardControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    private User user1;
    private User user2;

    @BeforeEach
    public void setup() {

        // user 1 played more games, but has less score than user 2
        user1 = new User();
        user1.setUsername("user1");
        user1.setId(1L);
        user1.setGamesPlayed(2);
        user1.setTotalScore(10);
        user1.setGamesWon(2);

        user2 = new User();
        user2.setUsername("user2");
        user2.setId(2L);
        user2.setGamesPlayed(1);
        user2.setTotalScore(20);
        user2.setGamesWon(1);
    }

    @Test
    public void getGamesPlayedRanking_returnCorrectJsonObject() throws Exception {
        List<User> users = new ArrayList<>();
        users.add(user1);
        users.add(user2);

        given(userService.getGamesPlayedRanking(Mockito.any())).willReturn(users);

        MockHttpServletRequestBuilder getRequest = get("/leaderboards/games-played");

        mockMvc.perform(getRequest)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].username").value("user1"))
                .andExpect(jsonPath("$[0].gamesPlayed").value("2"))
                .andExpect(jsonPath("$[1].username").value("user2"))
                .andExpect(jsonPath("$[1].gamesPlayed").value("1"));
    }

    @Test
    public void getTotalScoreRanking_returnCorrectJsonObject() throws Exception {
        List<User> users = new ArrayList<>();
        users.add(user1);
        users.add(user2);

        given(userService.getTotalScoreRanking(Mockito.any())).willReturn(users);

        MockHttpServletRequestBuilder getRequest = get("/leaderboards/total-score");

        mockMvc.perform(getRequest)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].username").value("user1"))
                .andExpect(jsonPath("$[0].totalScore").value("10"))
                .andExpect(jsonPath("$[1].username").value("user2"))
                .andExpect(jsonPath("$[1].totalScore").value("20"));
    }

    @Test
    public void getGamesWonRanking_returnCorrectJsonObject() throws Exception {
        List<User> users = new ArrayList<>();
        users.add(user1);
        users.add(user2);

        given(userService.getGamesWonRanking(Mockito.any())).willReturn(users);

        MockHttpServletRequestBuilder getRequest = get("/leaderboards/games-won");

        mockMvc.perform(getRequest)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].username").value("user1"))
                .andExpect(jsonPath("$[0].gamesWon").value("2"))
                .andExpect(jsonPath("$[1].username").value("user2"))
                .andExpect(jsonPath("$[1].gamesWon").value("1"));
    }
}
