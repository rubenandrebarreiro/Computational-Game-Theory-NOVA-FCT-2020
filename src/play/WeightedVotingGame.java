package play;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;

public class WeightedVotingGame {

	public int nPlayers;
	public String[] ids;

	public Map<String, Double> weightsByPlayer;
	public int thresholdLimit;
	public double spendingAmount;

	public Map<String, Double> winningCoalitions;


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

		this.ids = new String[nPlayers];

		weightsByPlayer = new HashMap<>();

		valuesFileReader = new Scanner(valuesFile);

		for(int i = 0; i < numLines; i++) {

			String id = (String.valueOf((char) ( 65 + weightsByPlayer.size() )));

			this.ids[i] = id;

			weightsByPlayer.put( id,
								 Double.parseDouble(valuesFileReader.nextLine()));

		}

		this.thresholdLimit = Integer.parseInt(valuesFileReader.nextLine());
		this.spendingAmount = Double.parseDouble(valuesFileReader.nextLine());

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

	public void showSet(long v) {
		boolean showPlayerID = true;
		//boolean showPlayerID = false;
		int power;
		System.out.print("{");
		int cnt = 0;
		for(int i=0;i<nPlayers;i++) {
			power = nPlayers - (i+1);
			if (showPlayerID) {
				if (inSet(i, v)) {
					if (cnt>0) System.out.print(",");
					cnt++;
					System.out.print(ids[power]);
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

	public void showGame() {
		System.out.println("*********** Coalitional Game ***********");
		for (int i=0;i<Math.pow(2, this.nPlayers);i++) {

			showSet(i);
			
			//System.out.println(" ("+v[i]+")");



		}

	}

}
