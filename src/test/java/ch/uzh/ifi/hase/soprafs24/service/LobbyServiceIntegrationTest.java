package ch.uzh.ifi.hase.soprafs24.service;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.*;
import static org.junit.jupiter.api.Assertions.*;

import ch.uzh.ifi.hase.soprafs24.entity.Lobby;
import ch.uzh.ifi.hase.soprafs24.entity.Player;
import ch.uzh.ifi.hase.soprafs24.entity.User;
import ch.uzh.ifi.hase.soprafs24.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@ExtendWith(SpringExtension.class)
public class LobbyServiceIntegrationTest {

    @Autowired
    private LobbyService lobbyService;


    @Autowired
    private UserRepository userRepository;

    @Autowired
    private MockMvc mockMvc;

    private User user;
    private Lobby lobby;

    @BeforeEach
    public void setUp() {
        // Clean up the database or setup initial state
        userRepository.deleteAll();

        // Setup user and lobby
        user = new User();
        user.setPassword("fefre");
        user.setId(1L);
        user.setUsername("user1");
        user = userRepository.save(user);
        Player player = new Player(user.getId(), user.getUsername(), user.getToken());
        player.setIsHost(true);
        lobby = lobbyService.createLobby(user);
    }

    @Test
    public void testGetLobbyById() {

        Lobby foundLobby = lobbyService.getLobbyById(lobby.getId());
        assertNotNull(foundLobby);
        assertEquals(foundLobby.getId(), lobby.getId());
    }


}
