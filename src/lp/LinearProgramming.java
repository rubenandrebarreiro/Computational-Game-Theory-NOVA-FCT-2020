package lp;

import java.util.ArrayList;
import java.util.Locale;

import scpsolver.constraints.*;
import scpsolver.lpsolver.*;
import scpsolver.problems.*;


public class LinearProgramming {
	static LinearProgram lp;
	static double[] x;
	
	public LinearProgramming() {
	}
	
	
	public static void setLP1() {
		double[] c = { 150.0, 175.0 };
        double[] b = {  77.0,  80.0,  9.0, 6.0 };
        double[][] A = {
                {  7.0, 11.0 },
                { 10.0,  8.0 },
                {  1.0,  0.0 },
                {  0.0,  1.0 },
        };
        double[] lb = {0.0, 0.0};
		lp = new LinearProgram(c);
		lp.setMinProblem(false);
		for (int i = 0; i<b.length; i++)
			lp.addConstraint(new LinearSmallerThanEqualsConstraint(A[i], b[i], "c"+i));
		lp.setLowerbound(lb);
	}

	private static void setLP2() {
		double[] c = {3.0, 2.0, 7.0};
		double[] b = {10, 10};
		double[][] A = {
				{-1, 1, 0},
				{2, -1, 1}
		};
		double[] lb = {0.0, 0.0, 0.0};

		lp = new LinearProgram(c);
		lp.setMinProblem(true);
		lp.addConstraint(new LinearEqualsConstraint(A[0], b[0], "c"+0));
		lp.addConstraint(new LinearBiggerThanEqualsConstraint(A[1], b[1], "c"+1));
		lp.setLowerbound(lb);
	}

	private static void setLP3(){
		double[] c = {1, 1};
		double[] b = {8, 0};
		double[][] A = {
				{0, 2},
				{7, 2}
		};
		double[] lb = {0,0};
		lp = new LinearProgram(c);
		lp.setMinProblem(true);
		for (int i = 0; i < A.length; i++) {
			lp.addConstraint(new LinearBiggerThanEqualsConstraint(A[i], b[i], "c"+i));
		}
		lp.setLowerbound(lb);
	}

	private static void setLP4(){
		double[] c = {1, 1};
		double[] b = {2, 2};
		double[][] A = {
				{8, 0},
				{0, 7}
		};
		double[] lb = {0,0};
		lp = new LinearProgram(c);
		lp.setMinProblem(true);
		for (int i = 0; i < A.length; i++) {
			lp.addConstraint(new LinearBiggerThanEqualsConstraint(A[i], b[i], "c"+i));
		}
		lp.setLowerbound(lb);
	}

	public static boolean solveLP() {
		LinearProgramSolver solver  = SolverFactory.newDefault();
		x = solver.solve(lp);
		if (x==null) return false;
		return true;
	}

	public static void showSolution() {
		if (x==null) System.out.println("*********** NO SOLUTION FOUND ***********");
		else {
			System.out.println("*********** SOLUTION ***********");
			for (int i = 0; i<x.length; i++) System.out.println("x["+i+"] = "+x[i]);
			System.out.println("f(x) = "+ lp.evaluate(x));
		}
	}

	public static void showLP() {
		System.out.println("*********** LINEAR PROGRAMMING PROBLEM ***********");
		String fs;
		if (lp.isMinProblem()) System.out.print("  minimize: ");
		else System.out.print("  maximize: ");
		double[] cf = lp.getC();
		for (int i = 0; i<cf.length; i++) if (cf[i] != 0) {
			fs = String.format(Locale.US,"%+7.1f", cf[i]);
			System.out.print(fs + "*x["+i+"]");
		}
		System.out.println("");
		System.out.print("subject to: ");
		ArrayList<Constraint> lcstr = lp.getConstraints();
		double aij;
		double[] ci = null;
		String str = null;
		for (int i = 0; i<lcstr.size(); i++) {
			if (lcstr.get(i) instanceof LinearSmallerThanEqualsConstraint) {
				str = " <= ";
				ci = ((LinearSmallerThanEqualsConstraint) lcstr.get(i)).getC();
			}
			if (lcstr.get(i) instanceof LinearBiggerThanEqualsConstraint) {
				str = " >= ";
				ci = ((LinearBiggerThanEqualsConstraint) lcstr.get(i)).getC();
			}
			if (lcstr.get(i) instanceof LinearEqualsConstraint) {
				str = " == ";
				ci = ((LinearEqualsConstraint) lcstr.get(i)).getC();
			}
			str = str + String.format(Locale.US,"%6.1f", lcstr.get(i).getRHS());
			if (i != 0) System.out.print("            ");
			for(int j=0;j<lp.getDimension();j++) {
				aij = ci[j];
				if (aij != 0) {
					fs = String.format(Locale.US,"%+7.1f", aij);
					System.out.print(fs + "*x["+j+"]");
				}
				else System.out.print("            ");
			}
			System.out.println(str);
		}
	}


	public static void main(String[] args) {
		//setLP1();
		//setLP2();
		//setLP3();
		setLP4();
		showLP();  
		solveLP();
		showSolution();
		
	}

	public static void showLP(LinearProgram lp) {
		System.out.println("*********** LINEAR PROGRAMMING PROBLEM ***********");
		String fs;
		if (lp.isMinProblem()) System.out.print("  minimize: ");
		else System.out.print("  maximize: ");
		double[] cf = lp.getC();
		for (int i = 0; i<cf.length; i++) if (cf[i] != 0) {
			fs = String.format(Locale.US,"%+7.1f", cf[i]);
			System.out.print(fs + "*x["+i+"]");
		}
		System.out.println("");
		System.out.print("subject to: ");
		ArrayList<Constraint> lcstr = lp.getConstraints();
		double aij;
		double[] ci = null;
		String str = null;
		for (int i = 0; i<lcstr.size(); i++) {
			if (lcstr.get(i) instanceof LinearSmallerThanEqualsConstraint) {
				str = " <= ";
				ci = ((LinearSmallerThanEqualsConstraint) lcstr.get(i)).getC();
			}
			if (lcstr.get(i) instanceof LinearBiggerThanEqualsConstraint) {
				str = " >= ";
				ci = ((LinearBiggerThanEqualsConstraint) lcstr.get(i)).getC();
			}
			if (lcstr.get(i) instanceof LinearEqualsConstraint) {
				str = " == ";
				ci = ((LinearEqualsConstraint) lcstr.get(i)).getC();
			}
			str = str + String.format(Locale.US,"%6.1f", lcstr.get(i).getRHS());
			if (i != 0) System.out.print("            ");
			for(int j=0;j<lp.getDimension();j++) {
				aij = ci[j];
				if (aij != 0) {
					fs = String.format(Locale.US,"%+7.1f", aij);
					System.out.print(fs + "*x["+j+"]");
				}
				else System.out.print("            ");
			}
			System.out.println(str);
		}
	}

	public static double[] solveLP(LinearProgram lp) {
		LinearProgramSolver solver  = SolverFactory.newDefault();
		x = solver.solve(lp);
		return x;
	}

	public static void showSolution(double[] x, LinearProgram lp) {
		if (x==null) System.out.println("*********** NO SOLUTION FOUND ***********");
		else {
			System.out.println("*********** SOLUTION ***********");
			for (int i = 0; i<x.length; i++) System.out.println("x["+i+"] = "+x[i]);
			System.out.println("f(x) = "+ lp.evaluate(x));
		}
	}
}
