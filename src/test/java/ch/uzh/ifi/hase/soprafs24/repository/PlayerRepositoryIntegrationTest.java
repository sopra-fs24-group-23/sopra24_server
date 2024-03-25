package ch.uzh.ifi.hase.soprafs24.repository;

import ch.uzh.ifi.hase.soprafs24.constant.PlayerStatus;
import ch.uzh.ifi.hase.soprafs24.entity.Player;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@DataJpaTest
public class PlayerRepositoryIntegrationTest {

  @Autowired
  private TestEntityManager entityManager;

  @Autowired
  private PlayerRepository playerRepository;

  @Test
  public void findByUsername_success() {
    // given
    Player player = new Player();
    player.setUsername("firstname@lastname");
    player.setStatus(PlayerStatus.OFFLINE);
    player.setToken("1");

    entityManager.persist(player);
    entityManager.flush();

    // when
    Player found = playerRepository.findByUsername(player.getUsername());

    // then
    assertNotNull(found.getId());
    assertEquals(found.getUsername(), player.getUsername());
    assertEquals(found.getToken(), player.getToken());
    assertEquals(found.getStatus(), player.getStatus());
  }
}
