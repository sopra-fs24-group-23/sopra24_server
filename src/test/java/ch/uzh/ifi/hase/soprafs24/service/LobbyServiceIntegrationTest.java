package ch.uzh.ifi.hase.soprafs24.service;

import ch.uzh.ifi.hase.soprafs24.entity.Lobby;
import ch.uzh.ifi.hase.soprafs24.entity.User;
import ch.uzh.ifi.hase.soprafs24.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.web.WebAppConfiguration;

import static org.junit.jupiter.api.Assertions.assertEquals;

@WebAppConfiguration
@SpringBootTest
public class LobbyServiceIntegrationTest {
    @Qualifier("userRepository")
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private LobbyService lobbyService;

    @BeforeEach
    public void setup() {
        userRepository.deleteAll();
    }

    @Test
    public void createLobby_validInputs_success() {
        User user = new User();
        user.setToken("ABCD");
        user.setId(1L);
        user.setUsername("TestUser");
        user.setPassword("TestPassword");
        user.setTotalScore(10);
        user.setGamesPlayed(1);
        user.setGamesWon(1);

        userRepository.saveAndFlush(user);

        User userToken = new User();
        userToken.setToken("ABCD");

        Lobby newLobby = lobbyService.createLobby(userToken);

        // lobby host is equal userRepo user
        assertEquals(user.getUsername(), newLobby.getHost().getUsername());
        assertEquals(user.getId(), newLobby.getHost().getId());
        assertEquals(user.getToken(), newLobby.getHost().getToken());

        assertEquals(1, lobbyService.getLobbies().size());
    }

}
