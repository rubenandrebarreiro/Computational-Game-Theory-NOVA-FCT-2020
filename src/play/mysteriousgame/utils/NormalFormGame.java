package play.mysteriousgame.utils;

import lp.LinearProgramming;
import scpsolver.constraints.LinearEqualsConstraint;
import scpsolver.constraints.LinearSmallerThanEqualsConstraint;
import scpsolver.problems.LinearProgram;

import java.util.ArrayList;
import java.util.List;

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
                          String[] actionLabelsForPlayerNum1, String[] actionLabelsForPlayerNum2) {

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

    }

    /**
     * Prints/Shows the Matrix Form of the Normal Form Game.
     * <p>
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

                } else {

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

                } else {

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

                        if ((matrixUtilitiesForPlayer1[currentRow][currentColumn] +
                                matrixUtilitiesForPlayer2[currentRow][currentColumn]) != 0) {

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
                playerNum2ZeroSumNashEquilibrium(numColumns, numRows, activeRows, activeColumns);


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

                constraintsMatrix[currentColumn][currentRow] = this.matrixUtilitiesForPlayer2[activeRows.get(currentRow)][activeColumns.get(currentColumn)];
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

        linearProgram.addConstraint(new LinearEqualsConstraint(constraintsMatrix[numColumns], independentTermsConstraints[numColumns], "c" + numColumns));
        linearProgram.setLowerbound(lowerBounds);

        LinearProgramming.showLP(linearProgram);

        double[] linearProgramSolution = LinearProgramming.solveLP(linearProgram);

        LinearProgramming.showSolution(linearProgramSolution, linearProgram);

        return linearProgramSolution;

    }

    double[] playerNum2ZeroSumNashEquilibrium(int nCols, int nRows, ArrayList<Integer> iRow, ArrayList<Integer> jCol) {

        // set P terms to one
        double[] c = new double[nCols + 1];
        for (int i = 0; i < nCols; i++) {
            c[i] = 0.0;
        }
        c[nCols] = 1.0;

        // set constraints independent term to
        // utilities of row to dominate
        double[] b = new double[nRows + 1];
        for (int j = 0; j < nRows; j++) {
            b[j] = 0.0;
        }
        b[nRows] = 1;

        // constraints matrix
        double[][] A = new double[nRows + 1][nCols + 1];
        double minUtil = 0;

        // add utilites to X's
        for (int i = 0; i < nRows; i++) {
            for (int j = 0; j < nCols; j++) {
                A[i][j] = matrixUtilitiesForPlayer1[iRow.get(i)][jCol.get(j)];
                A[nRows][j] = 1.0;
                if (A[i][j] < minUtil) {
                    minUtil = A[i][j];
                }
            }
            A[i][nCols] = -1.0;
        }


        // Set lower bounds
        double[] lb = new double[nCols + 1];
        for (int j = 0; j <= nCols; j++) {
            lb[j] = 0;
        }
        lb[nCols] = minUtil;


        LinearProgram lp = new LinearProgram(c);
        lp.setMinProblem(true);
        for (int i = 0; i < nRows; i++) {
            lp.addConstraint(new LinearSmallerThanEqualsConstraint(A[i], b[i], "c" + i));
        }
        lp.addConstraint(new LinearEqualsConstraint(A[nRows], b[nRows], "c" + nRows));
        lp.setLowerbound(lb);
        LinearProgramming.showLP(lp);
        double[] x = new double[c.length];
        x = LinearProgramming.solveLP(lp);
        LinearProgramming.showSolution(x, lp);
        return x;
    }

}
