package predicts;

public class FrequencyPrediction {
    int[] p1Frequency, p2Frequency;
    double[] p1Prob, p2Prob;
    int plays;

    public FrequencyPrediction(int p1Actions, int p2Actions){
        p1Frequency = new int[p1Actions];
        p2Frequency = new int[p2Actions];

        p1Prob = new double[p1Actions];
        p2Prob = new double[p2Actions];

        plays = 0;
    }

    public void newRound(int p1Action, int p2Action){
        plays++;

        p1Frequency[p1Action]++;
        p2Frequency[p2Action]++;

        for (int i = 0; i < p1Prob.length; i++) {
            p1Prob[i] = p1Frequency[i]/(double)plays;
        }
        for (int i = 0; i < p2Prob.length; i++) {
            p2Prob[i] = p2Frequency[i]/(double)plays;
        }
    }

    public double[] getP1Prob() {
        return p1Prob;
    }

    public double[] getP2Prob() {
        return p2Prob;
    }
}
