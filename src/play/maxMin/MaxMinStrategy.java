package play.maxMin;

import gametree.GameNode;
import gametree.GameNodeDoesNotExistException;
import lp.Dominance;
import play.NormalFormGame;
import play.PlayStrategy;
import play.Strategy;
import play.exception.InvalidStrategyException;

import java.util.Iterator;

public class MaxMinStrategy extends Strategy {

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
				if(myStrategy.getFinalP1Node() != -1) {
					GameNode finalP1 = this.tree.getNodeByIndex(myStrategy.getFinalP1Node());
					GameNode fatherP1 = null;
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
					GameNode finalP2 = this.tree.getNodeByIndex(myStrategy.getFinalP2Node());
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

				// Solve domination
				Dominance.solveDomination(game);

				game.showGame();

				game.printNash(labelsP1, labelsP2, game.doMaxMin(labelsP1, labelsP2));

				double[] strategyP1 = setStrategy(1, labelsP1, myStrategy);
				double[] strategyP2 = setStrategy(2, labelsP2, myStrategy);
				showStrategy(1, strategyP1, labelsP1);
				showStrategy(2, strategyP2, labelsP2);

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
	
	public static String showLabel(String label) {
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
		for (int i = 0; i<n; i++)  strategy[i] = 0;
		if (P==1) { // if playing as player 1 then choose first action
			strategy[0] = 1; 
		}
		else { 		// if playing as player 2 then choose first or second action randomly
			strategy[0] = 0.5;
			strategy[1] = 0.5;
		}
		for (int i = 0; i<n; i++) myStrategy.put(labels[i], strategy[i]);
		return strategy;
	}
	
	public void showStrategy(int P, double[] strategy, String[] labels) {
		System.out.println("Strategy Player " + P + ":");
		for (int i = 0; i<labels.length; i++) System.out.println("   " + strategy[i] + ":" + showLabel(labels[i]));
	}

}
