package ch.uzh.ifi.hase.soprafs24.service;

import ch.uzh.ifi.hase.soprafs24.constant.PlayerStatus;
import ch.uzh.ifi.hase.soprafs24.entity.Player;
import ch.uzh.ifi.hase.soprafs24.repository.PlayerRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.web.server.ResponseStatusException;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for the UserResource REST resource.
 *
 * @see PlayerService
 */
@WebAppConfiguration
@SpringBootTest
public class PlayerServiceIntegrationTest {

  @Qualifier("playerRepository")
  @Autowired
  private PlayerRepository playerRepository;

  @Autowired
  private PlayerService playerController;

  @BeforeEach
  public void setup() {
    playerRepository.deleteAll();
  }

  @Test
  public void createUser_validInputs_success() {
    // given
    assertNull(playerRepository.findByUsername("testUsername"));

    Player testPlayer = new Player();
    testPlayer.setUsername("testUsername");

    // when
    Player createdPlayer = playerController.createUser(testPlayer);

    // then
    assertEquals(testPlayer.getId(), createdPlayer.getId());
    assertEquals(testPlayer.getUsername(), createdPlayer.getUsername());
    assertNotNull(createdPlayer.getToken());
    assertEquals(PlayerStatus.OFFLINE, createdPlayer.getStatus());
  }

  @Test
  public void createUser_duplicateUsername_throwsException() {
    assertNull(playerRepository.findByUsername("testUsername"));

    Player testPlayer = new Player();
    testPlayer.setUsername("testUsername");
    Player createdPlayer = playerController.createUser(testPlayer);

    // attempt to create second user with same username
    Player testPlayer2 = new Player();

    // change the name but forget about the username
    testPlayer2.setUsername("testUsername");

    // check that an error is thrown
    assertThrows(ResponseStatusException.class, () -> playerController.createUser(testPlayer2));
  }
}
