import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.RecursiveTask;

public class Reducer extends RecursiveTask<ArrayList<int[]>> {
    private final List<int[]> list;
    public static int[] pegs;
    public static int[] guess;

    private final int loIndex, hiIndex;

    public Reducer(int loIndex, int hiIndex){
        if(loIndex == hiIndex){throw new IllegalArgumentException();}
        list = Main.getBoard().getRemainingCodes().subList(loIndex, hiIndex);
        this.loIndex = loIndex;
        this.hiIndex = hiIndex;
    }

    @Override
    public ArrayList<int[]> compute() {
        ArrayList<int[]> toReturn = new ArrayList<>();
        if(list.size() <= 300) {
            //if we would get the same pegs with this code that we currently have, add it to the reduced form
            for(int[] code : list){
                int[] temp = Board.compareWithCode(guess, code);
                Arrays.sort(temp);
                if(Arrays.equals(temp, pegs))
                {
                    toReturn.add(code);
                }
            }
            return toReturn;
        }
        //else split
        Reducer left = new Reducer(loIndex, (hiIndex-loIndex)/2 + loIndex);
        Reducer right = new Reducer((hiIndex-loIndex)/2 + loIndex, hiIndex);
        right.fork();
        //join step
        toReturn.addAll(left.compute());
        toReturn.addAll(right.join());
        return toReturn;
    }
}
