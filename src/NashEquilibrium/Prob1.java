package NashEquilibrium;

import play.NormalFormGame;

public class Prob1 {
    public static void main(String[] args) {
        int[][] test1 = new int[2][3];
        test1[0][0] = 30;
        test1[0][1] = -10;
        test1[0][2] = 20;
        test1[1][0] = -10;
        test1[1][1] = 20;
        test1[1][2] = -20;

        int[][] test2 = new int[2][3];
        test2[0][0] = -30;
        test2[0][1] = 10;
        test2[0][2] = -20;
        test2[1][0] = 10;
        test2[1][1] = -20;
        test2[1][2] = 20;




        NormalFormGame test = new NormalFormGame(test1, test2, new String[]{"T", "B"}, new String[]{"L", "M", "R"} );

        test.showGame();
        test.printZeroSumNash(new String[]{"T", "B"}, new String[]{"L", "M", "R"}, test.doZeroSumNash());
    }
}
