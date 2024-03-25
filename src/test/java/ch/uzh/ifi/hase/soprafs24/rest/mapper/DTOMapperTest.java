package ch.uzh.ifi.hase.soprafs24.rest.mapper;

import ch.uzh.ifi.hase.soprafs24.constant.PlayerStatus;
import ch.uzh.ifi.hase.soprafs24.entity.Player;
import ch.uzh.ifi.hase.soprafs24.rest.dto.PlayerGetDTO;
import ch.uzh.ifi.hase.soprafs24.rest.dto.PlayerPostDTO;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * DTOMapperTest
 * Tests if the mapping between the internal and the external/API representation
 * works.
 */
public class DTOMapperTest {
  @Test
  public void testCreateUser_fromUserPostDTO_toUser_success() {
    // create UserPostDTO
    PlayerPostDTO playerPostDTO = new PlayerPostDTO();
    playerPostDTO.setName("name");
    playerPostDTO.setUsername("username");

    // MAP -> Create user
    Player player = DTOMapper.INSTANCE.convertPlayerPostDTOtoEntity(playerPostDTO);

    // check content
    assertEquals(playerPostDTO.getUsername(), player.getUsername());
  }

  @Test
  public void testGetUser_fromUser_toUserGetDTO_success() {
    // create User
    Player player = new Player();
    player.setUsername("firstname@lastname");
    player.setStatus(PlayerStatus.OFFLINE);
    player.setToken("1");

    // MAP -> Create UserGetDTO
    PlayerGetDTO playerGetDTO = DTOMapper.INSTANCE.convertEntityToPlayerGetDTO(player);

    // check content
    assertEquals(player.getId(), playerGetDTO.getId());
    assertEquals(player.getUsername(), playerGetDTO.getUsername());
    assertEquals(player.getStatus(), playerGetDTO.getStatus());
  }
}
