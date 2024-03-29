package ch.uzh.ifi.hase.soprafs24.rest.mapper;

import ch.uzh.ifi.hase.soprafs24.entity.Player;
import ch.uzh.ifi.hase.soprafs24.entity.User;
import ch.uzh.ifi.hase.soprafs24.rest.dto.*;
import org.mapstruct.*;
import org.mapstruct.factory.Mappers;

/**
 * DTOMapper
 * This class is responsible for generating classes that will automatically
 * transform/map the internal representation
 * of an entity (e.g., the User) to the external/API representation (e.g.,
 * UserGetDTO for getting, UserPostDTO for creating)
 * and vice versa.
 * Additional mappers can be defined for new entities.
 * Always created one mapper for getting information (GET) and one mapper for
 * creating information (POST).
 */
@Mapper
public interface DTOMapper {

    DTOMapper INSTANCE = Mappers.getMapper(DTOMapper.class);

    @Mapping(source = "username", target = "username")
    @Mapping(source = "password", target = "password")
    User convertUserPostDTOtoEntity(UserPostDTO userPostDTO);

    @Mapping(source = "id", target = "id")
    @Mapping(source = "username", target = "username")
    @Mapping(source = "totalScore", target = "totalScore")
    @Mapping(source = "gamesPlayed", target = "gamesPlayed")
    @Mapping(source = "gamesWon", target = "gamesWon")
    UserGetDTO convertEntityToUserGetDTO(User user);

    @Mapping(source = "token", target = "token")
    @Mapping(source = "id", target = "id")
    User convertUserAuthenticationDTOtoEntity(UserAuthenticationDTO userAuthenticationDTO);

    @Mapping(source = "token", target = "token")
    @Mapping(source = "id", target = "id")
    UserAuthenticationDTO convertEntityToUserAuthenticationDTO(User user);

    @Mapping(source = "username", target = "username")
    User convertUserPutDTOtoEntity(UserPutDTO userPutDTO);

    @Mapping(source = "token", target = "token")
    User convertUserTokenDTOtoEntity(UserTokenDTO userTokenDTO);

    @Mapping(source = "id", target = "id")
    @Mapping(source = "username", target = "username")
    @Mapping(source = "currentScore", target = "currentScore")
    @Mapping(source = "currentAnswers", target = "currentAnswers")
    PlayerGetDTO convertEntityToPlayerGetDTO(Player player);
}
