import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;


public class Main extends JPanel{
    private static Main m;
    private final Board board;
    private static Timer t;


    public Dimension getPreferredSize() {
        return new Dimension(550,900);
    }

    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        board.draw(g);
    }

    Main(){
        board = new Board();
    }

    public static void main(String[] args){
        m = new Main();
        SwingUtilities.invokeLater(() -> createAndShowGUI(m));

        Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {
            public void run() {
                Server.shutdown();
            }
        }, "Shutdown-thread"));

        Server.main();

        new Timer(17, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                m.repaint();
            }
        }).start();

        //might need to slow this down when moved onto the cluster step
        t = new Timer(3000, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                m.board.advance();
                if (m.board.getFinished() || m.board.getCurrentGuess() >= 10) {
                    Main.t.stop();
                    Server.shutdown();
                } else {
                    Server.keepOpen();
                }
            }
        });
        t.start();
    }

    private static void createAndShowGUI(Main m) {
        JFrame f = new JFrame("Mastermind");
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        f.setSize(250,250);

        f.add(m);
        f.pack();
        f.setVisible(true);
    }

}
