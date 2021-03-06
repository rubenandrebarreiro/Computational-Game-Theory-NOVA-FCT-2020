package NashEquilibrium;

import play.NormalFormGame;

public class GeneralSum1 {
    public static void main(String[] args) {
        double[][] test1 = new double[3][3];
        test1[0][0] = 0;
        test1[0][1] = 0;
        test1[0][2] = 1;
        test1[1][0] = 0;
        test1[1][1] = 2;
        test1[1][2] = 0;
        test1[2][0] = 4;
        test1[2][1] = 0;
        test1[2][2] = 0;

        double[][] test2 = new double[3][3];
        test2[0][0] = 0;
        test2[0][1] = 0;
        test2[0][2] = 2;
        test2[1][0] = 3;
        test2[1][1] = 0;
        test2[1][2] = 0;
        test2[2][0] = 0;
        test2[2][1] = 2;
        test2[2][2] = 0;


        NormalFormGame test = new NormalFormGame(test1, test2, new String[]{"a", "c", "b"}, new String[]{"C", "B", "A"} );

        test.showGame();
        double[][][] asd = test.doAllGeneralSum();
        for (int i = 0; i < asd.length; i++) {
            System.out.println("*** GENERAL SUM EQUILIBRIUM: " + i + " ***");
            test.printNash(new String[]{"a", "c", "b"}, new String[]{"C", "B", "A"}, asd[i]);
        }
        double[][] dsa = test.doFirstGeneralSum();
        System.out.println("***GENERAL SUM EQUILIBRIUM***");
        test.printNash(new String[]{"a", "c", "b"}, new String[]{"C", "B", "A"}, dsa);
    }
}
