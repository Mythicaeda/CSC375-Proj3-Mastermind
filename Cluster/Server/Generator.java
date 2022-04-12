import java.util.ArrayList;
import java.util.concurrent.RecursiveTask;

public class Generator extends RecursiveTask<ArrayList<int[]>> {
    final int loIndex, hiIndex, startingVal;

   public Generator(int lo, int hi, int startingValue) {
        loIndex = lo;
        hiIndex = hi;
        startingVal = startingValue;
   }

    @Override
    protected ArrayList<int[]> compute() {
       //if we're on the split, solve (might cut down at a future point)
        if(hiIndex-loIndex <= 216) {
            ArrayList<int[]> answer = new ArrayList<>(216);
            for(int hundreds = 0; hundreds<6; hundreds++) {
                for (int tens = 0; tens < 6; tens++) {
                    for (int ones = 0; ones < 6; ones++) {
                        answer.add(new int[]{startingVal, hundreds, tens, ones});
                    }
                }
            }
            return answer;
        }
        int oneSixth = hiIndex/6;
        //else split into six branches (make startingVal the empty string the first go around
        Generator first = new Generator(loIndex, oneSixth,0);
        Generator second = new Generator(oneSixth, oneSixth*2, 1);
        Generator third = new Generator(oneSixth*2, oneSixth*3, 2);
        Generator fourth = new Generator(oneSixth*3, oneSixth*4, 3);
        Generator fifth = new Generator(oneSixth*4, oneSixth*5, 4);
        Generator sixth = new Generator(oneSixth*5, hiIndex, 5); //we'll do this one ourselves
        first.fork(); second.fork(); third.fork(); fourth.fork(); fifth.fork();
        //onto the merging, which is gonna be fun bc i have six arrays. also i don't trust calling join multiple times

        ArrayList<int[]> merged = first.join();
        merged.addAll(second.join());
        merged.addAll(third.join());
        merged.addAll(fourth.join());
        merged.addAll(fifth.join());
        merged.addAll(sixth.compute());
        return merged;
    }
}
