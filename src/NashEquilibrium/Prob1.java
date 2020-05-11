package NashEquilibrium;

import play.NormalFormGame;

public class Prob1 {
    public static void main(String[] args) {
        double[][] test1 = new double[2][3];
        test1[0][0] = 30;
        test1[0][1] = -10;
        test1[0][2] = 20;
        test1[1][0] = -10;
        test1[1][1] = 20;
        test1[1][2] = -20;

        double[][] test2 = new double[2][3];
        test2[0][0] = -30;
        test2[0][1] = 10;
        test2[0][2] = -20;
        test2[1][0] = 10;
        test2[1][1] = -20;
        test2[1][2] = 20;




        NormalFormGame test = new NormalFormGame(test1, test2, new String[]{"T", "B"}, new String[]{"L", "M", "R"} );

        test.showGame();
        test.printNash(new String[]{"T", "B"}, new String[]{"L", "M", "R"}, test.doZeroSumNash());
    }
}
