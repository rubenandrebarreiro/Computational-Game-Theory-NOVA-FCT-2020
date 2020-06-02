package play.mysteriousgame;

import gametree.GameNode;
import gametree.GameNodeDoesNotExistException;
import play.PlayStrategy;
import play.Strategy;
import play.exception.InvalidStrategyException;
import play.mysteriousgame.utils.IteratedDominanceByLinearProgramming;
import play.mysteriousgame.utils.NormalFormGame;

import java.util.*;

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

    private static final int NUM_MAX_ITERATIONS = 200;

    private static final int MIN_SIZE_SAMPLE = (int) ( 0.1 * NUM_MAX_ITERATIONS );


    private int numCurrentRoundsPlayed;

    private String actionForTheMaximumValueForBeliefsFromCurrentRoundForPlayer1;

    private String actionForTheMaximumValueForBeliefsFromCurrentRoundForPlayer2;


    private List<GameNode> getListGameNodesReversedPath (GameNode currentGameNode) {

        try {

            GameNode ancestorGameNode = currentGameNode.getAncestor();

            List<GameNode> listGameNodesReversedPath = this.getListGameNodesReversedPath(ancestorGameNode);

            listGameNodesReversedPath.add(currentGameNode);

            return listGameNodesReversedPath;

        }
        catch (GameNodeDoesNotExistException gameNodeDoesNotExistException) {

            List<GameNode> listGameNodes = new ArrayList<>();

            listGameNodes.add(currentGameNode);

            return listGameNodes;

        }

    }

    private String[] checkMyOpponentMovesAndComputeBestResponses
                    (List<GameNode> listGameNodesReversedPathForPlayerNum1,
                     List<GameNode> listGameNodesReversedPathForPlayerNum2,
                     NormalFormGame normalFormGame,
                     int numCurrentRound)

    throws GameNodeDoesNotExistException {


        Set<String> myOpponentMoves = new HashSet<>();

        // When we played as Player #1 we are going to check what were the moves
        // of our opponent as Player #2
        for (GameNode gameNodeForPlayerNum1 : listGameNodesReversedPathForPlayerNum1) {

            if ( ( gameNodeForPlayerNum1.isNature() ) || ( gameNodeForPlayerNum1.isRoot() ) ) {

                continue;

            }

            if (gameNodeForPlayerNum1.getAncestor().isPlayer2()) {

                myOpponentMoves.add( gameNodeForPlayerNum1.getLabel() );

            }

        }

        // When we played as Player #2 we are going to check what were the moves
        // of our opponent as Player #1
        for ( GameNode gameNodeForPlayerNum2 : listGameNodesReversedPathForPlayerNum2 ) {

            if ( ( gameNodeForPlayerNum2.isNature() ) || ( gameNodeForPlayerNum2.isRoot() ) ) {

                continue;

            }

            if ( gameNodeForPlayerNum2.getAncestor().isPlayer1() ) {

                myOpponentMoves.add( gameNodeForPlayerNum2.getLabel() );

            }

        }

        for ( String myOpponentMove : myOpponentMoves ) {

            if(showPlayerNum(myOpponentMove) == 1) {

                normalFormGame.addFictitiousPlayLearningMoveForPlayer(1, numCurrentRound,
                                                                      showActionLabel(myOpponentMove));

                this.actionForTheMaximumValueForBeliefsFromCurrentRoundForPlayer1 =
                        normalFormGame.guessMyOpponentNextMoveForPlayer(1, numCurrentRound);

            }
            else {

                normalFormGame.addFictitiousPlayLearningMoveForPlayer(2, numCurrentRound,
                                                                      showActionLabel(myOpponentMove));

                this.actionForTheMaximumValueForBeliefsFromCurrentRoundForPlayer2 =
                        normalFormGame.guessMyOpponentNextMoveForPlayer(2, numCurrentRound);

            }

        }

        return normalFormGame.computeBestResponses(this.actionForTheMaximumValueForBeliefsFromCurrentRoundForPlayer1,
                                                   this.actionForTheMaximumValueForBeliefsFromCurrentRoundForPlayer2);

    }


    @Override
    public void execute() throws InterruptedException {

        System.out.println("\n\n");

        // Waits until the Game Tree become known and available
        while(!this.isTreeKnown()) {

            System.err.println("Waiting for game tree to become available.");

            //noinspection BusyWait
            Thread.sleep(1000);

        }


        GameNode gameNodeFinalForPlayerNum1 = null;
        GameNode gameNodeFinalForPlayerNum2 = null;




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

                    gameNodeFinalForPlayerNum1 =
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

                    gameNodeFinalForPlayerNum2 =
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


                if ( ( gameNodeFinalForPlayerNum1 == null ) || ( gameNodeFinalForPlayerNum2 == null ) ) {

                    // Solve domination
                    IteratedDominanceByLinearProgramming.solveIteratedDominance(normalFormGame);

                    normalFormGame.showMatrixFormGame();

                    if ( normalFormGame.isZeroSumGame() ) {

                        double[][] zeroSumNash = normalFormGame.doZeroSumNashEquilibrium();

                        System.out.println("****ZERO SUM NASH EQUILIBRIUM****");

                        normalFormGame.printNashEquilibriumMatrix(actionLabelsForPlayerNum1,
                                actionLabelsForPlayerNum2,
                                zeroSumNash);


                        for (int currentActionForPlayer1 = 0;
                             currentActionForPlayer1 < actionLabelsForPlayerNum1.length;
                             currentActionForPlayer1++) {

                            myMysteriousGameStrategy.put(actionLabelsForPlayerNum1[currentActionForPlayer1],
                                    ( Math.round( zeroSumNash[0][currentActionForPlayer1] * 100.0 )
                                            / 100.0 ) );

                        }

                        for (int currentActionForPlayer2 = 0;
                             currentActionForPlayer2 < actionLabelsForPlayerNum2.length;
                             currentActionForPlayer2++) {

                            myMysteriousGameStrategy.put(actionLabelsForPlayerNum1[currentActionForPlayer2],
                                    ( Math.round( zeroSumNash[1][currentActionForPlayer2] * 100.0 )
                                            / 100.0 ) );

                        }

                    }
                    else {

                        double[][] generalSumNashEquilibrium = normalFormGame.doGeneralSumNashEquilibrium();

                        System.out.println("****GENERAL SUM NASH EQUILIBRIUM****");

                        normalFormGame.printNashEquilibriumMatrix(actionLabelsForPlayerNum1,
                                actionLabelsForPlayerNum2,
                                generalSumNashEquilibrium);

                        for (int currentActionForPlayer1 = 0;
                             currentActionForPlayer1 < actionLabelsForPlayerNum1.length;
                             currentActionForPlayer1++) {

                            myMysteriousGameStrategy.put(actionLabelsForPlayerNum1[currentActionForPlayer1],
                                    ( Math.round( generalSumNashEquilibrium[0][currentActionForPlayer1] * 100.0 )
                                            / 100.0 ) );

                        }

                        for (int currentActionForPlayer2 = 0;
                             currentActionForPlayer2 < actionLabelsForPlayerNum2.length;
                             currentActionForPlayer2++) {

                            myMysteriousGameStrategy.put(actionLabelsForPlayerNum1[currentActionForPlayer2],
                                    ( Math.round( generalSumNashEquilibrium[1][currentActionForPlayer2] * 100.0 )
                                            / 100.0 ) );

                        }

                    }

                    this.numCurrentRoundsPlayed++;

                }
                else {

                    // The Learning Sample isn't good yet,
                    // so we will play accordingly to the Nash Equilibrium for Zero-Sum/General-Sum
                    // and Iterated Removal of Strategies Strictly Dominated
                    if (this.numCurrentRoundsPlayed < MIN_SIZE_SAMPLE) {

                        // Solve domination
                        IteratedDominanceByLinearProgramming.solveIteratedDominance(normalFormGame);

                        normalFormGame.showMatrixFormGame();

                        if ( normalFormGame.isZeroSumGame() ) {

                            double[][] zeroSumNash = normalFormGame.doZeroSumNashEquilibrium();

                            System.out.println("****ZERO SUM NASH EQUILIBRIUM****");

                            normalFormGame.printNashEquilibriumMatrix(actionLabelsForPlayerNum1,
                                    actionLabelsForPlayerNum2,
                                    zeroSumNash);


                            for (int currentActionForPlayer1 = 0;
                                 currentActionForPlayer1 < actionLabelsForPlayerNum1.length;
                                 currentActionForPlayer1++) {

                                myMysteriousGameStrategy.put(actionLabelsForPlayerNum1[currentActionForPlayer1],
                                        ( Math.round( zeroSumNash[0][currentActionForPlayer1] * 100.0 )
                                                / 100.0 ) );

                            }

                            for (int currentActionForPlayer2 = 0;
                                 currentActionForPlayer2 < actionLabelsForPlayerNum2.length;
                                 currentActionForPlayer2++) {

                                myMysteriousGameStrategy.put(actionLabelsForPlayerNum1[currentActionForPlayer2],
                                        ( Math.round( zeroSumNash[1][currentActionForPlayer2] * 100.0 )
                                                / 100.0 ) );

                            }

                        }
                        else {

                            double[][] generalSumNashEquilibrium = normalFormGame.doGeneralSumNashEquilibrium();

                            System.out.println("****GENERAL SUM NASH EQUILIBRIUM****");

                            normalFormGame.printNashEquilibriumMatrix(actionLabelsForPlayerNum1,
                                    actionLabelsForPlayerNum2,
                                    generalSumNashEquilibrium);

                            for (int currentActionForPlayer1 = 0;
                                 currentActionForPlayer1 < actionLabelsForPlayerNum1.length;
                                 currentActionForPlayer1++) {

                                myMysteriousGameStrategy.put(actionLabelsForPlayerNum1[currentActionForPlayer1],
                                        ( Math.round( generalSumNashEquilibrium[0][currentActionForPlayer1] * 100.0 )
                                                / 100.0 ) );

                            }

                            for (int currentActionForPlayer2 = 0;
                                 currentActionForPlayer2 < actionLabelsForPlayerNum2.length;
                                 currentActionForPlayer2++) {

                                myMysteriousGameStrategy.put(actionLabelsForPlayerNum1[currentActionForPlayer2],
                                        ( Math.round( generalSumNashEquilibrium[1][currentActionForPlayer2] * 100.0 )
                                                / 100.0 ) );

                            }

                        }

                    }

                    // The Learning Sample is already good,
                    // so we will play the Fictitious Play Learning
                    else {

                        List<GameNode> listOfOpponentLastMovesAsPlayer1 =
                                getListGameNodesReversedPath( gameNodeFinalForPlayerNum1 );
                        List<GameNode> listOfOpponentLastMovesAsPlayer2  =
                                getListGameNodesReversedPath( gameNodeFinalForPlayerNum2 );


                        String[] bestResponsesAsActionLabels;

                        try {

                            bestResponsesAsActionLabels =
                                    this.checkMyOpponentMovesAndComputeBestResponses
                                            (listOfOpponentLastMovesAsPlayer1,
                                             listOfOpponentLastMovesAsPlayer2,
                                             normalFormGame,
                                             this.numCurrentRoundsPlayed);

                            // We now set our strategy to have a probability of 1.0 for the moves used
                            // by our adversary in the previous round and zero for the remaining ones.
                            Iterator<String> allAvailableMoves = myMysteriousGameStrategy.keyIterator();

                            while( allAvailableMoves.hasNext() ) {

                                String availableMoveActionLabel = allAvailableMoves.next();

                                if ( ( showActionLabel(availableMoveActionLabel)
                                       .equalsIgnoreCase(bestResponsesAsActionLabels[0]) ) &&
                                     ( showPlayerNum(availableMoveActionLabel) == 1 ) ) {

                                    myMysteriousGameStrategy.put(availableMoveActionLabel, 1d);

                                    System.err.println("Setting " + availableMoveActionLabel +
                                                       " to probability 1.0!!!");

                                }
                                else if ( ( showActionLabel(availableMoveActionLabel)
                                            .equalsIgnoreCase(bestResponsesAsActionLabels[1]) ) &&
                                          ( showPlayerNum(availableMoveActionLabel) == 2 ) ) {

                                    myMysteriousGameStrategy.put(availableMoveActionLabel, 1d);

                                    System.err.println("Setting " + availableMoveActionLabel +
                                                       " to probability 1.0!!!");

                                }

                                else {

                                    myMysteriousGameStrategy.put(availableMoveActionLabel, 0d);

                                    System.err.println("Setting " + availableMoveActionLabel +
                                                       " to probability 0.0!!!");

                                }

                            }

                        }
                        catch(GameNodeDoesNotExistException gameNodeDoesNotExistException) {

                            System.err.println("PANIC: Strategy structure doesn't match the game.");

                        }

                    }

                    this.numCurrentRoundsPlayed++;

                }

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

    public static int showPlayerNum(String actionLabel) {

        return Integer.parseInt(actionLabel.split(":")[0]);

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

}
