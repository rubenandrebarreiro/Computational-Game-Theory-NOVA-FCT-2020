package play;

import gametree.GameNode;
import gametree.GameNodeDoesNotExistException;
import lp.Dominance;
import play.exception.InvalidStrategyException;
import predicts.FrequencyPrediction;

import java.util.Iterator;

public class MysteryGameStrategy extends Strategy {
	FrequencyPrediction prediction;

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
				GameNode fatherP1 = null;
				GameNode finalP2 = null;
			while(! playComplete ) {
				System.out.println("*******************************************************");
				if(myStrategy.getFinalP1Node() != -1) {
					GameNode finalP1 = this.tree.getNodeByIndex(myStrategy.getFinalP1Node());
					fatherP1 = null;
					if(finalP1 != null) {
						try {
							fatherP1 = finalP1.getAncestor();
						} catch (GameNodeDoesNotExistException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						System.out.print("Last round as P1: " + showLabel(fatherP1.getLabel()) + "|" + showLabel(finalP1.getLabel()));
						System.out.println(" -> (Me) " + finalP1.getPayoffP1() + " : (Opp) "+ finalP1.getPayoffP2());
					}
				}			
				if(myStrategy.getFinalP2Node() != -1) {
					finalP2 = this.tree.getNodeByIndex(myStrategy.getFinalP2Node());
					GameNode fatherP2 = null;
					if(finalP2 != null) {
						try {
							fatherP2 = finalP2.getAncestor();
						} catch (GameNodeDoesNotExistException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						System.out.print("Last round as P2: " + showLabel(fatherP2.getLabel()) + "|" + showLabel(finalP2.getLabel()));
						System.out.println(" -> (Opp) " + finalP2.getPayoffP1() + " : (Me) "+ finalP2.getPayoffP2());
					}
				}

				// region  Build Normal Form Game
				// Normal Form Games only!
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

				if(game.isZeroSum()){
					System.out.println("Game is Zero Sum");
					double[][] strategies = game.doMinmax(labelsP1, labelsP2);
					for (int k = 0; k<labelsP1.length; k++) myStrategy.put(labelsP1[k], strategies[0][k]);
					for (int k = 0; k<labelsP2.length; k++) myStrategy.put(labelsP2[k], strategies[1][k]);
					showStrategy(1, strategies[0], labelsP1);
					showStrategy(2, strategies[1], labelsP2);
				} else {
					if(prediction == null) {
						prediction = new FrequencyPrediction(labelsP1.length, labelsP2.length);
					}

					if(myStrategy.isFirstRound()){
						//TODO: Do dominance

						int p1Choice = -1;
						int p1MaxAvg = Integer.MIN_VALUE;
						int p2Choice = -1;
						int p2MaxAvg = Integer.MIN_VALUE;

						for (int k = 0; k < labelsP1.length; k++) {
							int p1Value = 0;
							for (int l = 0; l < labelsP2.length; l++) {
								p1Value += game.u1[k][l];
							}
							p1Value /= labelsP2.length;

							if(p1Value > p1MaxAvg){
								p1MaxAvg = p1Value;
								p1Choice = k;
							}
						}

						for (int l = 0; l < labelsP2.length; l++) {
							int p2Value = 0;
							for (int k = 0; k < labelsP1.length; k++) {
								p2Value += game.u2[k][l];
							}
							p2Value /= labelsP1.length;

							if(p2Value > p2MaxAvg){
								p2MaxAvg = p2Value;
								p2Choice = l;
							}
						}

						double[][] strategies = {new double[labelsP1.length], new double[labelsP2.length]};
						strategies[0][p1Choice] = 1.0;
						strategies[1][p2Choice] = 1.0;
						for (int k = 0; k<labelsP1.length; k++) myStrategy.put(labelsP1[k], strategies[0][k]);
						for (int k = 0; k<labelsP2.length; k++) myStrategy.put(labelsP2[k], strategies[1][k]);
						showStrategy(1, strategies[0], labelsP1);
						showStrategy(2, strategies[1], labelsP2);
					}
					else{
						//TODO: play best response to fictitious
						int p1Idx = -1;
						int p2Idx = -1;

						for (int k = 0; k < labelsP1.length; k++) {
							if(fatherP1.getLabel().equals(labelsP1[k])){
								p1Idx = k;
							}
						}
						for (int k = 0; k < labelsP2.length; k++) {
							if(finalP2.getLabel().equals(labelsP2[k])){
								p2Idx = k;
							}
						}
						System.out.println("************Fictitious***********");

						prediction.newRound(p1Idx, p2Idx);

						double[][] strategies = game.doBestResponse(prediction.getP1Prob(), prediction.getP2Prob());



						for (int k = 0; k<labelsP1.length; k++){
							myStrategy.put(labelsP1[k], strategies[0][k]);
						}
						for (int k = 0; k<labelsP2.length; k++) {
							myStrategy.put(labelsP2[k], strategies[1][k]);
						}
						showStrategy(1, strategies[0], labelsP1);
						showStrategy(2, strategies[1], labelsP2);
					}
				}
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
	
	public double[] setStrategy(int P, String[] labels, double[] strategy, PlayStrategy myStrategy) {
		int n = labels.length;
		for (int i = 0; i<n; i++) myStrategy.put(labels[i], strategy[i]);
		return strategy;
	}
	
	public void showStrategy(int P, double[] strategy, String[] labels) {
		System.out.println("Strategy Player " + P + ":");
		for (int i = 0; i<labels.length; i++) System.out.println("   " + strategy[i] + ":" + showLabel(labels[i]));
	}

}
