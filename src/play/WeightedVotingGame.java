package play;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;

public class WeightedVotingGame {

	public int nPlayers;
	public String[] ids;

	public int[] weights;

	public int[][] bitSetCoalitions;
	public double[] vCoalitions;

	public double[] shapleyValuesVector;

	public int thresholdLimit;
	public double spendingAmount;

	public WeightedVotingGame(String filename) throws FileNotFoundException {

		File valuesFile;

		valuesFile = new File(filename);

		Scanner valuesFileReader = new Scanner(valuesFile);

		int numLines = 0;

		while(valuesFileReader.hasNextLine()) {

			numLines++;
			valuesFileReader.nextLine();

		}

		nPlayers = ( numLines - 2 );
		setPlayersID();

		this.weights = new int[nPlayers];
		this.bitSetCoalitions = new int[(int) Math.pow(2, this.nPlayers)][nPlayers];
		this.vCoalitions = new double[(int) Math.pow(2, this.nPlayers)];
		this.shapleyValuesVector = new double[nPlayers];

		valuesFileReader = new Scanner(valuesFile);

		for(int i = 0; i < nPlayers; i++) {

			this.weights[i] = Integer.parseInt(valuesFileReader.nextLine());

		}

		this.thresholdLimit = Integer.parseInt(valuesFileReader.nextLine());
		this.spendingAmount = Double.parseDouble(valuesFileReader.nextLine());

	}

	public void setPlayersID() {

		int c = 64;
		ids= new String[nPlayers];

		for (int i=0;i<nPlayers;i++) {

			c++;
			ids[i] = (String.valueOf((char)c));

		}

	}

	public void buildBitSetCoalitions() {

		BitSet bitSet;

		for(int i = 0;i< (1<<nPlayers) ;i++) {
			bitSet = new BitSet(nPlayers);
			int count = 0;
			int temp = i;
			while (temp > 0) {
				if ((temp % 2) == 1)
					bitSet.set(count);
				temp = temp / 2;
				count++;
			}

			StringBuilder bf = new StringBuilder();
			for (count = nPlayers - 1; count >= 0; count--)
				bf.append((bitSet.get(count) ? 1 : 0));

			for(int j = 0; j < bf.toString().length(); j++) {

				this.bitSetCoalitions[i][j] = Integer.parseInt(String.valueOf(bf.toString().charAt(j)));

			}

		}

	}

	public void printBitSetCoalitions() {

		for(int i = 0; i < bitSetCoalitions.length; i++) {

			System.out.print(i + " = " );

			for (int j = 0; j < bitSetCoalitions[0].length; j++) {

				System.out.print(bitSetCoalitions[i][j]);

			}

			System.out.println();

		}

	}

	public void showGame() {

		System.out.println("*********** Coalitional Game ***********");

		for(int i = 0; i < bitSetCoalitions.length; i++) {

			int count = 0;
			System.out.print("{");

			for (int j = 0; j < bitSetCoalitions[0].length; j++) {

				if(this.bitSetCoalitions[i][j] == 1) {

					if(count > 0) {

						System.out.print(",");

					}

					System.out.print(ids[j]);
					count++;

				}

			}

			System.out.println("} (" + this.vCoalitions[i] +")");

		}

		System.out.println();

	}

	public void buildVCoalitions() {

		for(int i = 0; i < bitSetCoalitions.length; i++) {

			int sumWeights = 0;

			for (int j = 0; j < bitSetCoalitions[0].length; j++) {

				if (this.bitSetCoalitions[i][j] == 1) {

					sumWeights += weights[j];

				}

			}

			if(sumWeights >= this.thresholdLimit) {

				vCoalitions[i] = this.spendingAmount;

			}

		}

	}

	public static void main(String[] args) throws FileNotFoundException {

		WeightedVotingGame weightedVotingGame = new WeightedVotingGame("EC4.txt");
//		WeightedVotingGame weightedVotingGame = new WeightedVotingGame("EC5.txt");
//		WeightedVotingGame weightedVotingGame = new WeightedVotingGame("EC6.txt");

		//        for(String id : coalitionalGame.ids) {
//            System.out.print(id);
//        }
//
//        System.out.println();
//
		weightedVotingGame.buildBitSetCoalitions();
//
		weightedVotingGame.printBitSetCoalitions();

		weightedVotingGame.buildVCoalitions();

		weightedVotingGame.showGame();

	}

}