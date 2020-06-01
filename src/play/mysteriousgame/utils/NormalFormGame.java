package play.mysteriousgame.utils;

import lp.LinearProgramming;
import lp.NashEquilibrium;
import scpsolver.constraints.LinearEqualsConstraint;
import scpsolver.constraints.LinearSmallerThanEqualsConstraint;
import scpsolver.problems.LinearProgram;

import java.util.*;

public class NormalFormGame {

    // Actions Labels for Player #1
    public List<String> actionLabelsForPlayerNum1;

    // Actions Labels for Player #2
    public List<String> actionLabelsForPlayerNum2;

    // Number of Actions of Player #1
    public int numberOfActionsForPlayerNum1;

    // Number of Actions of Player #2
    public int numberOfActionsForPlayerNum2;

    // Rows Currently Considered for Player #1
    // NOTE:
    // - If rowsCurrentlyConsideredForPlayer1[i] == false than Action i of Player #1 is not considered;
    public boolean[] rowsCurrentlyConsideredForPlayer1;

    // Columns Currently Considered for Player #1
    // NOTE:
    // - If columnsCurrentlyConsideredForPlayer2[i] == false than Action i of Player #2 is not considered;
    public boolean[] columnsCurrentlyConsideredForPlayer2;

    // Matrix of Utilities for Player #1
    public double[][] matrixUtilitiesForPlayer1;

    // Matrix of Utilities for Player #2
    public double[][] matrixUtilitiesForPlayer2;

    public Map<Integer, String> fictitiousPlayLearningLastMovesForPlayerNum1;

    public Map<Integer, String> fictitiousPlayLearningLastMovesForPlayerNum2;

    public Map<Integer, Map<String, Double>> fictitiousPlayLearningBeliefsForPlayerNum1;

    public Map<Integer, Map<String, Double>> fictitiousPlayLearningBeliefsForPlayerNum2;


    // Constructors:

    /**
     * Constructor #1:
     * - Constructor of a NormalFormGame with data obtained from the API;
     */
    public NormalFormGame() {

        // Empty Constructor

    }

    /**
     * Constructor #2:
     * - Constructor of a NormalFormGame with data obtained from the API;
     *
     * @param matrixUtilitiesForPlayer1 the Matrix Utilities for Player #1
     * @param matrixUtilitiesForPlayer2 the Matrix Utilities for Player #2
     * @param actionLabelsForPlayerNum1 the Action Labels for Player #1
     * @param actionLabelsForPlayerNum2 the Action Labels for Player #2
     */
    public NormalFormGame(double[][] matrixUtilitiesForPlayer1, double[][] matrixUtilitiesForPlayer2,
                          String[] actionLabelsForPlayerNum1, String[] actionLabelsForPlayerNum2,
                          boolean withFictitiousPlayLearning) {

        // Setting the properties and specifications for Player #1
        this.numberOfActionsForPlayerNum1 = actionLabelsForPlayerNum1.length;
        this.actionLabelsForPlayerNum1 = new ArrayList<>();

        this.rowsCurrentlyConsideredForPlayer1 = new boolean[this.numberOfActionsForPlayerNum1];

        for (int currentActionForPlayer1 = 0;
             currentActionForPlayer1 < this.numberOfActionsForPlayerNum1;
             currentActionForPlayer1++) {

            this.actionLabelsForPlayerNum1
                    .add(actionLabelsForPlayerNum1[currentActionForPlayer1]
                            .substring(actionLabelsForPlayerNum1[currentActionForPlayer1]
                                    .lastIndexOf(':') + 1));

            this.rowsCurrentlyConsideredForPlayer1[currentActionForPlayer1] = true;

        }


        // Setting the properties and specifications for Player #2
        this.numberOfActionsForPlayerNum2 = actionLabelsForPlayerNum2.length;
        this.actionLabelsForPlayerNum2 = new ArrayList<>();

        this.columnsCurrentlyConsideredForPlayer2 = new boolean[this.numberOfActionsForPlayerNum2];

        for (int currentActionForPlayer2 = 0;
             currentActionForPlayer2 < this.numberOfActionsForPlayerNum2;
             currentActionForPlayer2++) {

            this.actionLabelsForPlayerNum2
                    .add(actionLabelsForPlayerNum2[currentActionForPlayer2]
                            .substring(actionLabelsForPlayerNum2[currentActionForPlayer2]
                                    .lastIndexOf(':') + 1));

            this.columnsCurrentlyConsideredForPlayer2[currentActionForPlayer2] = true;

        }


        // Setting the Matrix Utilities for both Players
        this.matrixUtilitiesForPlayer1 = new double[this.numberOfActionsForPlayerNum1]
                [this.numberOfActionsForPlayerNum2];

        this.matrixUtilitiesForPlayer2 = new double[this.numberOfActionsForPlayerNum1]
                [this.numberOfActionsForPlayerNum2];

        for (int currentActionForPlayerNum1 = 0;
             currentActionForPlayerNum1 < this.numberOfActionsForPlayerNum1;
             currentActionForPlayerNum1++) {

            for (int currentActionForPlayerNum2 = 0;
                 currentActionForPlayerNum2 < this.numberOfActionsForPlayerNum2;
                 currentActionForPlayerNum2++) {

                this.matrixUtilitiesForPlayer1[currentActionForPlayerNum1][currentActionForPlayerNum2] =
                        matrixUtilitiesForPlayer1[currentActionForPlayerNum1][currentActionForPlayerNum2];

                this.matrixUtilitiesForPlayer2[currentActionForPlayerNum1][currentActionForPlayerNum2] =
                        matrixUtilitiesForPlayer2[currentActionForPlayerNum1][currentActionForPlayerNum2];

            }

        }

        if (withFictitiousPlayLearning) {

            this.fictitiousPlayLearningLastMovesForPlayerNum1 = new HashMap<>();
            this.fictitiousPlayLearningLastMovesForPlayerNum2 = new HashMap<>();

            this.fictitiousPlayLearningBeliefsForPlayerNum1 = new HashMap<>();
            this.fictitiousPlayLearningBeliefsForPlayerNum2 = new HashMap<>();

            this.fictitiousPlayLearningLastMovesForPlayerNum1.put(0, "");
            this.fictitiousPlayLearningLastMovesForPlayerNum2.put(0, "");

            Map<String, Double> beliefsForPlayer1 = new HashMap<>();
            Map<String, Double> beliefsForPlayer2 = new HashMap<>();

            for (String actionLabelForPlayerNum1 : this.actionLabelsForPlayerNum1) {

                beliefsForPlayer2.put(actionLabelForPlayerNum1, 0.0); //TODO confirmar / valor random

            }


            for (String actionLabelForPlayerNum2 : this.actionLabelsForPlayerNum2) {

                beliefsForPlayer1.put(actionLabelForPlayerNum2, 0.0); //TODO confirmar / valor random

            }

            this.fictitiousPlayLearningBeliefsForPlayerNum1.put(0, beliefsForPlayer1);
            this.fictitiousPlayLearningBeliefsForPlayerNum2.put(0, beliefsForPlayer2);

        }

    }



    /**
     * Adds a Fictitious Play Learning Move for a Player.
     *
     * @param numPlayer the Number of the Player
     *
     * @param numLastRound the Number of the Last Round
     *
     * @param lastMoveFromOpponent the Last Move performed by the Opponent
     *
     */
    public void addFictitiousPlayLearningMoveForPlayer(int numPlayer, int numLastRound, String lastMoveFromOpponent)  {

        if (numPlayer == 1) {

            this.fictitiousPlayLearningLastMovesForPlayerNum1.put(numLastRound, lastMoveFromOpponent);

            Map<String, Double> beliefsFromLastRoundForPlayer1 =
                        this.fictitiousPlayLearningBeliefsForPlayerNum1.get( ( numLastRound - 1 ) );


            Map<String, Double> beliefsForPlayer1 = new HashMap<>();


            for (String actionLabelForPlayerNum2 : this.actionLabelsForPlayerNum2) {

                Double beliefForPlayerLastActionFrequency =
                       beliefsFromLastRoundForPlayer1.get(actionLabelForPlayerNum2);

                if ( lastMoveFromOpponent.equalsIgnoreCase(actionLabelForPlayerNum2) ) {

                    beliefsForPlayer1.put(actionLabelForPlayerNum2, ( beliefForPlayerLastActionFrequency + 1.0 ) );

                }
                else {

                    beliefsForPlayer1.put(actionLabelForPlayerNum2, beliefForPlayerLastActionFrequency);

                }

            }

            this.fictitiousPlayLearningBeliefsForPlayerNum1.put(numLastRound, beliefsForPlayer1);

        }
        else {

            this.fictitiousPlayLearningLastMovesForPlayerNum2.put(numLastRound, lastMoveFromOpponent);

            Map<String, Double> beliefsFromLastRoundForPlayer2 =
                    this.fictitiousPlayLearningBeliefsForPlayerNum2.get( ( numLastRound - 1 ) );


            Map<String, Double> beliefsForPlayer2 = new HashMap<>();


            for (String actionLabelForPlayerNum1 : this.actionLabelsForPlayerNum1) {

                Double beliefForPlayerLastActionFrequency =
                       beliefsFromLastRoundForPlayer2.get(actionLabelForPlayerNum1);

                if ( lastMoveFromOpponent.equalsIgnoreCase(actionLabelForPlayerNum1) ) {

                    beliefsForPlayer2.put(actionLabelForPlayerNum1, ( beliefForPlayerLastActionFrequency + 1.0 ) );

                }
                else {

                    beliefsForPlayer2.put(actionLabelForPlayerNum1, beliefForPlayerLastActionFrequency);

                }

            }

            this.fictitiousPlayLearningBeliefsForPlayerNum2.put(numLastRound, beliefsForPlayer2);

        }

    }

    public String guessMyOpponentNextMoveForPlayer(int numPlayer, int numLastRound) {

        if ( numPlayer == 1 ) {

            Map<String, Double> beliefsFromCurrentRoundForPlayer1 =
                                this.fictitiousPlayLearningBeliefsForPlayerNum1.get( numLastRound );

            double maximumValueForBeliefsFromCurrentRoundForPlayer1 = Double.MIN_VALUE;

            String actionForTheMaximumValueForBeliefsFromCurrentRoundForPlayer1 = "";

            for ( String actionLabelForBeliefsFromCurrentRoundForPlayer1 :
                         beliefsFromCurrentRoundForPlayer1.keySet() ) {

                double currentValueForBeliefsFromCurrentRoundForPlayer1 =
                       beliefsFromCurrentRoundForPlayer1
                               .get(actionLabelForBeliefsFromCurrentRoundForPlayer1);

                if ( maximumValueForBeliefsFromCurrentRoundForPlayer1 <
                     currentValueForBeliefsFromCurrentRoundForPlayer1 ) {

                    maximumValueForBeliefsFromCurrentRoundForPlayer1 =
                            currentValueForBeliefsFromCurrentRoundForPlayer1;

                    actionForTheMaximumValueForBeliefsFromCurrentRoundForPlayer1 =
                            actionLabelForBeliefsFromCurrentRoundForPlayer1;

                }

            }

            return actionForTheMaximumValueForBeliefsFromCurrentRoundForPlayer1;

        }
        else {

            Map<String, Double> beliefsFromCurrentRoundForPlayer2 =
                    this.fictitiousPlayLearningBeliefsForPlayerNum2.get( numLastRound );

            double maximumValueForBeliefsFromCurrentRoundForPlayer2 = Double.MIN_VALUE;

            String actionForTheMaximumValueForBeliefsFromCurrentRoundForPlayer2 = "";

            for ( String actionLabelForBeliefsFromCurrentRoundForPlayer2 :
                         beliefsFromCurrentRoundForPlayer2.keySet() ) {

                double currentValueForBeliefsFromCurrentRoundForPlayer2 =
                        beliefsFromCurrentRoundForPlayer2
                                .get(actionLabelForBeliefsFromCurrentRoundForPlayer2);

                if ( maximumValueForBeliefsFromCurrentRoundForPlayer2 <
                        currentValueForBeliefsFromCurrentRoundForPlayer2 ) {

                    maximumValueForBeliefsFromCurrentRoundForPlayer2 =
                            currentValueForBeliefsFromCurrentRoundForPlayer2;

                    actionForTheMaximumValueForBeliefsFromCurrentRoundForPlayer2 =
                            actionLabelForBeliefsFromCurrentRoundForPlayer2;

                }

            }

            return actionForTheMaximumValueForBeliefsFromCurrentRoundForPlayer2;

        }

    }

    /**
     * Prints/Shows the Matrix Form of the Normal Form Game.
     *
     * NOTES:
     * - The names of the Actions are shortened to the 1st Letter;
     */
    public void showMatrixFormGame() {

        System.out.print("****");

        for (int currentActionForPlayerNum2 = 0;
             currentActionForPlayerNum2 < this.numberOfActionsForPlayerNum2;
             currentActionForPlayerNum2++) {

            if (this.columnsCurrentlyConsideredForPlayer2[currentActionForPlayerNum2]) {

                System.out.print("***********");

            }

        }

        System.out.println();
        System.out.print("  ");

        for (int currentActionForPlayerNum2 = 0;
             currentActionForPlayerNum2 < this.numberOfActionsForPlayerNum2;
             currentActionForPlayerNum2++) {

            if (this.columnsCurrentlyConsideredForPlayer2[currentActionForPlayerNum2]) {

                if (this.actionLabelsForPlayerNum2.size() > 0) {

                    System.out.print("      ");
                    System.out.print(this.actionLabelsForPlayerNum2.get(currentActionForPlayerNum2).substring(0, 1));
                    System.out.print("    ");

                }
                else {

                    System.out.print("\t");
                    System.out.print("Column " + currentActionForPlayerNum2);

                }

            }

        }

        System.out.println();

        for (int currentActionForPlayerNum1 = 0;
             currentActionForPlayerNum1 < this.numberOfActionsForPlayerNum1;
             currentActionForPlayerNum1++) {

            if (this.rowsCurrentlyConsideredForPlayer1[currentActionForPlayerNum1]) {

                if (this.actionLabelsForPlayerNum1.size() > 0) {

                    System.out.print(actionLabelsForPlayerNum1.get(currentActionForPlayerNum1).substring(0, 1) + ": ");

                }
                else {

                    System.out.print("Row " + currentActionForPlayerNum1 + ": ");

                }

                for (int currentActionForPlayerNum2 = 0;
                     currentActionForPlayerNum2 < this.numberOfActionsForPlayerNum2;
                     currentActionForPlayerNum2++) {

                    if (this.columnsCurrentlyConsideredForPlayer2[currentActionForPlayerNum2]) {

                        String stringFormatted = String.format("| %3.0f,%3.0f", this.matrixUtilitiesForPlayer1
                                        [currentActionForPlayerNum1]
                                        [currentActionForPlayerNum2],
                                this.matrixUtilitiesForPlayer2
                                        [currentActionForPlayerNum1]
                                        [currentActionForPlayerNum2]);
                        System.out.print(stringFormatted + "  ");

                    }

                }

                System.out.println("|");

            }

        }

        System.out.print("****");

        for (int currentActionForPlayerNum2 = 0;
             currentActionForPlayerNum2 < this.numberOfActionsForPlayerNum2;
             currentActionForPlayerNum2++) {

            if (this.columnsCurrentlyConsideredForPlayer2[currentActionForPlayerNum2]) {

                System.out.print("***********");

            }

        }

        System.out.println();

    }

    public boolean isZeroSumGame() {

        for (int currentRow = 0; currentRow < this.numberOfActionsForPlayerNum1; currentRow++) {

            if (this.rowsCurrentlyConsideredForPlayer1[currentRow]) {

                for (int currentColumn = 0; currentColumn < this.numberOfActionsForPlayerNum2; currentColumn++) {

                    if (this.columnsCurrentlyConsideredForPlayer2[currentColumn]) {

                        if ( ( matrixUtilitiesForPlayer1[currentRow][currentColumn] +
                               matrixUtilitiesForPlayer2[currentRow][currentColumn] ) != 0 ) {

                            return false;

                        }

                    }

                }

            }

        }

        return true;

    }


    public double[][] doZeroSumNashEquilibrium() {

        double[] zeroSumNashEquilibriumStrategy1;
        double[] zeroSumNashEquilibriumStrategy2;

        ArrayList<Integer> activeRows = new ArrayList<>();

        for (int currentRowForPlayerNum1 = 0;
             currentRowForPlayerNum1 < this.numberOfActionsForPlayerNum1;
             currentRowForPlayerNum1++) {

            if (this.rowsCurrentlyConsideredForPlayer1[currentRowForPlayerNum1]) {

                activeRows.add(currentRowForPlayerNum1);

            }

        }

        ArrayList<Integer> activeColumns = new ArrayList<>();

        for (int currentColumnForPlayerNum2 = 0;
             currentColumnForPlayerNum2 < this.numberOfActionsForPlayerNum2;
             currentColumnForPlayerNum2++) {

            if (this.columnsCurrentlyConsideredForPlayer2[currentColumnForPlayerNum2]) {

                activeColumns.add(currentColumnForPlayerNum2);

            }

        }

        int numRows = activeRows.size();
        int numColumns = activeColumns.size();

        zeroSumNashEquilibriumStrategy1 =
                playerNum1ZeroSumNashEquilibrium(numRows, numColumns, activeRows, activeColumns);

        zeroSumNashEquilibriumStrategy2 =
                playerNum2ZeroSumNashEquilibrium(numRows, numColumns, activeRows, activeColumns);


        return new double[][] { zeroSumNashEquilibriumStrategy1, zeroSumNashEquilibriumStrategy2 };

    }

    double[] playerNum1ZeroSumNashEquilibrium(int numRows, int numColumns,
                                              ArrayList<Integer> activeRows, ArrayList<Integer> activeColumns) {

        // set P terms to one
        double[] termsProbabilities = new double[numRows + 1];

        for (int currentRow = 0; currentRow < numRows; currentRow++) {

            termsProbabilities[currentRow] = 0.0;

        }

        termsProbabilities[numRows] = 1.0;

        // set constraints independent term to
        // utilities of row to dominate
        double[] independentTermsConstraints = new double[ ( numColumns + 1 ) ];

        for (int currentColumn = 0; currentColumn < numColumns; currentColumn++) {

            independentTermsConstraints[currentColumn] = 0.0;

        }

        independentTermsConstraints[numColumns] = 1.0;

        // constraints matrix
        double[][] constraintsMatrix = new double[ ( numColumns + 1 ) ][ ( numRows + 1 ) ];
        double minimumUtility = 0;

        // add utilities to X's
        for (int currentColumn = 0; currentColumn < numColumns; currentColumn++) {

            for (int currentRow = 0; currentRow < numRows; currentRow++) {

                constraintsMatrix[currentColumn][currentRow] =
                        this.matrixUtilitiesForPlayer2[activeRows.get(currentRow)]
                                                      [activeColumns.get(currentColumn)];

                constraintsMatrix[numColumns][currentRow] = 1.0;

                if (constraintsMatrix[currentColumn][currentRow] < minimumUtility) {

                    minimumUtility = constraintsMatrix[currentColumn][currentRow];

                }

            }

            constraintsMatrix[currentColumn][numRows] = -1.0;

        }


        // Set lower bounds
        double[] lowerBounds = new double[ ( numRows + 1 ) ];

        for (int currentRow = 0; currentRow <= numRows; currentRow++) {

            lowerBounds[currentRow] = 0;

        }

        lowerBounds[numRows] = minimumUtility;


        LinearProgram linearProgram = new LinearProgram(termsProbabilities);
        linearProgram.setMinProblem(true);

        for (int currentColumn = 0; currentColumn < numColumns; currentColumn++) {

            linearProgram.addConstraint
                    (
                            new LinearSmallerThanEqualsConstraint
                                    (
                                            constraintsMatrix[currentColumn],
                                            independentTermsConstraints[currentColumn],
                                            "c" + currentColumn
                                    )
                    );

        }

        linearProgram.addConstraint
                (
                        new LinearEqualsConstraint
                                (
                                        constraintsMatrix[numColumns],
                                        independentTermsConstraints[numColumns],
                                        "c" + numColumns
                                )
                );

        linearProgram.setLowerbound(lowerBounds);

        LinearProgramming.showLP(linearProgram);

        double[] linearProgramSolution = LinearProgramming.solveLP(linearProgram);

        LinearProgramming.showSolution(linearProgramSolution, linearProgram);

        return linearProgramSolution;

    }

    double[] playerNum2ZeroSumNashEquilibrium(int numRows, int numColumns,
                                              ArrayList<Integer> activeRows, ArrayList<Integer> activeColumns) {

        // set P terms to one
        double[] termsProbabilities = new double[ ( numColumns + 1 ) ];

        for (int currentColumn = 0; currentColumn < numColumns; currentColumn++) {

            termsProbabilities[currentColumn] = 0.0;

        }

        termsProbabilities[numColumns] = 1.0;

        // set constraints independent term to
        // utilities of row to dominate
        double[] independentTermsConstraints = new double[ ( numRows + 1 ) ];

        for (int currentRow = 0; currentRow < numRows; currentRow++) {

            independentTermsConstraints[currentRow] = 0.0;

        }

        independentTermsConstraints[numRows] = 1;

        // constraints matrix
        double[][] constraintsMatrix = new double[ ( numRows + 1 ) ][ ( numColumns + 1 ) ];
        double minimumUtility = 0;

        // add utilities to X's
        for (int currentRow = 0; currentRow < numRows; currentRow++) {

            for (int currentColumn = 0; currentColumn < numColumns; currentColumn++) {

                constraintsMatrix[currentRow][currentColumn] =
                        this.matrixUtilitiesForPlayer1[activeRows.get(currentRow)]
                                                      [activeColumns.get(currentColumn)];

                constraintsMatrix[numRows][currentColumn] = 1.0;

                if (constraintsMatrix[currentRow][currentColumn] < minimumUtility) {

                    minimumUtility = constraintsMatrix[currentRow][currentColumn];

                }

            }

            constraintsMatrix[currentRow][numColumns] = -1.0;

        }


        // Set lower bounds
        double[] lowerBounds = new double[ ( numColumns + 1 ) ];

        for (int currentColumn = 0; currentColumn <= numColumns; currentColumn++) {

            lowerBounds[currentColumn] = 0;

        }

        lowerBounds[numColumns] = minimumUtility;


        LinearProgram linearProgram = new LinearProgram(termsProbabilities);
        linearProgram.setMinProblem(true);

        for (int currentRow = 0; currentRow < numRows; currentRow++) {

            linearProgram.addConstraint
                    (
                            new LinearSmallerThanEqualsConstraint
                                    (
                                            constraintsMatrix[currentRow],
                                            independentTermsConstraints[currentRow],
                                            "c" + currentRow
                                    )
                    );

        }

        linearProgram.addConstraint
                (
                        new LinearEqualsConstraint
                                (
                                        constraintsMatrix[numRows],
                                        independentTermsConstraints[numRows],
                                        "c" + numRows
                                )
                );

        linearProgram.setLowerbound(lowerBounds);

        LinearProgramming.showLP(linearProgram);
        double[] linearProgramSolution = LinearProgramming.solveLP(linearProgram);

        LinearProgramming.showSolution(linearProgramSolution, linearProgram);

        return linearProgramSolution;

    }

    public double[][] doGeneralSumNashEquilibrium() {

        int maximumSubsetSize = Math.min(this.numberOfActionsForPlayerNum1, this.numberOfActionsForPlayerNum2);

        for (int subsetSize = 1; subsetSize <= maximumSubsetSize; subsetSize++) {

            System.out.println("Size: " + subsetSize + "x" + subsetSize);

            List<boolean[]> rowSubsets = NashEquilibrium.getSubSets(0, subsetSize,
                                                                    this.numberOfActionsForPlayerNum1,
                                                                    this.rowsCurrentlyConsideredForPlayer1);

            List<boolean[]> columnSubsets = NashEquilibrium.getSubSets(0, subsetSize,
                                                                       this.numberOfActionsForPlayerNum2,
                                                                       this.columnsCurrentlyConsideredForPlayer2);

            NashEquilibrium.showSubSets(rowSubsets);
            System.out.print("x ");

            NashEquilibrium.showSubSets(columnSubsets);
            System.out.print("\n");

            // Do all this for every combination of subsets until you find the first
            for (boolean[] rowSubset : rowSubsets) {

                for (boolean[] columnSubset : columnSubsets) {

                    double[] generalSumNashEquilibriumResult = doGeneralSum(rowSubset, columnSubset, subsetSize);

                    if ( generalSumNashEquilibriumResult != null ) {

                        double[] probabilitiesForPlayerNum1 =
                                new double[ this.numberOfActionsForPlayerNum1 ];

                        double[] probabilitiesForPlayerNum2 =
                                new double[ this.numberOfActionsForPlayerNum2 ];


                        int count = 0;

                        for (int currentRow = 0; currentRow < this.numberOfActionsForPlayerNum1; currentRow++) {

                            if (rowSubset[currentRow]) {

                                if (generalSumNashEquilibriumResult[count] == 0.0) {

                                    return null;

                                }

                                probabilitiesForPlayerNum1[currentRow] =
                                        generalSumNashEquilibriumResult[count];

                                count++;

                            }
                            else {

                                probabilitiesForPlayerNum1[currentRow] = 0.0;

                            }

                        }

                        for (int currentColumn = 0; currentColumn < this.numberOfActionsForPlayerNum2; currentColumn++) {

                            if (columnSubset[currentColumn]) {

                                probabilitiesForPlayerNum2[currentColumn] = generalSumNashEquilibriumResult[count];

                                count++;

                            }
                            else {

                                probabilitiesForPlayerNum2[currentColumn] = 0.0;

                            }

                        }

                        return new double[][] { probabilitiesForPlayerNum1, probabilitiesForPlayerNum2 };

                    }

                }

            }

        }

        return null;

    }

    public double[] doGeneralSum(boolean[] rowSubset, boolean[] columnSubset, int subsetSize) {

        System.out.print("TESTING SUBSET ");
        NashEquilibrium.showSubset(rowSubset);

        System.out.print(" x ");
        NashEquilibrium.showSubset(columnSubset);

        System.out.println();


        ArrayList<Integer> activeRows = new ArrayList<>();

        for (int currentRow = 0; currentRow < this.numberOfActionsForPlayerNum1; currentRow++) {

            if (this.rowsCurrentlyConsideredForPlayer1[currentRow]) {

                activeRows.add(currentRow);

            }

        }

        ArrayList<Integer> activeColumns = new ArrayList<>();

        for (int currentColumn = 0; currentColumn < this.numberOfActionsForPlayerNum2; currentColumn++) {

            if (this.columnsCurrentlyConsideredForPlayer2[currentColumn]) {

                activeColumns.add(currentColumn);

            }

        }

        int numNotDominatedRows = activeRows.size();
        int numNotDominatedColumns = activeColumns.size();

        double minimumUtility = 0;

        int numVariables = ( ( subsetSize * 2 ) + 2 );
        int numConstraints = ( numNotDominatedColumns + numNotDominatedRows + 2 );

        //region Define Cs
        double[] termsProbabilities = new double[numVariables]; // All zeros. We're using a function
        //endregion

        //region Define Bs
        double[] independentTermsConstraints = new double[numConstraints];

        // Make sure all probabilities add to one (last two rows of constraints)
        independentTermsConstraints[ ( numConstraints - 2 ) ] = 1.0;
        independentTermsConstraints[ ( numConstraints - 1 ) ] = 1.0;
        //endregion

        //region Define constraints
        double[][] constraintsMatrix = new double[numConstraints][numVariables];

        //region P1 utilities paired with P2's probabilities
        for (int currentNotDominatedRow = 0;
             currentNotDominatedRow < numNotDominatedRows;
             currentNotDominatedRow++) {

            int auxiliaryIndex = subsetSize; // Gets incremented as we find subset elements

            for (Integer activeColumn : activeColumns) {

                if (columnSubset[activeColumn]) {

                    // Get utility from NormalGame
                    double utilitiesForPlayerNum1 =
                            this.matrixUtilitiesForPlayer1[activeRows.get(currentNotDominatedRow)][activeColumn];

                    // Add utility multiplied by the P2's action probability
                    constraintsMatrix[currentNotDominatedRow][auxiliaryIndex] = utilitiesForPlayerNum1;

                    auxiliaryIndex++; // Found subset element, get next index ready

                    if (utilitiesForPlayerNum1 < minimumUtility) {

                        minimumUtility = utilitiesForPlayerNum1;

                    }

                }

            }

            constraintsMatrix[currentNotDominatedRow][ ( numVariables - 2 ) ] = -1.0;

        }

        //region P2 utilities paired with P1's probabilities
        for (int currentNotDominatedColumn = 0;
             currentNotDominatedColumn < numNotDominatedColumns;
             currentNotDominatedColumn++) {

            // Same thing as with P1 but with the offsets so indexes line up
            int auxiliaryIndex = 0;

            for (Integer activeRow : activeRows) {

                if (rowSubset[activeRow]) {

                    double utilitiesForPlayerNum2 =
                            this.matrixUtilitiesForPlayer2[activeRow][activeColumns.get(currentNotDominatedColumn)];

                    constraintsMatrix[ ( currentNotDominatedColumn + numNotDominatedRows ) ][auxiliaryIndex] =
                                                                                            utilitiesForPlayerNum2;

                    auxiliaryIndex++;

                    if (utilitiesForPlayerNum2 < minimumUtility) {

                        minimumUtility = utilitiesForPlayerNum2;

                    }

                }

            }

            constraintsMatrix[currentNotDominatedColumn + numNotDominatedRows][numVariables - 1] = -1.0;

        }
        //endregion

        for (int currentAuxiliaryColumn = 0; currentAuxiliaryColumn < subsetSize; currentAuxiliaryColumn++) {

            constraintsMatrix[ ( numConstraints - 2 ) ][currentAuxiliaryColumn] = 1.0;
            constraintsMatrix[ ( numConstraints - 1 ) ][ ( subsetSize + currentAuxiliaryColumn ) ] = 1.0;

        }
        //endregion

        //region Define Lower Bounds
        double[] lowerBounds = new double[numVariables];

        // Utilities need to be bounded by minUtil if it is less than 0 (it already is initialized to 0)
        lowerBounds[ ( numVariables - 2 ) ] = minimumUtility;
        lowerBounds[ ( numVariables - 1 ) ] = minimumUtility;
        //endregion

        //region Define LP
        LinearProgram linearProgram = new LinearProgram(termsProbabilities);
        linearProgram.setMinProblem(true);

        for (int currentConstraint = 0; currentConstraint < numConstraints; currentConstraint++) {

            if (currentConstraint < numNotDominatedRows) {

                if (rowSubset[activeRows.get(currentConstraint)]) {

                    linearProgram.addConstraint
                            (
                                    new LinearEqualsConstraint
                                            (
                                                    constraintsMatrix[currentConstraint],
                                                    independentTermsConstraints[currentConstraint],
                                                    "c" + currentConstraint
                                            )
                            );

                }
                else {

                    linearProgram.addConstraint
                            (
                                    new LinearSmallerThanEqualsConstraint
                                            (
                                                    constraintsMatrix[currentConstraint],
                                                    independentTermsConstraints[currentConstraint],
                                                    "c" + currentConstraint
                                            )
                            );

                }

            }
            else if (currentConstraint < ( numNotDominatedRows + numNotDominatedColumns ) ) {

                if (columnSubset[ activeColumns.get( ( currentConstraint - numNotDominatedRows ) ) ]) {

                    linearProgram.addConstraint
                            (
                                    new LinearEqualsConstraint
                                            (
                                                    constraintsMatrix[currentConstraint],
                                                    independentTermsConstraints[currentConstraint],
                                                    "c" + currentConstraint
                                            )
                            );

                }
                else {

                    linearProgram.addConstraint
                            (
                                    new LinearSmallerThanEqualsConstraint
                                            (
                                                    constraintsMatrix[currentConstraint],
                                                    independentTermsConstraints[currentConstraint],
                                                    "c" + currentConstraint
                                            )
                            );

                }

            }
            else {

                linearProgram.addConstraint
                        (
                                new LinearEqualsConstraint
                                        (
                                                constraintsMatrix[currentConstraint],
                                                independentTermsConstraints[currentConstraint],
                                                "c" + currentConstraint
                                        )
                        );

            }

        }

        linearProgram.setLowerbound(lowerBounds);
        LinearProgramming.showLP(linearProgram);

        double[] linearProgramSolutions = LinearProgramming.solveLP(linearProgram);

        LinearProgramming.showSolution(linearProgramSolutions, linearProgram);
        //endregion

        if (linearProgramSolutions != null) {

            for (int currentLinearProgramSolution = 0;
                 currentLinearProgramSolution < ( linearProgramSolutions.length - 2 );
                 currentLinearProgramSolution++) {

                if (linearProgramSolutions[currentLinearProgramSolution] == 0.0) {

                    return null;

                }

            }

        }


        System.out.println(Arrays.toString(linearProgramSolutions));

        return linearProgramSolutions;

    }

}
