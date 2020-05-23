package play;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class CoalitionalGame {
	public Double[] v;
	public int nPlayers;
	public String[] ids;

	public CoalitionalGame(Double[] v) {
		this.v=v; 
		this.nPlayers = (int)(Math.log(v.length) / Math.log(2));
		setPlayersID();
	}

	public CoalitionalGame(String filename) throws FileNotFoundException {

		File valuesFile;

		valuesFile = new File(filename);

		Scanner valuesFileReader = new Scanner(valuesFile);

		List<Double> valuesDataList = new ArrayList<>();

		while(valuesFileReader.hasNextLine()) {

			double valueData = Double.parseDouble(valuesFileReader.nextLine());

			valuesDataList.add(valueData);

		}

		this.v = new Double[valuesDataList.size()];

		this.v = valuesDataList.toArray( v );

		this.nPlayers = (int)(Math.log(v.length) / Math.log(2));
		setPlayersID();

	}

	public double computeShapleyValue(String id) {

		System.out.println("*********** Computing Shapley Values ***********");

		double shapleyValue = 0.0;

		int NFact = 1;
		int SFact = 1;
		int factorFact = 1;

		if(nPlayers > 0) {

			for(int i = 1; i <= nPlayers; i++) {

				NFact = NFact * i;

			}

		}

		if(nPlayers > 0) {

			for(int i = 1; i < nPlayers; i++) {

				SFact = SFact * i;

			}

		}

		if(nPlayers > 0) {

			int firstTerm = ( nPlayers - (nPlayers - 1) - 1 );

			if(firstTerm > 0) {

				for (int i = 1; i < firstTerm; i++) {

					factorFact = factorFact * i;

				}

			}

		}

		for(int i = 0; i < v.length; i++) {

			if(!this.checkIDInSet(id, i)) {

				showSet(i);
				System.out.print(" ("+v[i]+") -> ");
				showSetWithOtherID(id, i);
				// TODO

				System.out.print(" ***** gain = ");
				// TODO


			}

		}


		return shapleyValue;

	}

	public void setPlayersID() {  
		int c = 64;
		ids= new String[nPlayers];
		for (int i=nPlayers-1;i>=0;i--) {
			c++;
			ids[i] = (String.valueOf((char)c));
		}
	}
	
	public void showGame() {
		System.out.println("*********** Coalitional Game ***********");
		for (int i=0;i<v.length;i++) {
			showSet(i); 
			System.out.println(" ("+v[i]+")");
		}
	}

	public boolean checkIDInSet(String id, long v) {
		int power;

		List<String> setListIDs = new ArrayList<>();

		int cnt = 0;
		for(int i=0;i<nPlayers;i++) {
			power = nPlayers - (i+1);

			if (inSet(i, v)) {
				setListIDs.add(ids[power]);
			}

		}

		return setListIDs.contains(id);

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

	public void showSetWithOtherID(String id, long v) {
		boolean showPlayerID = true;
		//boolean showPlayerID = false;
		int power;
		System.out.print("{");
		int cnt = 0;
		boolean inserted = false;
		for(int i=0;i<nPlayers;i++) {
			power = nPlayers - (i+1);
			if (showPlayerID) {
				if (inSet(i, v)) {
					if (cnt>0) System.out.print(",");
					cnt++;
					if(id.compareTo(ids[power]) < 0 && !inserted) {
						System.out.print(id);
						inserted = true;
						if (cnt>0) System.out.print(",");
						cnt++;
					}
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

		if(cnt == 0) {

			System.out.print(id);

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
	
	public ArrayList<Integer> getSet(long v) {
		ArrayList<Integer> players = new ArrayList<>(); 
		int power;
		long vi;
		long div;
		long mod;
		for(int i=0;i<nPlayers;i++) {
			power = nPlayers - (i+1);
			vi = (long) Math.pow(2, power);
			div = v / vi;
			mod = div % 2;
			if (mod == 1) players.add(power);
		}
		return players;
	}
	
	public void permutation(int j, int k, int iZero, long v0) {
		long value = 0;
		if (k==0) {
			showSet(v0);
		}
		else {
			int op = 0;
			if (iZero < j) op = nPlayers - j;
			else op = nPlayers - j - 1;
			if (op==k) {
				for(int i=j;i<nPlayers;i++) {		
					if (i != iZero) value += (long) Math.pow(2, nPlayers-(i+1));
				}
				v0 = v0 + value;
				showSet(v0);
			}
			else {	
				if (j != iZero) permutation(j+1,k-1,iZero,v0+(long) Math.pow(2, nPlayers-(j+1)));
				permutation(j+1,k,iZero,v0);
			}
		}
	}

	public static void main(String[] args) throws FileNotFoundException {

		Double[] v1 = {0.0, 0.0, 3.0, 8.0, 2.0, 7.0, 5.0, 10.0, 0.0, 0.0, 4.0, 9.0, 3.0, 8.0, 6.0, 11.0};
		CoalitionalGame c=new CoalitionalGame(v1);
		c.showGame();
		for (int j=0;j<c.nPlayers;j++) {
			System.out.println("*********** Permutations without player "+c.ids[c.nPlayers-1-j]+ " ***********");
			for (int i=0;i<c.nPlayers;i++) {
				System.out.print("With "+i+" players: "); 
				c.permutation(0, i, j, 0);
				System.out.println();
			}
		}

	}

}
