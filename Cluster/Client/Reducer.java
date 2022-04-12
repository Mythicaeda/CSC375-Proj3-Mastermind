import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.RecursiveTask;

public class Reducer extends RecursiveTask<ArrayList<int[]>>{
    private final List<int[]> list;

    public static List<int[]> fullList;
    public static int[] pegs;
    public static int[] guess;

    private final int loIndex, hiIndex;

    public Reducer(int loIndex, int hiIndex){
        if(loIndex >= hiIndex){throw new IllegalArgumentException();}
        list = fullList.subList(loIndex, hiIndex);
        this.loIndex = loIndex;
        this.hiIndex = hiIndex;
        System.out.println("I'm a reducer of size " +list.size());
    }

    @Override
    public ArrayList<int[]> compute() {
        ArrayList<int[]> toReturn = new ArrayList<>();
        if(list.size() <= 300) {
            //if we would get the same pegs with this code that we currently have, add it to the reduced form
            for(int[] code : list){
                int[] temp = compareWithCode(guess, code);
                if(Arrays.equals(temp, pegs))
                {
                    toReturn.add(code);
                }
            }
            return toReturn;
        }
        //else split
        int midpoint = (hiIndex-loIndex)/2 + loIndex;
        Reducer left = new Reducer(loIndex, midpoint);
        Reducer right = new Reducer(midpoint, hiIndex);
        right.fork();
        //join step
        toReturn.addAll(left.compute());
        toReturn.addAll(right.join());
        System.out.println("I've reduced the solution set to " +toReturn.size() +" codes.");
        return toReturn;
    }

    //wow this actually works
    protected static int[] compareWithCode(int[] guess, int[] code) {
        if(guess.length != code.length){throw new IllegalArgumentException("Guess and Code are of different lengths");}
        int[] corr = new int[guess.length];

        //gather up how many times each color is guessed
        int[] numOfEachColor = new int[6];
        for(int color: guess)
        {
            numOfEachColor[color]++;
        }

        for(int i = 0; i<guess.length; i++)
        {
            //check if there's a right color in the guess
            if(numOfEachColor[code[i]] > 0) {
                corr[i] = 1;
                numOfEachColor[code[i]]--;
            }
            //right color right space
            if(guess[i] == code[i]){corr[i] = 2;}
        }
        Arrays.sort(corr);
        return corr;
    }
}




