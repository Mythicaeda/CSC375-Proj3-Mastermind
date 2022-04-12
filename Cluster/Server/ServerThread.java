import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class ServerThread {
    private final Socket client;
    private final List<int[]> codes;

    private ObjectOutputStream out;
    private ObjectInputStream in;

    public ServerThread(Socket socket) {
        this.client = socket;
        codes = new ArrayList<>();
        startup();
        System.out.println("Made connection");
    }

    public void startup() {
        try{
            out = new ObjectOutputStream(client.getOutputStream());
            in = new ObjectInputStream(client.getInputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void shutdown(){
        try{
            sendIsShutdown(true);
            out.close();
            in.close();
            client.close();
            System.out.println("Closed connection");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void sendIsShutdown(boolean b) throws IOException {
        out.writeBoolean(b);
        out.flush();
    }

    public void sendToClient(List<int[]> list, int[] guess, int[] pegs) throws InterruptedException, IOException {
        //send out the guess, the pegs, and the list to be parsed, in that order
        for (int k : guess) {
            out.writeInt(k);
        }
        for (int peg : pegs) {
            out.writeInt(peg);
        }
        //send out how many items we're sending in the list
        out.writeInt(list.size());
        //now send the list
        for (int[] arr : list) {
            for (int i : arr) {
                out.writeInt(i);
            }
        }
        out.flush();
        System.out.println(list.size() +" entries sent to Client");

        recieveFromClient();
    }

    public void recieveFromClient() throws IOException {
        //setup the codes and recieve
        codes.clear();
        int[] code;

        //get the size of the list, then the list itself
        int incomingListSize = in.readInt();
        System.out.println("Expecting list of size: " +incomingListSize);
        for(int duration = 0; duration<incomingListSize; duration++){
            code = new int[4];
            for(int i = 0; i<4; i++){
                code[i] = in.readInt();
            }
            //printArr(code);
            codes.add(code);
        }

        System.out.println("Data Recieved from Client");
    }

    public List<int[]> getCodes(){return codes;}
}

