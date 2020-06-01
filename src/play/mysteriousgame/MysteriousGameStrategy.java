package play.mysteriousgame;

import gametree.GameNode;
import gametree.GameNodeDoesNotExistException;
import play.PlayStrategy;
import play.Strategy;
import play.exception.InvalidStrategyException;
import play.mysteriousgame.utils.IteratedDominanceByLinearProgramming;
import play.mysteriousgame.utils.NormalFormGame;

import java.util.Iterator;

/**
 *
 * Computational Game Theory - 2019/2020
 * Faculty of Sciences and Technology of
 * New University of Lisbon (FCT NOVA | FCT/UNL)
 *
 * 2nd Tournament - Mysterious Game,
 * using NOVA GTI (Game Theory Interactive) Platform
 *
 * Mysterious Game Strategy
 *
 * Authors:
 * - Pedro Lamarao Pais (Student no. 48247)
 *   - pg.pais@campus.fct.unl.pt
 * - Ruben Andre Barreiro (Student no. 42648)
 *   - r.barreiro@campus.fct.unl.pt
 *
 */
public class MysteriousGameStrategy extends Strategy {

    @Override
    public void execute() throws InterruptedException {

        System.out.println("\n\n");

        // Waits until the Game Tree become known and available
        while(!this.isTreeKnown()) {

            System.err.println("Waiting for game tree to become available.");

            //noinspection BusyWait
            Thread.sleep(1000);

        }

        // Infinite Loop
        while(true) {

            PlayStrategy myMysteriousGameStrategy = this.getStrategyRequest();

            // The Strategy chosen by me become NULL,
            // what means (probably) that the Game
            // was terminated by an outside event
            if (myMysteriousGameStrategy == null) {

                // Breaks the Infinite Loop
                break;

            }

            // My Play wasn't completed yet
            boolean myPlayComplete = false;

            // While My Play isn't complete yet
            while (!myPlayComplete) {

                if (myMysteriousGameStrategy.getFinalP1Node() != -1) {

                    GameNode gameNodeFinalForPlayerNum1 =
                            this.tree.getNodeByIndex(myMysteriousGameStrategy.getFinalP1Node());

                    GameNode gameNodeFinalAncestorForPlayerNum1 = null;

                    if (gameNodeFinalForPlayerNum1 != null) {

                        try {

                            gameNodeFinalAncestorForPlayerNum1 = gameNodeFinalForPlayerNum1.getAncestor();

                        }
                        catch (GameNodeDoesNotExistException gameNodeDoesNotExistException) {

                            gameNodeDoesNotExistException.printStackTrace();

                        }


                        assert gameNodeFinalAncestorForPlayerNum1 != null;

                        System.out.print("Last Round as Player #1: " +
                                         showActionLabel(gameNodeFinalAncestorForPlayerNum1.getLabel()) + "|" +
                                         showActionLabel(gameNodeFinalForPlayerNum1.getLabel()));

                        System.out.println(" -> (Me) " + gameNodeFinalForPlayerNum1.getPayoffP1() +
                                           " : (My Opponent) " + gameNodeFinalForPlayerNum1.getPayoffP2());

                    }

                }

                if (myMysteriousGameStrategy.getFinalP2Node() != -1) {

                    GameNode gameNodeFinalForPlayerNum2 =
                            this.tree.getNodeByIndex(myMysteriousGameStrategy.getFinalP2Node());

                    GameNode gameNodeFinalAncestorForPlayerNum2 = null;

                    if (gameNodeFinalForPlayerNum2 != null) {

                        try {

                            gameNodeFinalAncestorForPlayerNum2 = gameNodeFinalForPlayerNum2.getAncestor();

                        }
                        catch (GameNodeDoesNotExistException gameNodeDoesNotExistException) {

                            gameNodeDoesNotExistException.printStackTrace();

                        }

                        assert gameNodeFinalAncestorForPlayerNum2 != null;

                        System.out.print("Last round as Player #2: " +
                                         showActionLabel(gameNodeFinalAncestorForPlayerNum2.getLabel()) + "|" +
                                         showActionLabel(gameNodeFinalForPlayerNum2.getLabel()));

                        System.out.println(" -> (My Opponent) " + gameNodeFinalForPlayerNum2.getPayoffP1() +
                                           " : (Me) " + gameNodeFinalForPlayerNum2.getPayoffP2());

                    }

                }


                // Normal Form Games only!
                GameNode gameNodeRoot = tree.getRootNode();

                int numActionsForPlayerNum1 = gameNodeRoot.numberOfChildren();
                int numActionsForPlayerNum2 = gameNodeRoot.getChildren().next().numberOfChildren();

                String[] actionLabelsForPlayerNum1 = new String[numActionsForPlayerNum1];
                String[] actionLabelsForPlayerNum2 = new String[numActionsForPlayerNum2];

                double[][] matrixUtilitiesForPlayer1 =
                        new double[numActionsForPlayerNum1][numActionsForPlayerNum2];

                double[][] matrixUtilitiesForPlayer2 =
                        new double[numActionsForPlayerNum1][numActionsForPlayerNum2];

                Iterator<GameNode> childrenNodes1 = gameNodeRoot.getChildren();

                GameNode childrenGameNodeForPlayerNum1;
                GameNode childrenGameNodeForPlayerNum2;

                int currentRowMatrixUtilities = 0;
                int currentColumnMatrixUtilities;

                while(childrenNodes1.hasNext()) {

                    childrenGameNodeForPlayerNum1 = childrenNodes1.next();
                    actionLabelsForPlayerNum1[currentRowMatrixUtilities] =
                            childrenGameNodeForPlayerNum1.getLabel();


                    currentColumnMatrixUtilities = 0;


                    Iterator<GameNode> childrenNodes2 = childrenGameNodeForPlayerNum1.getChildren();

                    while(childrenNodes2.hasNext()) {

                        childrenGameNodeForPlayerNum2 = childrenNodes2.next();


                        if (currentRowMatrixUtilities == 0) {

                            actionLabelsForPlayerNum2[currentColumnMatrixUtilities] =
                                    childrenGameNodeForPlayerNum2.getLabel();

                        }


                        matrixUtilitiesForPlayer1[currentRowMatrixUtilities][currentColumnMatrixUtilities] =
                                childrenGameNodeForPlayerNum2.getPayoffP1();

                        matrixUtilitiesForPlayer2[currentRowMatrixUtilities][currentColumnMatrixUtilities] =
                                childrenGameNodeForPlayerNum2.getPayoffP2();


                        currentColumnMatrixUtilities++;

                    }

                    currentRowMatrixUtilities++;

                }

                showActionLabels(1,actionLabelsForPlayerNum1);
                showActionLabels(2,actionLabelsForPlayerNum2);

                showUtilitiesForPlayer(1,matrixUtilitiesForPlayer1);
                showUtilitiesForPlayer(2,matrixUtilitiesForPlayer2);

                NormalFormGame normalFormGame = new NormalFormGame(matrixUtilitiesForPlayer1,
                                                                   matrixUtilitiesForPlayer2,
                                                                   actionLabelsForPlayerNum1,
                                                                   actionLabelsForPlayerNum2,
                                                                  true);


                normalFormGame.showMatrixFormGame();


                // Solve domination
                IteratedDominanceByLinearProgramming.solveIteratedDominance(normalFormGame);

                normalFormGame.showMatrixFormGame();

                if ( normalFormGame.isZeroSumGame() ) {

                    normalFormGame.doZeroSumNashEquilibrium();

                }
                else {

                    normalFormGame.doGeneralSumNashEquilibrium();

                }


                // TODO






                double[] strategyForPlayerNum1 =
                        setStrategyProbabilitiesForPlayer(1, actionLabelsForPlayerNum1,
                                                          myMysteriousGameStrategy);
                double[] strategyForPlayerNum2 =
                        setStrategyProbabilitiesForPlayer(2, actionLabelsForPlayerNum2,
                                                          myMysteriousGameStrategy);

                showStrategyForPlayer(1, strategyForPlayerNum1, actionLabelsForPlayerNum1);
                showStrategyForPlayer(2, strategyForPlayerNum2, actionLabelsForPlayerNum2);

                try {

                    this.provideStrategy(myMysteriousGameStrategy);
                    myPlayComplete = true;

                }
                catch (InvalidStrategyException invalidStrategyException) {

                    System.err.println("Invalid strategy: " + invalidStrategyException.getMessage());
                    invalidStrategyException.printStackTrace(System.err);

                }

            }

        }

    }

    public static String showActionLabel(String actionLabel) {

        return actionLabel.substring(actionLabel.lastIndexOf(':') + 1);

    }

    public void showActionLabels(int numPlayer, String[] actionLabels) {

        System.out.println("Actions for Player #" + numPlayer + ":");

        for (String actionLabel : actionLabels) {

            System.out.println("   - " + showActionLabel(actionLabel));

        }

    }

    public void showUtilitiesForPlayer(int numPlayer, double[][] matrixUtilities) {

        int numColumns = matrixUtilities[0].length;

        System.out.println("Utilities for Player #" + numPlayer + ":");

        for (double[] matrixUtilityRow : matrixUtilities) {

            for (int currentColumn = 0; currentColumn < numColumns; currentColumn++) {

                System.out.print("| " + matrixUtilityRow[currentColumn] + " ");

            }

            System.out.println("|");

        }

    }

    public double[] setStrategyProbabilitiesForPlayer(int numPlayer, String[] actionLabels, PlayStrategy myStrategy) {

        int numActionLabels = actionLabels.length;

        double[] strategyProbabilities = new double[numActionLabels];

        for (int currentActionLabel = 0;
             currentActionLabel < numActionLabels;
             currentActionLabel++)  {

            strategyProbabilities[currentActionLabel] = 0;

        }

        if ( numPlayer == 1 ) {

            // If playing as Player #1 then choose first action
            strategyProbabilities[0] = 1;
            strategyProbabilities[1] = 0.0;

        }
        else {

            // If playing as Player #2 then choose first or second action randomly
            strategyProbabilities[0] = 0.5;
            strategyProbabilities[1] = 0.5;

        }

        for (int currentActionLabel = 0; currentActionLabel < numActionLabels; currentActionLabel++) {

            myStrategy.put(actionLabels[currentActionLabel], strategyProbabilities[currentActionLabel]);

        }

        return strategyProbabilities;

    }

    public void showStrategyForPlayer(int numPlayer, double[] strategyProbabilities, String[] actionLabels) {

        System.out.println("Strategy for Player #" + numPlayer + ":");

        for (int currentActionLabel = 0; currentActionLabel < actionLabels.length; currentActionLabel++) {

            System.out.println("   " + strategyProbabilities[ currentActionLabel ] + ":"
                                     + showActionLabel( actionLabels[ currentActionLabel ] ) );

        }

    }

}
