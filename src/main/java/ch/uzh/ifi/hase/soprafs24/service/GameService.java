package ch.uzh.ifi.hase.soprafs24.service;

import ch.uzh.ifi.hase.soprafs24.constant.GamePhase;
import ch.uzh.ifi.hase.soprafs24.entity.*;
import ch.uzh.ifi.hase.soprafs24.events.GameStateChangeEvent;
import ch.uzh.ifi.hase.soprafs24.service.LobbyService;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import javax.transaction.Transactional;
import java.util.HashMap;
import java.util.List;

@Service
@Transactional
public class GameService {
    private HashMap<String, Game> games = new HashMap<>();
    private final ApplicationEventPublisher eventPublisher;

    public GameService(ApplicationEventPublisher eventPublisher) {
        this.eventPublisher = eventPublisher;
    }

    public void closeInputs(String gameId) {
        Game game = games.get(gameId);
        game.setPlayerHasAnswered(true);
    }

    /*  1. inform players round started
     *   2. set Phase to "scoreboard"
     *   3. wait for scoreboardDuration
     *   4. set Phase to "input", update clients
     *   5. wait for inputDuration OR all answered
     *          >> need to track who has/has not answered
     *   6. set Phase to "voting", update clients
     *   7. wait for votingDuration OR all voted
     *          >> need to track hos has/has not voted
     *   8. check all answers, except undoubted jokers
     *   9. calculate scores, end round
     *   10. call game.startNextRound()
     * */
    public void runGame(String gameId, GameSettings settings, List<Player> players) {
        Game game = new Game(settings, players);
        this.games.put(gameId, game);

        while (game.initializeRound()) {
            // inform clients that round has started
            updateClients(gameId, game.getState());
            // wait for scoreboardDuration
            game.waitScoreboard().thenRun(() -> {
                // change phase to INPUT
                game.setPhase(GamePhase.INPUT);
                // inform clients to open inputs
                updateClients(gameId, game.getState());
                // wait for inputDuration OR until all players have answered
                game.waitInput().thenRun(() -> {
                    // change phase to VOTING
                    game.setPhase(GamePhase.VOTING);
                    // inform clients that voting has started
                    updateClients(gameId, game.getState());
                    // wait for votingDuration OR until all players have voted
                    game.waitVoting().thenRun(() -> {
                        // make async calls to check answers
                        game.calculateScores().thenRun(() -> {
                            // update clients with new scores
                            updateClients(gameId, game.getState());
                        });
                    });
                });
            });
        }

        // if max number of rounds has been reached
        if(!game.initializeRound()) {
            // change phase to ENDED
            game.setPhase(GamePhase.ENDED);
            // inform clients with final state
            updateClients(gameId, game.getState());
            // delete game
            games.remove(gameId);
        }
    }

    private void updateClients(String gameId, GameState gameState) {
        eventPublisher.publishEvent(
                new GameStateChangeEvent(
                        this,
                        gameState,
                        gameId
                )
        );
    }

}