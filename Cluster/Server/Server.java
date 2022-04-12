import java.net.*;
import java.io.*;
import java.util.ArrayList;
import java.util.concurrent.CountDownLatch;

public class Server {
    static final int PORT = 9670;

    static final ServerThread[] threads = new ServerThread[3];

    public static void main() {
        try {
            ServerSocket serverSocket = new ServerSocket(PORT);
            for( int i = 0; i<3; i++ ) {
                threads[i] = new ServerThread(serverSocket.accept());
                //threads[i].start();
            }
        } catch (IOException ex) {
            ex.printStackTrace();
            System.exit(-1);
        }
    }

    public static ArrayList<int[]> processCodes(ArrayList<int[]> codes, int[] guess, int[] pegs){
        ArrayList<int[]> toReturn = new ArrayList<>();

        try {
            //split
            CountDownLatch awaitingRecievers = new CountDownLatch(3);
            int aThird = codes.size() / 3;

            new Thread(() -> {
                try {
                    threads[0].sendToClient(codes.subList(0, aThird), guess, pegs);
                }
                catch (InterruptedException|IOException e){
                    //shutdown();
                    e.printStackTrace();
                    System.exit(-1);
                }
                awaitingRecievers.countDown();
            }).start();
            new Thread(() -> {
                try {
                    threads[1].sendToClient(codes.subList(aThird, 2 * aThird), guess, pegs);
                }
                catch (InterruptedException|IOException e){
                    //shutdown();
                    e.printStackTrace();
                    System.exit(-1);
                }
                awaitingRecievers.countDown();
            }).start();
            new Thread(() -> {
                try {
                    threads[2].sendToClient(codes.subList(2 * aThird, codes.size()), guess, pegs);
                }
                catch (InterruptedException|IOException e){
                    //shutdown();
                    e.printStackTrace();
                    System.exit(-1);
                }
                awaitingRecievers.countDown();
            }).start();

            //wait
            awaitingRecievers.await();

            //join
            for(ServerThread st: threads){
                toReturn.addAll(st.getCodes());
            }

        } catch (InterruptedException e) {
            shutdown();
            e.printStackTrace();
            System.exit(-1);
        }

        return toReturn;
    }

    public static void shutdown() {
        for (int i = 0; i<threads.length; i++) {
            if(threads[i] != null) {
                threads[i].shutdown();
                threads[i] = null;
            }
        }
    }


    public static void keepOpen() {
        for(ServerThread st : threads)
        {
            new Thread(() -> {
                try {
                    st.sendIsShutdown(false);
                }
                catch (IOException e){
                    e.printStackTrace();
                    System.exit(-1);
                }
            }).start();
        }
    }
}
