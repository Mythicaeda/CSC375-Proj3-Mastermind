import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.ThreadLocalRandom;

public class Board {
    public static final int[] solution = new int[4]; //4 = number of components
                                        //       0            1             2           3           4           5
    public static final Color[] colors = {Color.red, Color.yellow, Color.green, Color.blue, Color.pink, Color.orange };

    private ArrayList<int[]> remainingCodes;

    public Row[] row = new Row[10]; //10 = number of guesses
    private int currentGuess;
    private boolean finished;
    //private int[] odds;

    public Board(){
        //randomly create the solution
        for(int i = 0; i<solution.length; i++)
            solution[i] = ThreadLocalRandom.current().nextInt(6);
        //initialize the rows
        for(int i = 0; i < row.length; i++) {
            row[i] = new Row();
        }
        currentGuess = 0;
        finished = false;
        remainingCodes = new Generator(0,1296,0).compute();
    }

    public void draw(Graphics g){
        int x = 50, y = 100;
        //draw the side panel of available colors
        for (Color color : colors) {
            g.setColor(color);
            g.fillOval(x, y, 50, 50);
            g.setColor(Color.black);
            g.drawOval(x, y, 50, 50);
            y += 70;
        }

        x= 150; y = 50;
        //draw the Rows
        for (Row r: row) {
            for (int i = 0; i<r.guesses.length; i++)
            {
                if(r.guesses[i] == -1){g.setColor(Color.lightGray);}
                else{ g.setColor(colors[r.guesses[i]]);}
                g.fillOval(x, y, 50, 50);
                g.setColor(Color.black);
                g.drawOval(x, y, 50, 50);
                x+= 70;
            }
            y += 70;
            x = 150;
        }
        g.fillRect(x, y, 70*solution.length, 5);
        y += 10;
        //draw the solution
        for (int j : solution) {
            g.setColor(colors[j]);
            g.fillOval(x, y, 50, 50);
            g.setColor(Color.black);
            g.drawOval(x, y, 50, 50);
            x += 70;
        }
        //draw the guesses status
        int tempx = x;
        for(int r = 0; r< row.length; r++){
            y = (50+50/4)*(r+1)+8*r;
            x = tempx;
            for(int i = 0; i< row[r].correctness.length; i++)
            {
                if(row[r].correctness[i] == 0) g.setColor(Color.gray);
                else if(row[r].correctness[i] == 1) g.setColor(Color.white);
                else if(row[r].correctness[i] == 2) g.setColor(Color.black);
                else throw new IllegalArgumentException("One of the rows has an out-of-bounds correctness score of "+row[r].correctness[i]+" at index " +i+".");

                g.fillOval(x, y, 10,10);
                g.setColor(Color.black);
                g.drawOval(x,y, 10,10);
                x+=15;
                if(i == 1) {
                    y+=15;
                    x = tempx;
                }
            }
        }
    }

    public void advance(){
        if(currentGuess == 0) {
            finished = guess(new int[]{0,0,1,1});
        }
        else{
            finished = guess(remainingCodes.get(0));
        }
        currentGuess++;
    }

    private boolean guess(int[] guess) {
        row[currentGuess].setGuesses(guess);
        int[] temp = compareWithCode(guess, solution);
        row[currentGuess].setCorrectness(temp);
        if (Arrays.equals(guess, solution)) {
            return true;
        }
        //else:
        //do the reduction
        Reducer.pegs = temp;
        Reducer.guess = guess;
        remainingCodes = new Reducer(0, remainingCodes.size()).compute();

        return false;
    }

    //wow this actually works
    protected static int[] compareWithCode(int[] guess, int[] code)
    {
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
        return corr;
    }

    public int getCurrentGuess() { return currentGuess; }
    public boolean getFinished() { return finished; }
    public ArrayList<int[]> getRemainingCodes() { return remainingCodes; }

    private static class Row{
        private int[] guesses = new int[] {-1, -1, -1, -1};
        private int[] correctness = new int[4]; //0 = nothing, 1 = right color, wrong space, 2 = right color, right color
        //to get the proper ordering, sort correctness after it's been set
        void setGuesses(int[] newGuess){
            guesses = newGuess;
        }
        void setCorrectness(int[] newCorrectness){
            if(newCorrectness.length != correctness.length){ throw new IllegalArgumentException("Parameter newCorrectnes is different size than correctness");}
            //assign the values, not the object, or else newCorrectness will be messed with, which is bad
            Arrays.sort(newCorrectness);
            for(int i = 0; i < newCorrectness.length; i++)
            {
                correctness[i] = newCorrectness[i];
            }


            //reverse the array cuz sort put it lo to high and i need hi to lo
            int temp;
            int len = correctness.length;
            for (int i = 0; i < len / 2; i++) {
                temp = correctness[i];
                correctness[i] = correctness[len - i - 1];
                correctness[len - i - 1] = temp;
            }
            //Collections.reverse(List.of(correctness));
        }
    }
}
