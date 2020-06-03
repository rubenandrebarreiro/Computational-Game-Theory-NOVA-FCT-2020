package play.PD;

import gametree.GameNode;
import gametree.GameNodeDoesNotExistException;
import play.NormalFormGame;
import play.PlayStrategy;
import play.Strategy;
import play.exception.InvalidStrategyException;

import java.util.Iterator;


public class FixedRoundsStrategy extends Strategy {

    int numberOfIterations;
    int iteration;
    double cooperatePayoff;
    double defectPayoff;
    double defectToCooperatePayoff;

    boolean grimTriggeredP1 = false;
    boolean grimTriggeredP2 = false;

    @Override
    public void execute() throws InterruptedException {
        while(!this.isTreeKnown()) {
            System.err.println("Waiting for game tree to become available.");
            Thread.sleep(1000);
        }
        while(true) {
            PlayStrategy myStrategy = this.getStrategyRequest();
            if(myStrategy == null) //Game was terminated by an outside event
                break;
            boolean playComplete = false;

            while(! playComplete ) {
                System.out.println("*******************************************************");


                //region Show last round as P1
                if(myStrategy.getFinalP1Node() != -1) {
                    // Last node when playing as P1 (P2's action)
                    GameNode finalP1 = this.tree.getNodeByIndex(myStrategy.getFinalP1Node());
                    GameNode fatherP1 = null;

                    if(finalP1 != null) {
                        try {
                            // Get P1 last action as P1
                            fatherP1 = finalP1.getAncestor();
                        } catch (GameNodeDoesNotExistException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }
                        System.out.print("Last round as P1: " + showLabel(fatherP1.getLabel()) + "|" + showLabel(finalP1.getLabel()));
                        System.out.println(" -> (Me) " + finalP1.getPayoffP1() + " : (Opp) "+ finalP1.getPayoffP2());
                    }
                }
                //endregion

                //region Show last round as P2
                if(myStrategy.getFinalP2Node() != -1) {
                    // Last node when playing as P2 (P2's action)
                    GameNode finalP2 = this.tree.getNodeByIndex(myStrategy.getFinalP2Node());
                    GameNode fatherP2 = null;

                    if(finalP2 != null) {
                        try {
                            // Get P1 last action as P2
                            fatherP2 = finalP2.getAncestor();
                        } catch (GameNodeDoesNotExistException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }

                        System.out.print("Last round as P2: " + showLabel(fatherP2.getLabel()) + "|" + showLabel(finalP2.getLabel()));
                        System.out.println(" -> (Opp) " + finalP2.getPayoffP1() + " : (Me) "+ finalP2.getPayoffP2());
                    }
                }
                //endregion

                //region Show Normal Form Game
                GameNode rootNode = tree.getRootNode();
                int n1 = rootNode.numberOfChildren();
                int n2 = rootNode.getChildren().next().numberOfChildren();
                String[] labelsP1 = new String[n1];
                String[] labelsP2 = new String[n2];
                double[][] U1 = new double[n1][n2];
                double[][] U2 = new double[n1][n2];
                Iterator<GameNode> childrenNodes1 = rootNode.getChildren();
                GameNode childNode1;
                GameNode childNode2;
                int i = 0;
                int j = 0;
                while(childrenNodes1.hasNext()) {
                    childNode1 = childrenNodes1.next();
                    labelsP1[i] = childNode1.getLabel();
                    j = 0;
                    Iterator<GameNode> childrenNodes2 = childNode1.getChildren();
                    while(childrenNodes2.hasNext()) {
                        childNode2 = childrenNodes2.next();
                        if (i==0) labelsP2[j] = childNode2.getLabel();
                        U1[i][j] = childNode2.getPayoffP1();
                        U2[i][j] = childNode2.getPayoffP2();
                        j++;
                    }
                    i++;
                }
                showActions(1,labelsP1);
                showActions(2,labelsP2);
                showUtility(1,U1);
                showUtility(2,U2);
                NormalFormGame game = new NormalFormGame(U1,U2,labelsP1,labelsP2);
                game.showGame();
                //endregion

                cooperatePayoff = U1[0][0]; // Top-left of normal form
                defectPayoff = U1[1][1]; // bottom-right of normal form
                defectToCooperatePayoff = U1[1][0];

                double[] strategyP1 = setStrategy(1,labelsP1,myStrategy);
                double[] strategyP2 = setStrategy(2,labelsP2,myStrategy);
                showStrategy(1,strategyP1,labelsP1);
                showStrategy(2,strategyP2,labelsP2);
                try{
                    this.provideStrategy(myStrategy);
                    playComplete = true;
                } catch (InvalidStrategyException e) {
                    System.err.println("Invalid strategy: " + e.getMessage());;
                    e.printStackTrace(System.err);
                }
            }
        }

    }

    public String showLabel(String label) {
        return label.substring(label.lastIndexOf(':')+1);
    }

    public void showActions(int P, String[] labels) {
        System.out.println("Actions Player " + P + ":");
        for (int i = 0; i<labels.length; i++) System.out.println("   " + showLabel(labels[i]));
    }

    public void showUtility(int P, double[][] M) {
        int nLin = M.length;
        int nCol = M[0].length;
        System.out.println("Utility Player " + P + ":");
        for (int i = 0; i<nLin; i++) {
            for (int j = 0; j<nCol; j++) System.out.print("| " + M[i][j] + " ");
            System.out.println("|");
        }
    }

    public double[] setStrategy(int P, String[] labels, PlayStrategy myStrategy) {
        int n = labels.length;
        double[] strategy = new double[n];

        // TODO: remove this part, only for debug
        /*for (int i = 0; i < strategy.length; i++) {
            strategy[i] = 0.5;
        }*/

        //region strategize for P1
        if(P == 1) {
            if(!grimTriggeredP1) {
                // We're not triggered yet, might Cooperate...
                if (myStrategy.isFirstRound()) {
                    //First round? Cooperate
                    strategy[0] = 1;
                    strategy[1] = 0;
                }
                else{
                    // Check if opponent defected last round
                    GameNode node = tree.getNodeByIndex(myStrategy.getFinalP1Node());
                    if(showLabel(node.getLabel()).equals("Defect")){
                        // GET TRIGGERED
                        grimTriggeredP1 = true;
                    } else{
                        strategy[0] = 1;
                        strategy[1] = 0;
                    }
                }
            }
            // WE'RE TRIGGERED, DEFECT
            if(grimTriggeredP1){
                strategy[0] = 0;
                strategy[1] = 1;
            }
        }
        //endregion

        //region strategize for P2
        else {
            // We're not triggered yet, might Cooperate...
            if (!grimTriggeredP2) {
                if (myStrategy.isFirstRound()) {
                    //First round? Cooperate
                    strategy[0] = 1;
                    strategy[1] = 0;
                } else {
                    // Check if opponent defected last round
                    try {
                        GameNode node = tree.getNodeByIndex(myStrategy.getFinalP2Node()).getAncestor();
                        if (showLabel(node.getLabel()).equals("Defect")) {
                            // GET TRIGGERED
                            grimTriggeredP2 = true;
                            System.out.println("P2 GOT TRIGGERED");
                        } else {
                            strategy[0] = 1;
                            strategy[1] = 0;
                        }
                    } catch (GameNodeDoesNotExistException e) {
                        System.err.println("THIS SHOULD NEVER HAPPEN. WE KNOW WHAT GAME THIS IS");
                        // Default to Defect in this weird scenario
                        strategy[0] = 0;
                        strategy[1] = 1;
                    }
                }
            }
            // WE'RE TRIGGERED, DEFECT
            if (grimTriggeredP2) {
                strategy[0] = 0;
                strategy[1] = 1;
            }
        }
        //endregion

        // region Check if it pays off to defect and defect if so
        int iterationsLeft = myStrategy.getMaximumNumberOfIterations();
        double gainFromDefect = defectToCooperatePayoff - cooperatePayoff;
        double lossFromDefect = (cooperatePayoff - defectPayoff) * (iterationsLeft - 4);

        if(gainFromDefect > lossFromDefect){
            // Defect if it pays off
            strategy[0] = 0;
            strategy[1] = 1;
        }
        // endregion

        // send strategy
        for (int i = 0; i<n; i++) myStrategy.put(labels[i], strategy[i]);
        return strategy;
    }

    public void showStrategy(int P, double[] strategy, String[] labels) {
        System.out.println("Strategy Player " + P + ":");
        for (int i = 0; i<labels.length; i++) System.out.println("   " + strategy[i] + ":" + showLabel(labels[i]));
    }
}
