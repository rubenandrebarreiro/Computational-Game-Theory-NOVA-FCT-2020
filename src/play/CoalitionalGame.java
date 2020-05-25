package play;

import lp.LinearProgramming;
import scpsolver.problems.LinearProgram;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;

public class CoalitionalGame {

	public int nPlayers;

	public String[] ids;

	public int[][] bitSetCoalitions;
	public double[] vCoalitions;

	public double[] shapleyValuesVector;

	public CoalitionalGame(String filename) throws FileNotFoundException {

		File valuesFile;

		valuesFile = new File(filename);

		Scanner valuesFileReader = new Scanner(valuesFile);

		int nLines = 0;

		while(valuesFileReader.hasNextLine()) {

			valuesFileReader.nextLine();
			nLines++;

		}

		this.nPlayers = (int) (Math.log(nLines)/Math.log(2));
		setPlayersID();

		this.bitSetCoalitions = new int[(int) Math.pow(2, this.nPlayers)][nPlayers];
		this.vCoalitions = new double[(int) Math.pow(2, this.nPlayers)];
		this.shapleyValuesVector = new double[nPlayers];

		valuesFileReader = new Scanner(valuesFile);

		int currentLine = 0;

		while(valuesFileReader.hasNextLine()) {

			String lineRead = valuesFileReader.nextLine();

			this.vCoalitions[currentLine] = Double.parseDouble(lineRead);

			currentLine++;

		}

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

	public void showSet(long v) {
		boolean showPlayerID = true;
		//boolean showPlayerID = false;
		int power;
		System.out.print("{");
		int cnt = 0;
		for(int i=0;i<nPlayers;i++) {

			if (showPlayerID) {
				if (inSet(i, v)) {
					if (cnt>0) System.out.print(",");
					cnt++;
					System.out.print(ids[i]);
				}
			}
			else {
				if (cnt>0) System.out.print(",");
				cnt++;
				if (inSet(i, v)) System.out.print(1);
				else System.out.print(0);
			}
		}
		System.out.print("}");
	}


	public boolean inSet(int i, long v) {
		int power;
		long vi;
		long div;
		long mod;
		power = nPlayers - (i+1);
		vi = (long) Math.pow(2, power);
		div = v / vi;
		mod = div % 2;
		return (mod == 1);
	}

	public int getIDIndex(String id) {

		for(int i = 0; i < ids.length; i++) {

			if(id.equalsIgnoreCase(ids[i])) {
				return i;
			}

		}

		return -1;

	}

	public int getVSize(long v) {

		int count = 0;

		for(int i = 0; i < nPlayers; i++) {

			if(this.bitSetCoalitions[(int) v][i] == 1) {

				count++;

			}

		}

		return count;

	}

	public double computeShapleyValue(String id) {

     //   System.out.println("***** Shapley Value for " + id + " *****");

		double shapleyValueSum = 0.0;

		int id_index = this.getIDIndex(id);

		long NFact = this.computeFactorial(nPlayers);

		for(int i = 0; i < vCoalitions.length; i++) {

			if(!this.inSet(id_index, i)) {

				double valueWithoutID = vCoalitions[i];

				int j = i + (int) Math.pow(2, nPlayers - id_index - 1);
				double valueWithID = vCoalitions[j];

//                showSet(i);
//                System.out.print(" ("+valueWithoutID+") -> ");
//                showSet(j);
//                System.out.print(" ("+valueWithID+") ");
//                System.out.print(" ***** gain = ");
//                System.out.println( ( valueWithID - valueWithoutID ) );

				long sizeSetWithoutID = this.getVSize(i);
				long SFact = this.computeFactorial(sizeSetWithoutID);

				long NMinusSMinusOne = ( nPlayers - sizeSetWithoutID - 1 );
				long NMinusSMinusOneFact = this.computeFactorial(NMinusSMinusOne);

				shapleyValueSum += ( SFact * NMinusSMinusOneFact * ( valueWithID - valueWithoutID ) );

			}

		}

//		System.out.println("Shapley Value for " + id + ": " + shapleyValueSum / (double) NFact);

//		System.out.println();

		return shapleyValueSum / (double) NFact;

	}

	public long computeFactorial(long num) {

		if (num == 0) {

			return 1;

		}
		else {

			long fact = 1;

			for(int i = 1; i <= num; i++) {

				fact = fact * i;

			}

			return fact;

		}

	}

	public void computeShapleyValuesVector() {

		for(int i = 0; i < nPlayers; i++) {

			shapleyValuesVector[i] = this.computeShapleyValue(ids[i]);

		}

		System.out.println("*********** Computing Shapley Values ***********");
		System.out.println("Shapley Values:");

		int i = 0;
		for(double shapleyValue : shapleyValuesVector) {

			System.out.println("   " + ids[i] + ":" + shapleyValue);

			i++;

		}

	}

	public boolean isShapleyVectorInTheCore() {

		for(int i = 0; i < this.vCoalitions.length; i++) {

			double shapleyValuesSum = 0.0;

			for(int j = 0; j < this.nPlayers; j++) {

				if(this.bitSetCoalitions[i][j] == 1) {

					shapleyValuesSum += this.shapleyValuesVector[j];

				}

			}

			if( shapleyValuesSum < this.vCoalitions[i] ) {

				isCoreEmpty();
				return false;
			}

		}

		return true;

	}

	private void isCoreEmpty() {
		// Linear Prob verifying if core is empty
		LinearProgram lp = new LinearProgram();
		double[] c = new double[nPlayers];

		double[] b = new double[this.vCoalitions.length];
		for (int i = 0; i < b.length; i++) {
			//TODO: use values from txt
		}

		double[][] A = new double[b.length][c.length];
		for (int i = 0; i < b.length; i++) {
			for (int j = 0; j < c.length; j++) {
				//TODO: pick which agents to put and set their values to 1
			}
		}

		double[] lb = new double[c.length];


	}

	public static void main(String[] args) throws FileNotFoundException {

		CoalitionalGame coalitionalGame = new CoalitionalGame("EC1.txt");
//		CoalitionalGame coalitionalGame = new CoalitionalGame("EC2.txt");
//		CoalitionalGame coalitionalGame = new CoalitionalGame("EC3.txt");

//        for(String id : coalitionalGame.ids) {
//            System.out.print(id);
//        }
//
//        System.out.println();
//
		coalitionalGame.buildBitSetCoalitions();
//
		coalitionalGame.printBitSetCoalitions();

		coalitionalGame.showGame();

		coalitionalGame.computeShapleyValuesVector();

		System.out.println("*********** Verifying if Shapley vector is in the core ***********");

		if(coalitionalGame.isShapleyVectorInTheCore()) {

			System.out.println("Shapley vector is in the core!");

		}
		else {

			System.out.println("Shapley vector is NOT in the core!");

		}

	}

}
