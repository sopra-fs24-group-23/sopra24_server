package ch.uzh.ifi.hase.soprafs24.service;

import ch.uzh.ifi.hase.soprafs24.constant.GamePhase;
import ch.uzh.ifi.hase.soprafs24.entity.*;
import ch.uzh.ifi.hase.soprafs24.events.GameStateChangeEvent;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import javax.transaction.Transactional;
import java.util.HashMap;
import java.util.Map;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@Service
@Transactional
public class GameService {
    private HashMap<String, Game> games = new HashMap<>();
    private final ApplicationEventPublisher eventPublisher;

    public GameService(ApplicationEventPublisher eventPublisher) {
        this.eventPublisher = eventPublisher;
    }

    public GameSettings getSettings(String gameId){
        // Retrieve game from the games map
        Game game = this.games.get(gameId);
        // Return the settings of the game
        return game.getSettings();
    }

    public void closeInputs(String lobbyId, Map<String, List<Answer>> answers) { // Adjust parameter type if necessary
        Game game = games.get(lobbyId);
        game.setPhase(GamePhase.AWAITING_ANSWERS);
        game.setPlayerHasAnswered(true);
        updateClients(lobbyId, game.getState());
        //if (!game.isInputPhaseClosed()) {
          //  game.setInputPhaseClosed(true);
            //updateClients(lobbyId, game.getState());
        //}
    }
    public void setAnswers(String lobbyId, Map<String, List<Answer>> answers) { // Adjust parameter type if necessary
        Game game = games.get(lobbyId);
        game.handleAnswers(answers);
        game.setPlayerHasAnswered(true);
        if (game.getPlayerHasAnswered()){
            game.setInputPhaseClosed(true);
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
        startGameLoop(gameId, game);
    }

    public void startGameLoop(String gameId, Game game) {
        while (game.initializeRound()) {
            handleScoreboardPhase(gameId, game).thenRun(
                () -> handleInputPhase(gameId, game).thenRun(
                    () -> handleAwaitAnswersPhase(gameId, game).thenRun(
                        () -> handleVotingPhase(gameId, game).thenRun(
                            () -> handleAwaitVotesPhase(gameId, game).thenRun(
                                () -> handleVotingResultsPhase(gameId, game)
                            )
                        )
                    )
                )
            ).join();
        }

        if (!game.initializeRound()) {
            handleEndGame(gameId, game);
        }
    }

    /* GamePhase specific helpers */
    private CompletableFuture<Void> handleScoreboardPhase(String gameId, Game game) {
        System.out.println("SCOREBOARD PHASE BEING HANDLED");
        updateClients(gameId, game.getState());
        return game.waitScoreboard();
    }

    private CompletableFuture<Void> handleInputPhase(String gameId, Game game) {
        System.out.println("INPUT PHASE BEING HANDLED");
        setPhaseAndUpdate(GamePhase.INPUT, gameId, game);
        return game.waitInput();
    }

    private CompletableFuture<Void> handleAwaitAnswersPhase(String gameId, Game game) {
        System.out.println("AWAITING_ANSWERS PHASE BEING HANDLED");
        setPhaseAndUpdate(GamePhase.AWAITING_ANSWERS, gameId, game);
        return game.waitForAnswers();
    }

    private CompletableFuture<Void> handleVotingPhase(String gameId, Game game) {
        System.out.println("VOTING PHASE BEING HANDLED");
        setPhaseAndUpdate(GamePhase.VOTING, gameId, game);
        return game.waitVoting();
    }

    private CompletableFuture<Void> handleAwaitVotesPhase(String gameId, Game game) {
        System.out.println("AWAITING_VOTES PHASE BEING HANDLED");
        setPhaseAndUpdate(GamePhase.AWAITING_VOTES, gameId, game);
        return game.waitForVotes();
    }

    private void handleVotingResultsPhase(String gameId, Game game) {
        System.out.println("VOTING_RESULTS PHASE BEING HANDLED");
        setPhaseAndUpdate(GamePhase.VOTING_RESULTS, gameId, game);
        game.waitScoreboard();
    }

    private void handleEndGame(String gameId, Game game) {
        System.out.println("ENDED PHASE BEING HANDLED");
        setPhaseAndUpdate(GamePhase.ENDED, gameId, game);
        games.remove(gameId);
    }



    /* General Helper Methods*/

    private void setPhaseAndUpdate(GamePhase gamePhase, String gameId, Game game) {
        game.setPhase(gamePhase);
        updateClients(gameId, game.getState());
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