package play.prisonersdilemma.grimtrigger.utils;

import play.PlayStrategy;
import play.prisonersdilemma.utils.PrisonersDilemmaStrategyCommonUtils;

/**
 *
 * Computational Game Theory - 2019/2020
 * Faculty of Sciences and Technology of
 * New University of Lisbon (FCT NOVA | FCT/UNL)
 *
 * 1st Tournament - Prisoner's Dilemma,
 * using NOVA GTI (Game Theory Interactive) Platform
 *
 * Grim Trigger Strategy's Common Utils, extending the Strategy's Common Utils
 *
 * Authors:
 * - Pedro Lamarao Pais (Student no. 48247)
 *   - pgp@campus.fct.unl.pt
 * - Ruben Andre Barreiro (Student no. 42648)
 *   - r.barreiro@campus.fct.unl.pt
 *
 */

public class GrimTriggerStrategyCommonUtils extends PrisonersDilemmaStrategyCommonUtils {


    // Global Instance Variables:

    /**
     * The Play Strategy object, representing the Grim Trigger Strategy.
     */
    private final PlayStrategy myGrimTriggerStrategy;

    /**
     * The boolean value to keep the information about
     * if I am currently defecting, as Player £2, as punishment for
     * My Opponent, played Defect Move, in some moment, as Player #1.
     *
     * NOTES:
     * - This flag should be changed to true, after the Randomness Threshold Factor
     *   applied to the total number of times that My Opponent's Moves changed,
     *   as Player #1, every time he played;
     */
    private boolean defectingAsPunishmentToMyOpponentAsPlayer1;

    /**
     * The boolean value to keep the information about
     * if I am currently defecting as punishment for
     * the possible randomness behaviour from My Opponent, as Player #2.
     *
     * NOTES:
     * - This flag should be changed to true, after the Randomness Threshold Factor
     *   applied to the total number of times that My Opponent's Moves changed,
     *   as Player #2, every time he played;
     */
    private boolean defectingAsPunishmentToMyOpponentAsPlayer2;


    // Constructors:

    /**
     * Constructor #1:
     * - Constructor of the Grim Trigger Strategy's Common Utils, extending the Strategy's Common Utils,
     *   given the Play Strategy object, representing the Grim Trigger Strategy.
     *
     * @param myGrimTriggerStrategy the Play Strategy object, representing the Grim Trigger Strategy
     */
    public GrimTriggerStrategyCommonUtils(PlayStrategy myGrimTriggerStrategy) {

        this.myGrimTriggerStrategy = myGrimTriggerStrategy;

    }


    // Public Methods/Procedures:

    /**
     * Performs the Setup of the Parameters' Configuration for the Grim Trigger Strategy.
     */
    public void setupGrimTriggerStrategyParametersConfiguration() {

        setMyOpponentMovePlayedInLastGameStageRoundAsPlayer1("");
        setMyOpponentMovePlayedInLastGameStageRoundAsPlayer2("");

        setTotalNumOfGameStagesRounds( this.getMyGrimTriggerStrategy().getMaximumNumberOfIterations() + 1 );

        setProbabilityOfContinueToTheNextGameStageRound
                    (
                            this.getMyGrimTriggerStrategy().probabilityForNextIteration()
                    );

        setNumOfGameStagesRoundsCurrentlyPlayed(0);

        this.defectingAsPunishmentToMyOpponentAsPlayer1 = false;
        this.defectingAsPunishmentToMyOpponentAsPlayer2 = false;

    }

    /**
     * Prints the Setup of the Parameters' Configuration for the Grim Trigger Strategy.
     */
    public void printGrimTriggerStrategyParametersConfiguration() {

        System.out.println("--- INFORMATION ABOUT THE PARAMETERS' CONFIGURATION FOR " +
                           "THE GRIM TRIGGER STRATEGY ---");

        System.out.println("\n");

        System.out.println("    Total Number of Game Stages/Rounds:");
        System.out.println("    - " + TOTAL_NUM_OF_GAME_STAGES_ROUNDS);

        System.out.println("\n\n");

    }

    /**
     * Returns the Play Strategy object, representing the Grim Trigger Strategy.
     *
     * @return the Play Strategy object, representing the Grim Trigger Strategy
     */
    public PlayStrategy getMyGrimTriggerStrategy() {

        return this.myGrimTriggerStrategy;

    }

    /**
     * Returns the boolean value that keeps the information about I'm currently playing a Defect Move, as Player #2,
     * as punishment, in response to, a Defect Move from My Opponent playing as Player #1, in some moment.
     *
     * @return the boolean value that keeps the information about I'm currently playing a Defect Move, as Player #2,
     *         as punishment, in response to, a Defect Move from My Opponent playing as Player #1, in some moment
     */
    public boolean isDefectingAsPunishmentToMyOpponentAsPlayer1() {

        return this.defectingAsPunishmentToMyOpponentAsPlayer1;

    }

    /**
     * Sets the boolean value that keeps the information about I'm currently playing a Defect Move, as Player #2,
     * as punishment, in response to, a Defect Move from My Opponent playing as Player #1, in some moment.
     *
     * @param defectingAsPunishmentToMyOpponentAsPlayer1 the boolean value that keeps the information about
     *        I'm currently playing a Defect Move, as Player #2, as punishment, in response to,
     *        a Defect Move from My Opponent playing as Player #1, in some moment
     */
    public void setDefectingAsPunishmentToMyOpponentAsPlayer1(boolean defectingAsPunishmentToMyOpponentAsPlayer1) {

        this.defectingAsPunishmentToMyOpponentAsPlayer1 = defectingAsPunishmentToMyOpponentAsPlayer1;

    }

    /**
     * Returns the boolean value that keeps the information about I'm currently playing a Defect Move, as Player #1,
     * as punishment, in response to, a Defect Move from My Opponent playing as Player #2, in some moment.
     *
     * @return the boolean value that keeps the information about I'm currently playing a Defect Move, as Player #1,
     *         as punishment, in response to, a Defect Move from My Opponent playing as Player #2, in some moment
     */
    public boolean isDefectingAsPunishmentToMyOpponentAsPlayer2() {

        return this.defectingAsPunishmentToMyOpponentAsPlayer2;

    }

    /**
     * Sets the boolean value that keeps the information about I'm currently playing a Defect Move, as Player #1,
     * as punishment, in response to, a Defect Move from My Opponent playing as Player #2, in some moment.
     *
     * @param defectingAsPunishmentToMyOpponentAsPlayer2 the boolean value that keeps the information about
     *        I'm currently playing a Defect Move, as Player #1, as punishment, in response to,
     *        a Defect Move from My Opponent playing as Player #2, in some moment
     */
    public void setDefectingAsPunishmentToMyOpponentAsPlayer2(boolean defectingAsPunishmentToMyOpponentAsPlayer2) {

        this.defectingAsPunishmentToMyOpponentAsPlayer2 = defectingAsPunishmentToMyOpponentAsPlayer2;

    }

    /**
     * Checks/Verifies if My Opponent, played a Defect Move, in the last Game Stage/Round, as Player #1.
     */
    public void checkIfMyOpponentDefectedInLastRoundAsPayer1() {

        if( ( !this.isDefectingAsPunishmentToMyOpponentAsPlayer1() )
                                    &&
            ( this.myOpponentMovePlayedInLastGameStageRoundAsPlayer1.equalsIgnoreCase(DEFECT) ) )
        {
            System.out.println("My Opponent played a " + DEFECT +" Move, in the last Game Stage/Round, as Player #1, " +
                               "I will start to punish him, from now!!!\n\n");

            this.setDefectingAsPunishmentToMyOpponentAsPlayer1(true);

        }

    }

    /**
     * Checks/Verifies if My Opponent, played a Defect Move, in the last Game Stage/Round, as Player #2.
     */
    public void checkIfMyOpponentDefectedInLastRoundAsPayer2() {

        if( ( !this.isDefectingAsPunishmentToMyOpponentAsPlayer2() )
                                    &&
            ( this.myOpponentMovePlayedInLastGameStageRoundAsPlayer2.equalsIgnoreCase(DEFECT) ) )
        {
            System.out.println("My Opponent played a " + DEFECT +" Move, in the last Game Stage/Round, as Player #2, " +
                               "I will start to punish him, from now!!!\n\n");

            this.setDefectingAsPunishmentToMyOpponentAsPlayer2(true);

        }

    }

}
