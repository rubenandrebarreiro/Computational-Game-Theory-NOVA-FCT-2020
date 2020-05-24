public class Permutations {


    public static void permGen(char[] s,int i,int k,char[] buff) {
        if(i<k) {
            for(int j=0;j<s.length;j++) {

                buff[i] = s[j];
                permGen(s,i+1,k,buff);
            }
        }
        else {

            System.out.println(String.valueOf(buff));

        }

    }

    public static void main(String[] args) {
        char[] database = {'a', 'b', 'c'};
        char[] buff = new char[database.length];
        int k = database.length;
        for(int i=1;i<=k;i++) {
            permGen(database,0,i,buff);
        }

    }

}