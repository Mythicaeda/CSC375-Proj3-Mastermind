import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;


public class Main extends JPanel{
    private static boolean visible = false;
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
    public static Board getBoard() { return m.board; }

    public static void main(String[] args){
        m = new Main();
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                createAndShowGUI(m);
            }
        });

        while(!visible){Thread.onSpinWait();}

        new Timer(17, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                m.repaint();
            }
        }).start();

        //might need to slow this down when moved onto the cluster step
        t = new Timer(1000, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                m.board.advance();
                if(m.board.getFinished()||m.board.getCurrentGuess()>=10){Main.t.stop();}
            }
        });
        t.start();
    }

    private static void createAndShowGUI(Main m) {
        //System.out.println("Created GUI on EDT? "+SwingUtilities.isEventDispatchThread());
        JFrame f = new JFrame("Mastermind");
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        f.setSize(250,250);

        f.add(m);
        f.pack();
        f.setVisible(true);
        //System.out.println("GUI's open");
        visible = true;
    }

