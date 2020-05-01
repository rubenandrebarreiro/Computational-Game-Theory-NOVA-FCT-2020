package lp;

import java.util.ArrayList;
import java.util.List;

public class NashEquilibrium {

    public static void main(String[] args) {
        int support_size = 2;
        int total_actions = 8;
        int dominated_actions = 0;
        System.out.println("******** " + support_size + "/" + "(" + total_actions + "-" + dominated_actions + ") ********");
        List<boolean[]> s1=getSubSets(0,support_size,total_actions, new boolean[] {true,true,true,true,true,true,true,true});
        showSubSets(s1);
        dominated_actions = 3;
        System.out.println("******** " + support_size + "/" + "(" + total_actions + "-" + dominated_actions + ") ********");
        s1=getSubSets(0,support_size,total_actions, new boolean[] {true,true,true,true,true,false,false,false});
        showSubSets(s1);
        support_size = 3;
        total_actions = 5;
        dominated_actions = 1;
        System.out.println("******** " + support_size + "/" + "(" + total_actions + "-" + dominated_actions + ") ********");
        s1=getSubSets(0,support_size,total_actions, new boolean[] {false,true,true,true,true});
        showSubSets(s1);
    }

    public static List<boolean[]> getSubSets(int j, int support_size, int total_actions, boolean[] p) {
        boolean[] b = new boolean[total_actions];
        List<boolean[]> subset = new ArrayList<>();
        if (support_size == 0) {
            for (int i = 0; i < b.length; i++)
                b[i] = false;

            subset.add(b);

        } else {
            int op = 0;

            for (int i = j; i < p.length; i++)

                if (p[i])
                    op++;

            if (op == support_size) {

                for (int i = 0; i < b.length; i++)
                    if (p[i])
                        b[i] = true;

                subset.add(b);

            } else {
                if (p[j]) {
                    List<boolean[]> s1 = getSubSets(j + 1, support_size - 1, total_actions, p);
                    for (boolean[] booleans : s1) {
                        b = booleans;
                        b[j] = true;
                        subset.add(b);
                    }
                }
                List<boolean[]> s0 = getSubSets(j + 1, support_size, total_actions, p);
                for (boolean[] booleans : s0) {
                    b = booleans;
                    b[j] = false;
                    subset.add(b);
                }
            }
        }
        return subset;
    }

    public static void showSubSets(List<boolean[]> s) {
        int n = s.get(0).length;
        boolean[] b = new boolean[n];
        for (boolean[] booleans : s) {
            b = booleans;
            System.out.print("{");
            for (int j = 0; j < n; j++)
                if (b[j]) System.out.print(" " + 1);
                else System.out.print(" " + 0);
            System.out.print(" } ");
        }
    }

    public static void showSubset(boolean[] s){
        System.out.print("{");
        for (boolean b : s)
            if (b) System.out.print(" " + 1);
            else System.out.print(" " + 0);
        System.out.print(" } ");
    }

	/* Experimentar com:
		int support_size = 2;
		int total_actions = 8;
		int dominated_actions = 0;
		System.out.println("******** " + support_size + "/" + "(" + total_actions + "-" + dominated_actions + ") ********");
		List<boolean[]> s1=getSubSets(0,support_size,total_actions, new boolean[] {true,true,true,true,true,true,true,true});
		showSubSet(s1);
		dominated_actions = 3;
		System.out.println("******** " + support_size + "/" + "(" + total_actions + "-" + dominated_actions + ") ********");
		s1=getSubSets(0,support_size,total_actions, new boolean[] {true,true,true,true,true,false,false,false});
		showSubSet(s1);
		support_size = 3;
		total_actions = 5;
		dominated_actions = 1;
		System.out.println("******** " + support_size + "/" + "(" + total_actions + "-" + dominated_actions + ") ********");
		s1=getSubSets(0,support_size,total_actions, new boolean[] {false,true,true,true,true});
		showSubSet(s1);
	 */

}
