package ch.uzh.ifi.hase.soprafs24.entity;

import ch.uzh.ifi.hase.soprafs24.constant.GamePhase;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Round {
    private Integer inputDuration;
    private Integer votingDuration;
    private Integer scoreboardDuration;
    private String roundLetter;
    private List<Answer> answers;
    private List<Player> players;
    private Game game;
    private GamePhase currentPhase;
    private boolean roundEnded = false;

    public Round(GameSettings settings, List<Player> players, Game game) {
        this.roundLetter = generateRandomLetter();
        this.players = players;
        this.answers = new ArrayList<Answer>();
        this.currentPhase = GamePhase.SCOREBOARD;
        this.inputDuration = settings.getInputDuration();
        this.scoreboardDuration = settings.getScoreboardDuration();
        this.votingDuration = settings.getVotingDuration();
    }

    public void startRound() {
        /*  1. inform players round started
        *   2. set Phase to "scoreboard"
        *   3. wait for scoreboardDuration
        *   4. set Phase to "input", update clients
        *   5. wait for inputDuration OR all answered
        *       >> need to track who has/has not answered
        *   6. set Phase to "voting", update clients
        *   7. wait for votingDuration OR all voted
        *   8. check all answers, except undoubted jokers
        *   9. calculate scores, end round
        *   10. call game.startNextRound()
        * */
    }
    public String generateRandomLetter() {
        Random random = new Random();
        return ("A" + random.nextInt(26));
    }
}
