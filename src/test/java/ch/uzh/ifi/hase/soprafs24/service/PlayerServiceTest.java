package ch.uzh.ifi.hase.soprafs24.service;

import ch.uzh.ifi.hase.soprafs24.constant.PlayerStatus;
import ch.uzh.ifi.hase.soprafs24.entity.Player;
import ch.uzh.ifi.hase.soprafs24.repository.PlayerRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.web.server.ResponseStatusException;

import static org.junit.jupiter.api.Assertions.*;

public class PlayerServiceTest {

  @Mock
  private PlayerRepository playerRepository;

  @InjectMocks
  private PlayerService playerController;

  private Player testPlayer;

  @BeforeEach
  public void setup() {
    MockitoAnnotations.openMocks(this);

    // given
    testPlayer = new Player();
    testPlayer.setId(1L);
    testPlayer.setUsername("testUsername");

    // when -> any object is being save in the userRepository -> return the dummy
    // testUser
    Mockito.when(playerRepository.save(Mockito.any())).thenReturn(testPlayer);
  }

  @Test
  public void createUser_validInputs_success() {
    // when -> any object is being save in the userRepository -> return the dummy
    // testUser
    Player createdPlayer = playerController.createUser(testPlayer);

    // then
    Mockito.verify(playerRepository, Mockito.times(1)).save(Mockito.any());

    assertEquals(testPlayer.getId(), createdPlayer.getId());
    assertEquals(testPlayer.getUsername(), createdPlayer.getUsername());
    assertNotNull(createdPlayer.getToken());
    assertEquals(PlayerStatus.OFFLINE, createdPlayer.getStatus());
  }

  @Test
  public void createUser_duplicateName_throwsException() {
    // given -> a first user has already been created
    playerController.createUser(testPlayer);

    // when -> setup additional mocks for UserRepository
    Mockito.when(playerRepository.findByUsername(Mockito.any())).thenReturn(testPlayer);

    // then -> attempt to create second user with same user -> check that an error
    // is thrown
    assertThrows(ResponseStatusException.class, () -> playerController.createUser(testPlayer));
  }

  @Test
  public void createUser_duplicateInputs_throwsException() {
    // given -> a first user has already been created
    playerController.createUser(testPlayer);

    // when -> setup additional mocks for UserRepository
    Mockito.when(playerRepository.findByUsername(Mockito.any())).thenReturn(testPlayer);

    // then -> attempt to create second user with same user -> check that an error
    // is thrown
    assertThrows(ResponseStatusException.class, () -> playerController.createUser(testPlayer));
  }

}
