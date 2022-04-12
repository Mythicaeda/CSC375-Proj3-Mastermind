import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Client {
    private static ObjectOutputStream out = null;
    private static ObjectInputStream in = null;
    private static boolean shutdown = false;

    public static void main(String[] args) {
        String host = "cs.oswego.edu";
        int portNumber = 9670;

        Socket socket = null;


        //setup
        try {
            socket = new Socket(host, portNumber);
            out = new ObjectOutputStream(socket.getOutputStream());
            in = new ObjectInputStream(socket.getInputStream());
        } catch (UnknownHostException e) {
            System.err.println("Don't know about host " + host);
            e.printStackTrace();
            System.exit(1);
        } catch (IOException e) {
            System.err.println("Couldn't get I/O for the connection.");
            e.printStackTrace();
            System.exit(1);
        }

        try {
            //execution
            while(!shutdown){
                recieveFromServer();
                checkShutdown();
            }
            out.close();
            in.close();
            socket.close();
        } catch (IOException ex) {
            System.err.println("IO failure.");
            ex.printStackTrace();
        }
    }

    public static void sendToServer(List<int[]> list) throws IOException {
        //write out how many items we're sending out to the server
        System.out.println("Sending " + list.size() + " items to the server.");
        out.writeInt(list.size());
        //now write the list
        for (int[] arr : list) {//int[] arr : list){
            for (int i : arr) {
                out.writeInt(i);
            }
            //printArr(list.get(i));
        }
        out.flush();
        System.out.println("Data sent to Server");
    }

    public static void recieveFromServer() throws IOException {
        int[] guess = new int[4];
        int[] pegs = new int[4];

        List<int[]> data = new ArrayList<>();

        //read in the guess, the pegs, and the list to be parsed, in that order
        for (int i = 0; i < guess.length; i++) {
            guess[i] = in.readInt();

        }
        System.out.print("guess: ");
        printArr(guess);

        for (int i = 0; i < pegs.length; i++) {
            pegs[i] = in.readInt();
        }
        System.out.print("pegs: ");
        printArr(pegs);



        //read in how many items were sent in the list
        int size = in.readInt();
        int[] code;
        System.out.println("Recieving "+size+" items from the server.");

        //now read in the list
        for (int l = 0; l < size; l++) {
            code = new int[4];
            for (int i = 0; i < code.length; i++) {
                code[i] = in.readInt();
            }
            //printArr(code);
            data.add(code);
        }

        System.out.println("Recieved "+data.size()+ " entries from Server");

        if(size>0) {
            //use the data and send it back
            Reducer.guess = guess;
            Reducer.pegs = pegs;
            Arrays.sort(Reducer.pegs);
            Reducer.fullList = data;
            List<int[]> reducedData = new Reducer(0, data.size()).compute();//, 0, data.size()).compute();
            sendToServer(reducedData);
        }
        else{
            sendToServer(new ArrayList<>());
        }
    }

    private static void printArr(int[] arr){
        for(int i: arr) {
            System.out.print(i + ", ");
        }
        System.out.println();
    }



    public static void checkShutdown() throws IOException{
        shutdown = in.readBoolean();
    }
}
