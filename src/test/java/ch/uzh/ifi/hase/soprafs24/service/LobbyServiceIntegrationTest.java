package ch.uzh.ifi.hase.soprafs24.service;

import ch.uzh.ifi.hase.soprafs24.constant.UserStatus;
import ch.uzh.ifi.hase.soprafs24.entity.GameSettings;
import ch.uzh.ifi.hase.soprafs24.entity.Lobby;
import ch.uzh.ifi.hase.soprafs24.entity.Player;
import ch.uzh.ifi.hase.soprafs24.entity.User;
import ch.uzh.ifi.hase.soprafs24.events.LobbyClosedEvent;
import ch.uzh.ifi.hase.soprafs24.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@WebAppConfiguration
@SpringBootTest
public class LobbyServiceIntegrationTest {
    @Qualifier("userRepository")
    @Autowired
    private UserRepository userRepository;

    private LobbyService lobbyService;

    @Mock
    private ApplicationEventPublisher eventPublisher;

    private User host;
    private User player;

    @BeforeEach
    public void setup() {
        userRepository.deleteAll();
        lobbyService = new LobbyService(userRepository, eventPublisher);

        host = new User();
        host.setToken("hostToken");
        host.setId(1L);
        host.setStatus(UserStatus.ONLINE);
        host.setUsername("host");
        host.setPassword("hostPassword");
        host.setTotalScore(10);
        host.setGamesPlayed(1);
        host.setGamesWon(1);

        player = new User();
        player.setToken("playerToken");
        player.setId(2L);
        player.setStatus(UserStatus.ONLINE);
        player.setUsername("player");
        player.setPassword("playerPassword");
        player.setTotalScore(10);
        player.setGamesPlayed(1);
        player.setGamesWon(1);

        // store users to DB
        userRepository.save(host);
        userRepository.save(player);
        userRepository.flush();
    }

    @Test
    public void createLobby_validInputs_success() {
        Lobby newLobby = lobbyService.createLobby(host);

        // lobby host is equal userRepo user
        assertEquals(host.getUsername(), newLobby.getHost().getUsername());
        assertEquals(host.getId(), newLobby.getHost().getId());
        assertEquals(host.getToken(), newLobby.getHost().getToken());

        assertEquals(1, lobbyService.getLobbies().size());
    }

    @Test
    public void removePlayer_playerIsHost_lobbyClosed() {
        Lobby newLobby = lobbyService.createLobby(host);

        lobbyService.addPlayer(newLobby.getId(), player);

        lobbyService.removePlayer(newLobby.getId(), host);

        // verify that LobbyClosedEvent was published
        Mockito.verify(eventPublisher).publishEvent(Mockito.any(LobbyClosedEvent.class));
    }

    @Test
    public void kickPlayer_validInput_playerRemovedFromLobby() {
        Lobby newLobby = lobbyService.createLobby(host);

        lobbyService.addPlayer(newLobby.getId(), player);

        lobbyService.kickPlayer(newLobby.getId(), host, player.getUsername());

        List<Player> players = newLobby.getPlayers();

        for (Player p : players) {
            assertNotEquals(p.getToken(), player.getToken());
        }
    }

    @Test
    public void addPlayer_lobbyIsRunning_throwsException() {

        User latePlayer = new User();
        latePlayer.setToken("latePlayerToken");
        latePlayer.setId(1L);
        latePlayer.setStatus(UserStatus.ONLINE);
        latePlayer.setUsername("latePlayer");
        latePlayer.setPassword("latePlayerPassword");
        latePlayer.setTotalScore(10);
        latePlayer.setGamesPlayed(1);
        latePlayer.setGamesWon(1);

        userRepository.saveAndFlush(latePlayer);

        Lobby newLobby = lobbyService.createLobby(host);

        lobbyService.addPlayer(newLobby.getId(), player);

        // this is usually handled by GameWebsocketController
        newLobby.setGameRunning(true);

        assertThrows(ResponseStatusException.class, () -> lobbyService.addPlayer(newLobby.getId(), latePlayer));
    }

    @Test
    public void addPlayer_lobbyFull_throwsException() {
        User latePlayer = new User();
        latePlayer.setToken("latePlayerToken");
        latePlayer.setId(1L);
        latePlayer.setStatus(UserStatus.ONLINE);
        latePlayer.setUsername("latePlayer");
        latePlayer.setPassword("latePlayerPassword");
        latePlayer.setTotalScore(10);
        latePlayer.setGamesPlayed(1);
        latePlayer.setGamesWon(1);

        userRepository.saveAndFlush(latePlayer);

        Lobby newLobby = lobbyService.createLobby(host);

        // this is usually handled by GameSettingsService
        GameSettings settings = new GameSettings();
        settings.setMaxPlayers(2);
        newLobby.setSettings(settings);

        lobbyService.addPlayer(newLobby.getId(), player);

        // this throws a nullpointer, not a responsestatus for some reason
        assertThrows(ResponseStatusException.class, () -> lobbyService.addPlayer(newLobby.getId(), latePlayer));
    }

    @Test
    public void createLobby_validInputs_hostAutomaticallyAddedToPlayers() {
        Lobby newLobby = lobbyService.createLobby(host);

        List<Player> players = newLobby.getPlayers();

        boolean isHostInPlayers = false;

        for (Player p : players) {
            if (p.getUsername().equals(host.getUsername())) {
                isHostInPlayers = true;
                break;
            }
        }

        assertTrue(isHostInPlayers);
    }

    @Test
    public void createLobby_validInputs_shortLobbyId() {
        Lobby newLobby = lobbyService.createLobby(host);

        assertTrue(newLobby.getId().length() < 16);
    }

}
