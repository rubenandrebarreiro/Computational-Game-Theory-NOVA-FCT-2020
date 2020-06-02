package play;

import lp.LinearProgramming;
import lp.NashEquilibrium;
import scpsolver.constraints.LinearEqualsConstraint;
import scpsolver.constraints.LinearSmallerThanEqualsConstraint;
import scpsolver.problems.LinearProgram;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class NormalFormGame {

    public List<String> rowActions;    // actions of player 1
    public List<String> colActions;    // actions of player 2
    public int nRow;                    // number of actions of player 1
    public int nCol;                    // number of actions of player 2
    public boolean[] pRow;                // if pRow[i]==false than action i of player 1 is not considered
    public boolean[] pCol;                // if pCol[j]==false than action j of player 2 is not considered
    public double[][] u1;                // utility matrix of player 1
    public double[][] u2;                // utility matrix of player 2

    int[] bestResponseP1Index, bestResponseP2Index;

    public NormalFormGame() {
    }

    public NormalFormGame(double[][] M1, double[][] M2, String[] labelsP1, String[] labelsP2) {
        /*
         * Constructor of a NormalFormGame with data obtained from the API
         */
        nRow = labelsP1.length;
        rowActions = new ArrayList<String>();
        pRow = new boolean[nRow];
        for (int i = 0; i < nRow; i++) {
            rowActions.add(labelsP1[i].substring(labelsP1[i].lastIndexOf(':') + 1));
            pRow[i] = true;
        }
        nCol = labelsP2.length;
        colActions = new ArrayList<String>();
        pCol = new boolean[nCol];
        for (int j = 0; j < nCol; j++) {
            colActions.add(labelsP2[j].substring(labelsP2[j].lastIndexOf(':') + 1));
            pCol[j] = true;
        }
        u1 = new double[nRow][nCol];
        u2 = new double[nRow][nCol];
        for (int i = 0; i < nRow; i++) {
            for (int j = 0; j < nCol; j++) {
                u1[i][j] = M1[i][j];
                u2[i][j] = M2[i][j];
            }
        }
    }

    public void showGame() {
        /*
         * Prints the game in matrix form. The names of the actions are shortened to the first letter
         */
        System.out.print("****");
        for (int j = 0; j < nCol; j++)
            if (pCol[j])
                System.out.print("***********");
        System.out.println();
        System.out.print("  ");
        for (int j = 0; j < nCol; j++)
            if (pCol[j]) {
                if (colActions.size() > 0) {
                    System.out.print("      ");
                    System.out.print(colActions.get(j).substring(0, 1));
                    System.out.print("    ");
                } else {
                    System.out.print("\t");
                    System.out.print("Col " + j);
                }
            }
        System.out.println();
        for (int i = 0; i < nRow; i++)
            if (pRow[i]) {
                if (rowActions.size() > 0) System.out.print(rowActions.get(i).substring(0, 1) + ": ");
                else System.out.print("Row " + i + ": ");
                for (int j = 0; j < nCol; j++)
                    if (pCol[j]) {
                        String fs = String.format("| %3.0f,%3.0f", u1[i][j], u2[i][j]);
                        System.out.print(fs + "  ");
                    }
                System.out.println("|");
            }
        System.out.print("****");
        for (int j = 0; j < nCol; j++)
            if (pCol[j])
                System.out.print("***********");
        System.out.println();
    }

    // As seen in class
    public double[][] doNash2x2() {

        double[] strategy2 = new double[nCol];
        double[] strategy1 = new double[nRow];


        ArrayList<Integer> iRow = new ArrayList<>();
        for (int i = 0; i < nRow; i++)
            if (pRow[i])
                iRow.add(i);

        ArrayList<Integer> iCol = new ArrayList<>();
        for (int i = 0; i < nCol; i++)
            if (pCol[i])
                iCol.add(i);

        int n1 = iRow.size();
        int n2 = iCol.size();
        if ((n1 != 2) || (n2 != 2)) return null;

        int r0 = iRow.get(0);
        int r1 = iRow.get(1);
        int c0 = iCol.get(0);
        int c1 = iCol.get(1);

        double denominator;

        if ((denominator = u1[r0][c0] + u1[r1][c1] - u1[r0][c1] - u1[r1][c0]) == 0.0)
            return null;

        double p = (u1[r1][c1] - u1[r0][c1]) / denominator;

        strategy2[c0] = Math.round(p * 100.0) / 100.0;
        strategy2[c1] = Math.round((1 - p) * 100.0) / 100.0;

        if ((denominator = u2[r0][c0] + u2[r1][c1] - u2[r0][c1] - u2[r1][c0]) == 0.0)
            return null;

        double q = (u2[r1][c1] - u2[r1][c0]) / denominator;

        strategy1[r0] = Math.round(q * 100.0) / 100.0;
        strategy1[r1] = Math.round((1 - q) * 100.0) / 100.0;

        return new double[][]{strategy1, strategy2};
    }

    public double[][] doZeroSumNash() {

        double[] strategy2;
        double[] strategy1;


        ArrayList<Integer> iRow = new ArrayList<>();
        for (int i = 0; i < nRow; i++)
            if (pRow[i])
                iRow.add(i);

        ArrayList<Integer> jCol = new ArrayList<>();
        for (int i = 0; i < nCol; i++)
            if (pCol[i])
                jCol.add(i);

        int nRows = iRow.size();
        int nCols = jCol.size();

        strategy1 = p1ZeroSum(nRows, nCols, iRow, jCol);

        strategy2 = p2ZeroSum(nCols, nRows, iRow, jCol);


        return new double[][]{strategy1, strategy2};
    }

    public double[][][] doAllGeneralSum() {
        ArrayList<double[][]> equilibria = new ArrayList<>();

        int maxSubsetSize = Math.min(nRow, nCol);

        for (int subsetSize = 1; subsetSize <= maxSubsetSize; subsetSize++) {
            System.out.println("Size: " + subsetSize + "x" + subsetSize);
            List<boolean[]> rowSubsets = NashEquilibrium.getSubSets(0, subsetSize, nRow, pRow);

            List<boolean[]> colSubsets = NashEquilibrium.getSubSets(0, subsetSize, nCol, pCol);

            NashEquilibrium.showSubSets(rowSubsets);
            System.out.print("x ");
            NashEquilibrium.showSubSets(colSubsets);
            System.out.print("\n");


            // Do all this for every combination of subsets
            for (boolean[] rowSubset : rowSubsets) {
                for (boolean[] colSubset : colSubsets) {
                    double[] res = doGeneralSum(rowSubset, colSubset, subsetSize);
                    if (res != null) {
                        checkAndAddAnswer(equilibria, res, colSubset, rowSubset);
                    }
                }
            }
        }
        double[][][] ret = new double[0][][];
        ret = equilibria.toArray(ret);
        return ret;
    }

    void checkAndAddAnswer(List<double[][]> equilibria, double[] res, boolean[] colSubset, boolean[] rowSubset){
        double[] p1 = new double[nRow];
        double[] p2 = new double[nCol];

        int count = 0;
        for(int i = 0; i < nRow; i++){
            if(rowSubset[i]) {
                p1[i] = res[count];
                count++;
            }
            else
                p1[i] = 0.0;
        }

        for (int j = 0; j < nCol; j++) {
            if(colSubset[j]) {
                p2[j] = res[count];
                count++;
            }
            else
                p2[j] = 0.0;
        }
        double[][] cleanRes = new double[][]{p1, p2};
        equilibria.add(cleanRes);
    }

    double[] doGeneralSum(boolean[] rowSubset, boolean[] colSubset, int subsetSize) {
        System.out.print("TESTING SUBSET ");
        NashEquilibrium.showSubset(rowSubset);
        System.out.print(" x ");
        NashEquilibrium.showSubset(colSubset);
        System.out.println();

        ArrayList<Integer> iRow = new ArrayList<>();
        for (int i = 0; i < nRow; i++)
            if (pRow[i])
                iRow.add(i);

        ArrayList<Integer> jCol = new ArrayList<>();
        for (int i = 0; i < nCol; i++)
            if (pCol[i])
                jCol.add(i);

        int nDomRow = iRow.size();
        int nDomCol = jCol.size();

        double minUtil = 0;

        int nVariables = subsetSize * 2 + 2;
        int nConstraints = nDomCol + nDomRow + 2;

        //region Define Cs
        double[] c = new double[nVariables]; // All zeros. We're using a function
        //endregion

        //region Define Bs
        double[] b = new double[nConstraints];
        // Make sure all probabilities add to one (last two rows of constraints)
        b[nConstraints - 2] = 1.0;
        b[nConstraints - 1] = 1.0;
        //endregion

        //region Define constraints
        double[][] A = new double[nConstraints][nVariables];

        //region P1 utilities paired with P2's probabilities
        for (int i = 0; i < nDomRow; i++) {
            int idx = subsetSize; // Gets incremented as we find subset elements
            for (int j = 0; j < nDomCol; j++) {
                if (colSubset[jCol.get(j)]) {
                    double util = u1[iRow.get(i)][jCol.get(j)]; // Get utility from NormalGame
                    A[i][idx] = util; // Add utility multiplied by the P2's action probability

                    idx++; // Found subset element, get next index ready
                    if (util < minUtil)
                        minUtil = util;
                }
            }
            A[i][nVariables - 2] = -1.0;
        }
        //endregion
        System.out.print("");

        //region P2 utilities paired with P1's probabilities
        for (int j = 0; j < nDomCol; j++) {
            // Same thing as with P1 but with the offsets so indexes line up
            int idx = 0;
            for (int i = 0; i < nDomRow; i++) {
                if (rowSubset[iRow.get(i)]) {
                    double util = u2[iRow.get(i)][jCol.get(j)];
                    A[j + nDomRow][idx] = util;

                    idx++;

                    if (util < minUtil)
                        minUtil = util;
                }
            }
            A[j + nDomRow][nVariables - 1] = -1.0;
        }
        //endregion

        System.out.print("");

        for (int j = 0; j < subsetSize; j++) {
            A[nConstraints - 2][j] = 1.0;
            A[nConstraints - 1][subsetSize + j] = 1.0;
        }
        //endregion

        //region Define Lower Bounds
        double[] lb = new double[nVariables];

        // Utilities need to be bounded by minUtil if it is less than 0 (it already is initialized to 0)
        lb[nVariables - 2] = minUtil;
        lb[nVariables - 1] = minUtil;
        //endregion

        //region Define LP
        LinearProgram lp = new LinearProgram(c);
        lp.setMinProblem(true);
        for (int i = 0; i < nConstraints; i++) {
            if (i < nDomRow) {
                if (rowSubset[iRow.get(i)])
                    lp.addConstraint(new LinearEqualsConstraint(A[i], b[i], "c" + i));
                else
                    lp.addConstraint(new LinearSmallerThanEqualsConstraint(A[i], b[i], "c" + i));
            } else if (i < nDomRow + nDomCol) {
                if (colSubset[jCol.get(i - nDomRow)])
                    lp.addConstraint(new LinearEqualsConstraint(A[i], b[i], "c" + i));
                else
                    lp.addConstraint(new LinearSmallerThanEqualsConstraint(A[i], b[i], "c" + i));
            } else {
                lp.addConstraint(new LinearEqualsConstraint(A[i], b[i], "c" + i));
            }
        }
        lp.setLowerbound(lb);
        LinearProgramming.showLP(lp);
        double[] x;
        x = LinearProgramming.solveLP(lp);
        LinearProgramming.showSolution(x, lp);
        //endregion

        if(x != null) {
            for (int i = 0; i < x.length - 2; i++) {
                if (x[i] == 0.0)
                    return null;
            }
        }

        System.out.println(Arrays.toString(x));
        return x;
    }

    public double[][] doFirstGeneralSum() {

        int maxSubsetSize = Math.min(nRow, nCol);

        for (int subsetSize = 1; subsetSize <= maxSubsetSize; subsetSize++) {
            System.out.println("Size: " + subsetSize + "x" + subsetSize);
            List<boolean[]> rowSubsets = NashEquilibrium.getSubSets(0, subsetSize, nRow, pRow);

            List<boolean[]> colSubsets = NashEquilibrium.getSubSets(0, subsetSize, nCol, pCol);

            NashEquilibrium.showSubSets(rowSubsets);
            System.out.print("x ");
            NashEquilibrium.showSubSets(colSubsets);
            System.out.print("\n");

            // Do all this for every combination of subsets until you find the first
            for (boolean[] rowSubset : rowSubsets) {
                for (boolean[] colSubset : colSubsets) {
                    double[] res = doGeneralSum(rowSubset, colSubset, subsetSize);
                    if (res != null) {
                        double[] p1 = new double[nRow];
                        double[] p2 = new double[nCol];

                        int count = 0;
                        for(int i = 0; i < nRow; i++){
                            if(rowSubset[i]) {
                                if(res[count] == 0.0){
                                    return null;
                                }
                                p1[i] = res[count];
                                count++;
                            }
                            else
                                p1[i] = 0.0;
                        }

                        for (int j = 0; j < nCol; j++) {
                            if(colSubset[j]) {

                                p2[j] = res[count];
                                count++;
                            }
                            else
                                p2[j] = 0.0;
                        }
                        double[][] cleanRes = new double[][]{p1, p2};
                        return cleanRes;
                    }
                }
            }
        }
        return null;
    }

    public boolean isZeroSum(){
        for (int i = 0; i < u1.length; i++) {
            for (int j = 0; j < u1[i].length; j++) {
                if(u1[i][j] != u2[i][j]*-1){
                    return false;
                }
            }
        }
        return true;
    }

    double[] p1ZeroSum(int nRows, int nCols, ArrayList<Integer> iRow, ArrayList<Integer> jCol) {

        // set P terms to one
        double[] c = new double[nRows + 1];
        for (int i = 0; i < nRows; i++) {
            c[i] = 0.0;
        }
        c[nRows] = 1.0;

        // set constraints independent term to
        // utilities of row to dominate
        double[] b = new double[nCols + 1];
        for (int j = 0; j < nCols; j++) {
            b[j] = 0.0;
        }
        b[nCols] = 1;

        // constraints matrix
        double[][] A = new double[nCols + 1][nRows + 1];
        double minUtil = 0;

        // add utilites to X's
        for (int j = 0; j < nCols; j++) {
            for (int i = 0; i < nRows; i++) {
                A[j][i] = u2[iRow.get(i)][jCol.get(j)];
                A[nCols][i] = 1.0;
                if (A[j][i] < minUtil) {
                    minUtil = A[j][i];
                }
            }
            A[j][nRows] = -1.0;
        }


        // Set lower bounds
        double[] lb = new double[nRows + 1];
        for (int i = 0; i <= nRows; i++) {
            lb[i] = 0;
        }
        lb[nRows] = minUtil;


        LinearProgram lp = new LinearProgram(c);
        lp.setMinProblem(true);
        for (int j = 0; j < nCols; j++) {
            lp.addConstraint(new LinearSmallerThanEqualsConstraint(A[j], b[j], "c" + j));
        }
        lp.addConstraint(new LinearEqualsConstraint(A[nCols], b[nCols], "c" + nCols));
        lp.setLowerbound(lb);
//        LinearProgramming.showLP(lp);
        double[] x = new double[c.length];
        x = LinearProgramming.solveLP(lp);
//        LinearProgramming.showSolution(x, lp);

        return x;
    }

    double[] p2ZeroSum(int nCols, int nRows, ArrayList<Integer> iRow, ArrayList<Integer> jCol) {

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
                A[i][j] = u1[iRow.get(i)][jCol.get(j)];
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
//        LinearProgramming.showLP(lp);
        double[] x = new double[c.length];
        x = LinearProgramming.solveLP(lp);
//        LinearProgramming.showSolution(x, lp);
        return x;
    }

    public double[] doMaxMinP1(String[] labelsP1, String[] labelsP2) {

        double[][] negU1 = new double[nRow][nCol];

        for(int i = 0; i < nRow; i++) {

            for(int j = 0; j < nCol; j++) {

                negU1[i][j] = u1[i][j] * -1;

            }

        }

        System.out.println("******** Maxmin Strategy Player 1 ********");


        NormalFormGame newGame = new NormalFormGame( u1, negU1, labelsP1, labelsP2);

        /*newGame.showGame();
        Dominance.solveDomination(newGame);
        newGame.showGame();*/


        System.out.println("******** ZeroSum Game Strategy Player 1 ********");

        /*
        os Bs é o que vem depois do igual
        os Cs é o que está a ser minimizado
         */

        /*int nVariables = nRow + 1;
        int nConstraints = nCol + 1;

        //region Define Cs
        double[] c = new double[nVariables]; // All ones. We're using a function
        Arrays.fill(c, 1);
        //endregion

        //region Define Bs
        double[] b = new double[nConstraints];
        b[b.length - 1] = 1.0;

        //region Define constraints
        double[][] A = new double[nConstraints][nVariables];

        //region P1 utilities paired with P2's probabilities
        for (int i = 0; i < (nConstraints - 1); i++) {

            for(int j = 0; j < (nVariables - 1); j++) {

                double util = utilitiesSymmetryForPlayer2[j][i];

                A[i][j] = util;

            }

            A[i][nVariables - 2] = -1.0;

        }

        double minUtil = Double.MAX_VALUE;

        for (int i = 0; i < (nConstraints - 1); i++) {

            for (int j = 0; j < (nVariables - 1); j++) {

                minUtil = Double.min(minUtil, A[i][j]);

            }

        }

        //region Define Lower Bounds
        double[] lb = new double[nVariables];

        // Utilities need to be bounded by minUtil if it is less than 0 (it already is initialized to 0)
        lb[nVariables - 2] = minUtil;
        lb[nVariables - 1] = minUtil;
        //endregion

        LinearProgram lp = new LinearProgram(c);
        lp.setMinProblem(true);

        for (int i = 0; i < ( nConstraints - 1); i++) {
            lp.addConstraint(new LinearSmallerThanEqualsConstraint(A[i], b[i], "c" + i));
        }

        double[] constraintsLastRow = new double[nVariables];
        Arrays.fill(constraintsLastRow, 1);
        constraintsLastRow[constraintsLastRow.length - 1] = 0.0;
        lp.addConstraint(new LinearEqualsConstraint(constraintsLastRow, b[b.length-1], "c" + (b.length-1)));

        lp.setLowerbound(lb);
        LinearProgramming.showLP(lp);
        double[] x;

        x = LinearProgramming.solveLP(lp);
        LinearProgramming.showSolution(x, lp);
        //endregion

        System.out.println(Arrays.toString(x));

        return x;*/

        return newGame.doZeroSumNash()[0];
    }

    public double[] doMaxMinP2(String[] labelsP1, String[] labelsP2) {

        double[][] negU2 = new double[nRow][nCol];

        for(int i = 0; i < nRow; i++) {

            for(int j = 0; j < nCol; j++) {

                negU2[i][j] = u2[i][j] * -1;

            }

        }

        System.out.println("******** Maxmin Strategy Player 2 ********");


        NormalFormGame newGame = new NormalFormGame( negU2, u2, labelsP1, labelsP2);

        /*newGame.showGame();
        Dominance.solveDomination(newGame);
        newGame.showGame();*/


        System.out.println("******** ZeroSum Game Strategy Player 1 ********");

        /*
        os Bs é o que vem depois do igual
        os Cs é o que está a ser minimizado
         */

        /*int nVariables = nRow + 1;
        int nConstraints = nCol + 1;

        //region Define Cs
        double[] c = new double[nVariables]; // All ones. We're using a function
        Arrays.fill(c, 1);
        //endregion

        //region Define Bs
        double[] b = new double[nConstraints];
        b[b.length - 1] = 1.0;

        //region Define constraints
        double[][] A = new double[nConstraints][nVariables];

        //region P1 utilities paired with P2's probabilities
        for (int i = 0; i < (nConstraints - 1); i++) {

            for(int j = 0; j < (nVariables - 1); j++) {

                double util = utilitiesSymmetryForPlayer2[j][i];

                A[i][j] = util;

            }

            A[i][nVariables - 2] = -1.0;

        }

        double minUtil = Double.MAX_VALUE;

        for (int i = 0; i < (nConstraints - 1); i++) {

            for (int j = 0; j < (nVariables - 1); j++) {

                minUtil = Double.min(minUtil, A[i][j]);

            }

        }

        //region Define Lower Bounds
        double[] lb = new double[nVariables];

        // Utilities need to be bounded by minUtil if it is less than 0 (it already is initialized to 0)
        lb[nVariables - 2] = minUtil;
        lb[nVariables - 1] = minUtil;
        //endregion

        LinearProgram lp = new LinearProgram(c);
        lp.setMinProblem(true);

        for (int i = 0; i < ( nConstraints - 1); i++) {
            lp.addConstraint(new LinearSmallerThanEqualsConstraint(A[i], b[i], "c" + i));
        }

        double[] constraintsLastRow = new double[nVariables];
        Arrays.fill(constraintsLastRow, 1);
        constraintsLastRow[constraintsLastRow.length - 1] = 0.0;
        lp.addConstraint(new LinearEqualsConstraint(constraintsLastRow, b[b.length-1], "c" + (b.length-1)));

        lp.setLowerbound(lb);
        LinearProgramming.showLP(lp);
        double[] x;

        x = LinearProgramming.solveLP(lp);
        LinearProgramming.showSolution(x, lp);
        //endregion

        System.out.println(Arrays.toString(x));

        return x;*/

        return newGame.doZeroSumNash()[1];

    }


    public double[] doMinMaxP1(String[] labelsP1, String[] labelsP2) {

        double[][] negU2 = new double[nRow][nCol];

        for(int i = 0; i < nRow; i++) {

            for(int j = 0; j < nCol; j++) {

                negU2[i][j] = u2[i][j] * -1;

            }

        }

        System.out.println("******** Minmax Strategy Player 1 ********");


        NormalFormGame newGame = new NormalFormGame(negU2, u2, labelsP1, labelsP2);

        /*newGame.showGame();
        Dominance.solveDomination(newGame);
        newGame.showGame();*/


        System.out.println("******** ZeroSum Game Strategy Player 1 ********");

        /*
        os Bs é o que vem depois do igual
        os Cs é o que está a ser minimizado
         */

        /*int nVariables = nRow + 1;
        int nConstraints = nCol + 1;

        //region Define Cs
        double[] c = new double[nVariables]; // All ones. We're using a function
        Arrays.fill(c, 1);
        //endregion

        //region Define Bs
        double[] b = new double[nConstraints];
        b[b.length - 1] = 1.0;

        //region Define constraints
        double[][] A = new double[nConstraints][nVariables];

        //region P1 utilities paired with P2's probabilities
        for (int i = 0; i < (nConstraints - 1); i++) {

            for(int j = 0; j < (nVariables - 1); j++) {

                double util = utilitiesSymmetryForPlayer2[j][i];

                A[i][j] = util;

            }

            A[i][nVariables - 2] = -1.0;

        }

        double minUtil = Double.MAX_VALUE;

        for (int i = 0; i < (nConstraints - 1); i++) {

            for (int j = 0; j < (nVariables - 1); j++) {

                minUtil = Double.min(minUtil, A[i][j]);

            }

        }

        //region Define Lower Bounds
        double[] lb = new double[nVariables];

        // Utilities need to be bounded by minUtil if it is less than 0 (it already is initialized to 0)
        lb[nVariables - 2] = minUtil;
        lb[nVariables - 1] = minUtil;
        //endregion

        LinearProgram lp = new LinearProgram(c);
        lp.setMinProblem(true);

        for (int i = 0; i < ( nConstraints - 1); i++) {
            lp.addConstraint(new LinearSmallerThanEqualsConstraint(A[i], b[i], "c" + i));
        }

        double[] constraintsLastRow = new double[nVariables];
        Arrays.fill(constraintsLastRow, 1);
        constraintsLastRow[constraintsLastRow.length - 1] = 0.0;
        lp.addConstraint(new LinearEqualsConstraint(constraintsLastRow, b[b.length-1], "c" + (b.length-1)));

        lp.setLowerbound(lb);
        LinearProgramming.showLP(lp);
        double[] x;

        x = LinearProgramming.solveLP(lp);
        LinearProgramming.showSolution(x, lp);
        //endregion

        System.out.println(Arrays.toString(x));

        return x;*/

        return newGame.doZeroSumNash()[0];

    }

    public double[] doMinMaxP2(String[] labelsP1, String[] labelsP2) {

        double[][] negU1 = new double[nRow][nCol];

        for(int i = 0; i < nRow; i++) {

            for(int j = 0; j < nCol; j++) {

                negU1[i][j] = u1[i][j] * -1;

            }

        }

        System.out.println("******** Minmax Strategy Player 2 ********");


        NormalFormGame newGame = new NormalFormGame( u1, negU1, labelsP1, labelsP2);

        /*newGame.showGame();
        Dominance.solveDomination(newGame);
        newGame.showGame();*/


        System.out.println("******** ZeroSum Game Strategy Player 1 ********");

        /*
        os Bs é o que vem depois do igual
        os Cs é o que está a ser minimizado
         */

        /*int nVariables = nRow + 1;
        int nConstraints = nCol + 1;

        //region Define Cs
        double[] c = new double[nVariables]; // All ones. We're using a function
        Arrays.fill(c, 1);
        //endregion

        //region Define Bs
        double[] b = new double[nConstraints];
        b[b.length - 1] = 1.0;

        //region Define constraints
        double[][] A = new double[nConstraints][nVariables];

        //region P1 utilities paired with P2's probabilities
        for (int i = 0; i < (nConstraints - 1); i++) {

            for(int j = 0; j < (nVariables - 1); j++) {

                double util = utilitiesSymmetryForPlayer2[j][i];

                A[i][j] = util;

            }

            A[i][nVariables - 2] = -1.0;

        }

        double minUtil = Double.MAX_VALUE;

        for (int i = 0; i < (nConstraints - 1); i++) {

            for (int j = 0; j < (nVariables - 1); j++) {

                minUtil = Double.min(minUtil, A[i][j]);

            }

        }

        //region Define Lower Bounds
        double[] lb = new double[nVariables];

        // Utilities need to be bounded by minUtil if it is less than 0 (it already is initialized to 0)
        lb[nVariables - 2] = minUtil;
        lb[nVariables - 1] = minUtil;
        //endregion

        LinearProgram lp = new LinearProgram(c);
        lp.setMinProblem(true);

        for (int i = 0; i < ( nConstraints - 1); i++) {
            lp.addConstraint(new LinearSmallerThanEqualsConstraint(A[i], b[i], "c" + i));
        }

        double[] constraintsLastRow = new double[nVariables];
        Arrays.fill(constraintsLastRow, 1);
        constraintsLastRow[constraintsLastRow.length - 1] = 0.0;
        lp.addConstraint(new LinearEqualsConstraint(constraintsLastRow, b[b.length-1], "c" + (b.length-1)));

        lp.setLowerbound(lb);
        LinearProgramming.showLP(lp);
        double[] x;

        x = LinearProgramming.solveLP(lp);
        LinearProgramming.showSolution(x, lp);
        //endregion

        System.out.println(Arrays.toString(x));

        return x;*/

        return newGame.doZeroSumNash()[1];

    }


    public void printGeneralNash(String[] labelsP1, String[] labelsP2, double[][] nash) {
        System.out.println("Player 1:");
        for (int i = 0; i < labelsP1.length; i++) {
            System.out.println("  " + showLabel(labelsP1[i]) + ": " + (Math.round(nash[0][i] * 100.0) / 100.0));
        }
        System.out.println("Player 2:");
        for (int i = 0; i < labelsP2.length; i++) {
            System.out.println("  " + showLabel(labelsP2[i]) + ": " + (Math.round(nash[1][i] * 100.0) / 100.0));
        }
    }

    public void printNash(String[] labelsP1, String[] labelsP2, double[][] nash) {
        System.out.println("Player 1:");
        for (int i = 0; i < labelsP1.length; i++) {
            System.out.println("  " + showLabel(labelsP1[i]) + ": " + (Math.round(nash[0][i] * 100.0) / 100.0));
        }
        System.out.println("Player 2:");
        for (int i = 0; i < labelsP2.length; i++) {
            System.out.println("  " + showLabel(labelsP2[i]) + ": " + (Math.round(nash[1][i] * 100.0) / 100.0));
        }
    }

    public static String showLabel(String label) {
        return label.substring(label.lastIndexOf(':')+1);
    }

    public double[][] doMinmax(String[] labelsP1, String[] labelsP2) {
        return new double[][] {doMinMaxP1(labelsP1, labelsP2), doMinMaxP2(labelsP1, labelsP2)};
    }

    public double[][] doMaxMin(String[] labelsP1, String[] labelsP2){
        return new double[][] {doMaxMinP1(labelsP1, labelsP2), doMaxMinP2(labelsP1, labelsP2)};
    }

    public double[][] doBestResponse(double[] strategyP1, double[] strategyP2){
        if(bestResponseP1Index == null){
            bestResponseP1Index = new int[strategyP2.length];
            for (int i = 0; i < nCol; i++) {
                for (int j = 0; j < nRow; j++) {
                    if( u1[j][i] > u1[bestResponseP1Index[i]][i] ){
                        bestResponseP1Index[i] = j;
                    }
                }
            }
        }
        if(bestResponseP2Index == null){
            bestResponseP2Index = new int[strategyP1.length];
            for (int i = 0; i < nRow; i++) {
                for (int j = 0; j < nCol; j++) {
                    if( u2[i][j] > u2[i][bestResponseP2Index[i]] ){
                        bestResponseP2Index[i] = j;
                    }
                }
            }
        }
        System.out.println("******BEST RESPONSES******");
        for (int k = 0; k < bestResponseP1Index.length; k++) {
            System.out.println("P1 - Best response to action " + colActions.get(k) + " is " + rowActions.get(bestResponseP1Index[k]));
        }

        for (int k = 0; k < bestResponseP2Index.length; k++) {
            System.out.println("P2 - Best response to action " + rowActions.get(k) + " is " + colActions.get(bestResponseP2Index[k]));
        }

        //probabilities
        double[] bestResponseP1 = new double[strategyP1.length];
        double[] bestResponseP2 = new double[strategyP2.length];

        for (int i = 0; i < strategyP2.length; i++) {
            bestResponseP1[bestResponseP1Index[i]] += strategyP2[i];
        }

        for (int i = 0; i < strategyP1.length; i++) {
            bestResponseP2[bestResponseP1Index[i]] += strategyP1[i];
        }

        return new double[][]{bestResponseP1, bestResponseP2};
    }
}
