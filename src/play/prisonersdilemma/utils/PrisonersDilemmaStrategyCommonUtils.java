package play.prisonersdilemma.utils;

import gametree.GameNode;
import gametree.GameNodeDoesNotExistException;
import play.PlayStrategy;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 *
 * Computational Game Theory - 2019/2020
 * Faculty of Sciences and Technology of
 * New University of Lisbon (FCT NOVA)
 *
 * 1st Tournament - Prisoner's Dilemma,
 * using NOVA GTI (Game Theory Interactive) Platform
 *
 * Strategy's Common Utils
 *
 * Authors:
 * - Pedro Lamarao Pais (Student no. 48247)
 *   - pgp@campus.fct.unl.pt
 * - Ruben Andre Barreiro (Student no. 42648)
 *   - r.barreiro@campus.fct.unl.pt
 *
 */
public abstract class PrisonersDilemmaStrategyCommonUtils {

    // Constants:

    /**
     * The String Label for the Cooperate Move, in Upper Case.
     */
    public static final String COOPERATE = "COOPERATE";

    /**
     * The String Label for the Defect Move, in Upper Case.
     */
    public static final String DEFECT = "DEFECT";

    /**
     * The String Label for the Unknown Move, in Upper Case.
     */
    public static final String UNKNOWN = "UNKNOWN";

    /**
     * The String Label for the Cooperate Move, as Player #1
     */
    public static final String COOPERATE_AS_PLAYER_1 = "1:1:Cooperate";

    /**
     * The String Label for the Defect Move, as Player #1
     */
    public static final String DEFECT_AS_PLAYER_1 = "1:1:Defect";

    /**
     * The String Label for the Cooperate Move, as Player #2
     */
    public static final String COOPERATE_AS_PLAYER_2 = "2:1:Cooperate";

    /**
     * The String Label for the Defect Move, as Player #1
     */
    public static final String DEFECT_AS_PLAYER_2 = "2:1:Defect";

    /**
     * The total number of Game Stages/Rounds played, until the moment.
     */
    public static int TOTAL_NUM_OF_GAME_STAGES_ROUNDS;

    /**
     * The Probability of continue to the next Game Stage/Round.
     */
    public static double PROBABILITY_OF_CONTINUE_TO_THE_NEXT_GAME_STAGE_ROUND;


    // Global Instance Variables:

    /**
     * The number of Game Stages/Rounds currently played.
     */
    public int numOfGameStagesRoundsCurrentlyPlayed;

    /**
     * The Last Move played by My Opponent, in the last Game Stage/Round, as Player #1.
     */
    public String myOpponentMovePlayedInLastGameStageRoundAsPlayer1;

    /**
     * The Last Move played by My Opponent, in the last Game Stage/Round, as Player #2.
     */
    public String myOpponentMovePlayedInLastGameStageRoundAsPlayer2;


    // Constructors:

    /**
     * Constructor #1:
     * - The Constructor for the Prisoner's Dilemma Strategy's Common Utils;
     */
    public PrisonersDilemmaStrategyCommonUtils() {

        setMyOpponentMovePlayedInLastGameStageRoundAsPlayer1("");
        setMyOpponentMovePlayedInLastGameStageRoundAsPlayer2("");

    }


    // Public Methods/Procedures:

    /**
     * Returns the Total Number of the current Game Stages/Rounds.
     *
     * @return the Total Number of the current Game Stages/Rounds
     */
    public static int getTotalNumOfGameStagesRounds() {

        return TOTAL_NUM_OF_GAME_STAGES_ROUNDS;

    }

    /**
     * Sets the Total Number of the current Game Stages/Rounds.
     *
     * @param totalNumOfGameStagesRounds the Total Number of the current Game Stages/Rounds
     */
    public static void setTotalNumOfGameStagesRounds(int totalNumOfGameStagesRounds) {

        TOTAL_NUM_OF_GAME_STAGES_ROUNDS = totalNumOfGameStagesRounds;

    }

    /**
     * Returns the Probability of continue to the next Game Stage/Round.
     *
     * @return the Probability of continue to the next Game Stage/Round
     */
    public static double getProbabilityOfContinueToTheNextGameStageRound() {

        return PROBABILITY_OF_CONTINUE_TO_THE_NEXT_GAME_STAGE_ROUND;

    }

    /**
     * Sets the Probability of continue to the next Game Stage/Round.
     *
     * @param probabilityOfContinueToTheNextGameStageRound the Probability of continue to the next Game Stage/Round
     */
    public static void setProbabilityOfContinueToTheNextGameStageRound
        (
                double probabilityOfContinueToTheNextGameStageRound
        )
    {

        PROBABILITY_OF_CONTINUE_TO_THE_NEXT_GAME_STAGE_ROUND = probabilityOfContinueToTheNextGameStageRound;

    }

    /**
     * Returns the number of Game Stages/Rounds currently played.
     *
     * @return the number of Game Stages/Rounds currently played
     */
    public int getNumOfGameStagesRoundsCurrentlyPlayed() {

        return this.numOfGameStagesRoundsCurrentlyPlayed;

    }

    /**
     * Sets the number of Game Stages/Rounds currently played.
     *
     * @param numOfGameStagesRoundsCurrentlyPlayed number of Game Stages/Rounds currently played
     */
    public void setNumOfGameStagesRoundsCurrentlyPlayed(int numOfGameStagesRoundsCurrentlyPlayed) {

        this.numOfGameStagesRoundsCurrentlyPlayed = numOfGameStagesRoundsCurrentlyPlayed;

    }

    /**
     * Returns My Opponent's Move played in last Game Stage/Round as Player #1, as a String representation.
     *
     * @return My Opponent's Move played in last Game Stage/Round as Player #1, as a String representation
     */
    public String getMyOpponentMovePlayedInLastGameStageRoundAsPlayer1() {

        return this.myOpponentMovePlayedInLastGameStageRoundAsPlayer1;

    }

    /**
     * Sets My Opponent's Move played in last Game Stage/Round as Player #1, as a String representation.
     *
     * @param myOpponentMovePlayedInLastGameStageRoundAsPlayer1 My Opponent's Move played in
     *        last Game Stage/Round as Player #1, as a String representation
     */
    public void setMyOpponentMovePlayedInLastGameStageRoundAsPlayer1
            (
                    String myOpponentMovePlayedInLastGameStageRoundAsPlayer1
            )
    {

        this.myOpponentMovePlayedInLastGameStageRoundAsPlayer1 =
                        myOpponentMovePlayedInLastGameStageRoundAsPlayer1;

    }

    /**
     * Returns My Opponent's Move played in last Game Stage/Round as Player #2, as a String representation.
     *
     * @return My Opponent's Move played in last Game Stage/Round as Player #2, as a String representation
     */
    public String getMyOpponentMovePlayedInLastGameStageRoundAsPlayer2() {

        return this.myOpponentMovePlayedInLastGameStageRoundAsPlayer2;

    }

    /**
     * Sets My Opponent's Move played in last Game Stage/Round as Player #2, as a String representation.
     *
     * @param myOpponentMovePlayedInLastGameStageRoundAsPlayer2 My Opponent's Move played in
     *        last Game Stage/Round as Player #2, as a String representation
     */
    public void setMyOpponentMovePlayedInLastGameStageRoundAsPlayer2
            (
                    String myOpponentMovePlayedInLastGameStageRoundAsPlayer2
            )
    {

        this.myOpponentMovePlayedInLastGameStageRoundAsPlayer2 = myOpponentMovePlayedInLastGameStageRoundAsPlayer2;

    }

    /**
     * Returns the Reverse Path, from a given current Game Node, in the tree.
     *
     * NOTE:
     * - This method/procedure it's useful for analyse My Opponent's Moves;
     *
     * @param currentGameNode current Game Node, in the tree
     *
     * @return the Reverse Path, from a given current Game Node, in the tree
     */
    public static List<GameNode> getReversePath(GameNode currentGameNode) {

        try {

            GameNode ancestorOfCurrentGameNode = currentGameNode.getAncestor();

            List<GameNode> myOpponentLastMovePlayedGameNode = getReversePath(ancestorOfCurrentGameNode);

            myOpponentLastMovePlayedGameNode.add(currentGameNode);

            return myOpponentLastMovePlayedGameNode;

        }
        catch (GameNodeDoesNotExistException gameNodeDoesNotExistException) {

            List<GameNode> myOpponentLastMoveGameNode = new ArrayList<>();

            myOpponentLastMoveGameNode.add(currentGameNode);

            return myOpponentLastMoveGameNode;

        }

    }

    public static Set<String> getMyOpponentMovesInLastGameStageRound
            (
                    List<GameNode> listOfOpponentLastMovesAsPlayer1,
                    List<GameNode> listOfOpponentLastMovesAsPlayer2
            )
            throws GameNodeDoesNotExistException
    {

        Set<String> myOpponentMovesInLastGameStageRound = new HashSet<>();

        // As I played as Player 1, I am gonna check what were the moves
        // of My Opponent as Player 2, in the last Game Stage/Round
        for(GameNode myOpponentMovesAsPlayer1GameNode : listOfOpponentLastMovesAsPlayer1) {

            if ( ( myOpponentMovesAsPlayer1GameNode.isNature() )
                    ||
                    (myOpponentMovesAsPlayer1GameNode.isRoot() ) )
            {

                continue;

            }

            // Check/Verify that My Opponent, really played as Player 2,
            // in the last Game Stage/Round
            if( myOpponentMovesAsPlayer1GameNode.getAncestor().isPlayer2() ) {

                myOpponentMovesInLastGameStageRound.add( myOpponentMovesAsPlayer1GameNode.getLabel() );

            }

        }

        // As I played as Player 2, I am gonna check what were the moves
        // of My Opponent as Player 1, in the last Game Stage/Round
        for(GameNode myOpponentMovesAsPlayer2GameNode : listOfOpponentLastMovesAsPlayer2) {

            if( ( myOpponentMovesAsPlayer2GameNode.isNature() )
                    ||
                    ( myOpponentMovesAsPlayer2GameNode.isRoot() ) )
            {

                continue;

            }

            // Check/Verify that My Opponent, really played as Player 1,
            // in the last Game Stage/Round
            if(myOpponentMovesAsPlayer2GameNode.getAncestor().isPlayer1()) {

                myOpponentMovesInLastGameStageRound.add( myOpponentMovesAsPlayer2GameNode.getLabel() );

            }
        }

        return myOpponentMovesInLastGameStageRound;

    }

    /**
     * Verifies if it's a Terminal Game Node of the current Game Stage/Round.
     *
     * @param myStrategy the Strategy I'm currently using for the Prisoner's Dilemma Game
     *
     * @param gameNodePlayer1 the Game Node for the Player #1
     *
     * @param gameNodePlayer2 the Game Node for the Player #2
     */
    public static void isATerminalGameNode(PlayStrategy myStrategy,
                                           GameNode gameNodePlayer1, GameNode gameNodePlayer2) {

        System.out.println();

        System.out.println("The current play isn't completed yet...\n\n");

        System.out.println("Checking if was reached a Terminal Game Node in " +
                           "the last Game Stage/Round for any of the Players...\n\n");

        if(myStrategy.getFinalP1Node() != -1) {

            if (gameNodePlayer1 != null) {

                System.out.println("Terminal Game Node in last Game Stage/Round as Player #1: " + gameNodePlayer1);

            }

        }

        if(myStrategy.getFinalP2Node() != -1) {

            if (gameNodePlayer2 != null) {

                System.out.println("Terminal Game Node in last Stage/Round as Player #2: " + gameNodePlayer2);

            }

        }

        System.out.println("\n\n");

    }

}
