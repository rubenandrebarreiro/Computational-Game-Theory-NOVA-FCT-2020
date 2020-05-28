package play.mysteriousgame.utils;

import lp.LinearProgramming;
import scpsolver.constraints.LinearBiggerThanEqualsConstraint;
import scpsolver.problems.LinearProgram;

import java.util.ArrayList;

public class IteratedDominanceByLinearProgramming {
    
    public static void solveIteratedDominance(NormalFormGame normalFormGame) {

        int currentRowForPlayerNum1 = 0;
        int currentColumnForPlayerNum2 = 0;

        boolean changedRow = true;
        boolean changedCol = true;

        do {

            while ( ( currentRowForPlayerNum1 < normalFormGame.rowsCurrentlyConsideredForPlayer1.length ) &&
                    ( !normalFormGame.rowsCurrentlyConsideredForPlayer1[currentRowForPlayerNum1] ) ) {

                currentRowForPlayerNum1++;

            }

            if(currentRowForPlayerNum1 < normalFormGame.rowsCurrentlyConsideredForPlayer1.length) {

                if (dominatedRow(currentRowForPlayerNum1, normalFormGame)) {

                    System.out.println("Row #" + currentRowForPlayerNum1 + " is dominated!");

                    normalFormGame.rowsCurrentlyConsideredForPlayer1[currentRowForPlayerNum1] = false;

                    changedRow = true;

                    currentRowForPlayerNum1 = 0;
                    currentColumnForPlayerNum2 = 0;

                }
                else {

                    changedRow = false;
                    currentRowForPlayerNum1++;

                }

            }

            normalFormGame.showMatrixFormGame();


            while ( ( currentColumnForPlayerNum2 < normalFormGame.columnsCurrentlyConsideredForPlayer2.length ) &&
                    ( !normalFormGame.columnsCurrentlyConsideredForPlayer2[currentColumnForPlayerNum2] ) ) {

                currentColumnForPlayerNum2++;

            }

            if(currentColumnForPlayerNum2 < normalFormGame.columnsCurrentlyConsideredForPlayer2.length) {

                if (dominatedColumn(currentColumnForPlayerNum2, normalFormGame)) {

                    System.out.println("Column " + currentColumnForPlayerNum2 + " is dominated!");

                    normalFormGame.columnsCurrentlyConsideredForPlayer2[currentColumnForPlayerNum2] = false;

                    changedCol = true;

                    currentRowForPlayerNum1 = 0;
                    currentColumnForPlayerNum2 = 0;

                }
                else {

                    changedCol = false;
                    currentColumnForPlayerNum2++;

                }

            }

            normalFormGame.showMatrixFormGame();

        }
        while ( ( currentRowForPlayerNum1 < normalFormGame.rowsCurrentlyConsideredForPlayer1.length ) ||
                ( currentColumnForPlayerNum2 < normalFormGame.columnsCurrentlyConsideredForPlayer2.length ) ||
                  changedRow || changedCol);

    }

    public static boolean dominatedRow(int indexDominatedRow, NormalFormGame normalFormGame){

        ArrayList<Integer> activeRows = new ArrayList<>();

        for (int currentActionForPlayer1 = 0;
             currentActionForPlayer1 < normalFormGame.numberOfActionsForPlayerNum1;
             currentActionForPlayer1++) {

            if ( ( normalFormGame.rowsCurrentlyConsideredForPlayer1[currentActionForPlayer1] ) &&
                    ( currentActionForPlayer1 != indexDominatedRow ) ) {

                activeRows.add(currentActionForPlayer1);

            }

        }


        ArrayList<Integer> activeColumns = new ArrayList<>();

        for (int currentActionForPlayer2 = 0;
             currentActionForPlayer2 < normalFormGame.numberOfActionsForPlayerNum2;
             currentActionForPlayer2++) {

            if (normalFormGame.columnsCurrentlyConsideredForPlayer2[currentActionForPlayer2]) {

                activeColumns.add(currentActionForPlayer2);

            }

        }



        int numRows = activeRows.size();
        int numColumns = activeColumns.size();

        if ( ( numRows == 0 ) || ( numColumns == 0 ) ) {

            return false;

        }

        // set P terms to one
        double[] termsProbabilities = new double[numRows];

        for (int currentRow = 0; currentRow < numRows; currentRow++) {

            termsProbabilities[currentRow] = 1.0;

        }

        // set constraints independent term to
        // utilities of row to dominate
        double[] independentTermsConstraints = new double[numColumns];

        for (int currentColumn = 0; currentColumn < numColumns; currentColumn++) {

            independentTermsConstraints[currentColumn] =
                    normalFormGame.matrixUtilitiesForPlayer1[indexDominatedRow]
                                                            [activeColumns.get(currentColumn)];

        }

        // constraints matrix
        double[][] constraintsMatrix = new double[numColumns][numRows];

        double minimumUtility = 0;

        // add utilities to X's
        for (int currentColumn = 0; currentColumn < numColumns; currentColumn++) {

            for (int currentRow = 0; currentRow < numRows; currentRow++) {

                constraintsMatrix[currentColumn][currentRow] =
                        normalFormGame.matrixUtilitiesForPlayer1[activeRows.get(currentRow)]
                                                                [activeColumns.get(currentColumn)];

                if(constraintsMatrix[currentColumn][currentRow] < minimumUtility){

                   minimumUtility = constraintsMatrix[currentColumn][currentRow];

                }

            }

        }

        // Set lower bounds
        double[] lowerBounds = new double[numRows];

        for (int currentRow = 0; currentRow < numRows; currentRow++) {

            lowerBounds[currentRow] = 0.0;

        }

        if (minimumUtility < 0) {

            System.out.println("Negative utilities detected.\n" +
                               "Offsetting utilities so all are positive.");

            for (int currentColumn = 0; currentColumn < numColumns; currentColumn++) {

                independentTermsConstraints[currentColumn] =
                        ( independentTermsConstraints[currentColumn] - minimumUtility );

                for (int currentRow = 0; currentRow < numRows; currentRow++) {

                    constraintsMatrix[currentColumn][currentRow] =
                            ( constraintsMatrix[currentColumn][currentRow] - minimumUtility );

                }

            }

        }


        LinearProgram linearProgram = new LinearProgram(termsProbabilities);

        linearProgram.setMinProblem(true);

        for (int currentColumn = 0; currentColumn < numColumns; currentColumn++) {

            linearProgram.addConstraint
                (

                    new LinearBiggerThanEqualsConstraint
                            (
                                constraintsMatrix[currentColumn],
                                independentTermsConstraints[currentColumn],
                                "c" + currentColumn
                            )

                );

        }

        linearProgram.setLowerbound(lowerBounds);
        LinearProgramming.showLP(linearProgram);

        double[] linearProgrammingSolution = LinearProgramming.solveLP(linearProgram);

        if (linearProgrammingSolution != null) {

            LinearProgramming.showSolution(linearProgrammingSolution, linearProgram);

            if ( (Math.round(linearProgram.evaluate(linearProgrammingSolution) * 100.0) / 100.0) < 1.0) {

                return true;

            }

            System.out.println("SOLUTION NOT LESS THAN ONE");

        }

        return false;

    }

    public static boolean dominatedColumn(int indexDominatedColumn, NormalFormGame normalFormGame){

        ArrayList<Integer> activeRows = new ArrayList<>();

        for (int currentActionForPlayer1 = 0;
             currentActionForPlayer1 < normalFormGame.numberOfActionsForPlayerNum1;
             currentActionForPlayer1++) {

            if (normalFormGame.rowsCurrentlyConsideredForPlayer1[currentActionForPlayer1]) {

                activeRows.add(currentActionForPlayer1);

            }

        }


        ArrayList<Integer> activeCols = new ArrayList<>();

        for (int currentActionForPlayer2 = 0;
             currentActionForPlayer2 < normalFormGame.numberOfActionsForPlayerNum2;
             currentActionForPlayer2++) {

            if ( ( normalFormGame.columnsCurrentlyConsideredForPlayer2[currentActionForPlayer2] ) &&
                 ( currentActionForPlayer2 != indexDominatedColumn ) ) {

                activeCols.add(currentActionForPlayer2);

            }

         }

        int numRows = activeRows.size();
        int numColumns = activeCols.size();

        if ( ( numRows == 0 ) || ( numColumns == 0 ) ) {

            return false;

        }

        // set P terms to one
        double[] termsProbabilities = new double[numColumns];

        for (int currentColumn = 0; currentColumn < numColumns; currentColumn++){

            termsProbabilities[currentColumn] = 1.0;

        }

        // set constraints independent term to
        // utilities of column to dominate
        double[] independentTermsConstraints = new double[numRows];

        for (int currentRow = 0; currentRow < numRows; currentRow++) {

            independentTermsConstraints[currentRow] =
                    normalFormGame.matrixUtilitiesForPlayer2[activeRows.get(currentRow)]
                                                            [indexDominatedColumn];

        }

        // constraints matrix
        double[][] constraintsMatrix = new double[numRows][numColumns];
        double minimumUtility = 0;

        // add utilities to X's

        for (int currentRow = 0; currentRow < numRows; currentRow++) {

            for (int currentColumn = 0; currentColumn < numColumns; currentColumn++) {

                constraintsMatrix[currentRow][currentColumn] =
                        normalFormGame.matrixUtilitiesForPlayer2[activeRows.get(currentRow)]
                                                                [activeCols.get(currentColumn)];

                if (constraintsMatrix[currentRow][currentColumn] < minimumUtility) {

                    minimumUtility = constraintsMatrix[currentRow][currentColumn];

                }

            }

        }

        // Set lower bounds
        double[] lowerBounds = new double[numColumns];

        for (int currentColumn = 0; currentColumn < numColumns; currentColumn++) {

            lowerBounds[currentColumn] = 0.0;

        }

        if (minimumUtility < 0) {

            System.out.println("Negative utilities detected.\n" +
                               "Offsetting utilities so all are positive.");

            for (int currentRow = 0; currentRow < numRows; currentRow++) {

                independentTermsConstraints[currentRow] =
                        ( independentTermsConstraints[currentRow] - minimumUtility );

                for (int currentColumn = 0; currentColumn < numColumns; currentColumn++) {

                    constraintsMatrix[currentRow][currentColumn] =
                            ( constraintsMatrix[currentRow][currentColumn] - minimumUtility );

                }

            }

        }

        LinearProgram linearProgram = new LinearProgram(termsProbabilities);
        linearProgram.setMinProblem(true);

        for (int currentRow = 0; currentRow < numRows; currentRow++) {

            linearProgram.addConstraint
                    (
                            new LinearBiggerThanEqualsConstraint
                                    (
                                            constraintsMatrix[currentRow],
                                            independentTermsConstraints[currentRow], "c" + currentRow
                                    )
                    );

        }

        linearProgram.setLowerbound(lowerBounds);
        LinearProgramming.showLP(linearProgram);

        double[] linearProgrammingSolution = LinearProgramming.solveLP(linearProgram);

        if (linearProgrammingSolution != null) {

            if ( ( Math.round(linearProgram.evaluate(linearProgrammingSolution) * 100.0) / 100.0 ) < 1.0) {

                System.out.println("lp.evaluate = " + linearProgram.evaluate(linearProgrammingSolution));

                return true;

            }

            System.out.println("SOLUTION NOT LESS THAN ONE");

        }

        return false;

    }

}