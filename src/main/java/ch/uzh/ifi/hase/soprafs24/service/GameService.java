package ch.uzh.ifi.hase.soprafs24.service;

import ch.uzh.ifi.hase.soprafs24.constant.GamePhase;
import ch.uzh.ifi.hase.soprafs24.entity.*;
import ch.uzh.ifi.hase.soprafs24.events.GameStateChangeEvent;
import ch.uzh.ifi.hase.soprafs24.service.LobbyService;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import javax.transaction.Transactional;
import java.util.HashMap;
import java.util.Map;
import java.util.List;

@Service
@Transactional
public class GameService {
    private HashMap<String, Game> games = new HashMap<>();
    private final ApplicationEventPublisher eventPublisher;

    public GameService(ApplicationEventPublisher eventPublisher) {
        this.eventPublisher = eventPublisher;
    }

    public void closeInputs(String gameId, Map<String, List<Answer>> answers) { // Adjust parameter type if necessary
        Game game = games.get(gameId);
        game.handleAnswers(answers);
        game.setPlayerHasAnswered(true); // Ensure this line is present to reflect player action
        if (!game.isInputPhaseClosed()) {
            game.setInputPhaseClosed(true);
            game.setPhase(GamePhase.AWAITING_ANSWERS);
            updateClients(gameId, game.getState());
        }
    }

    public GameState getGameState(String gameId) {
        Game game = games.get(gameId);
        return game.getState();
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
                    // change phase to AWAITING_ANSWERS
                    game.setPhase(GamePhase.AWAITING_ANSWERS);
                    // inform clients to send answers
                    updateClients(gameId, game.getState());
                    // wait for all answers to arrive
                    game.waitForAnswers().thenRun(() -> {
                        // change phase to VOTING
                        game.setPhase(GamePhase.VOTING);
                        // inform clients that voting has started
                        updateClients(gameId, game.getState());
                        // wait for voting duration
                        game.waitVoting().thenRun(() -> {
                            // change phase to AWAITING_VOTES
                            game.setPhase(GamePhase.AWAITING_VOTES);
                            // inform clients to send votes
                            updateClients(gameId, game.getState());
                            // wait for all votes to arrive
                            game.waitForVotes().thenRun(() -> {
                                // make async calls to calculate score
                                game.calculateScores().thenRun(() -> {
                                    // change phase to VOTING_RESULTS
                                    game.setPhase(GamePhase.VOTING_RESULTS);
                                    // inform clients to display voting results
                                    updateClients(gameId, game.getState());
                                    // delay round-end to display voting results
                                    game.waitScoreboard();
                                });
                            });
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

    /* Class-Private Helper Methods*/
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