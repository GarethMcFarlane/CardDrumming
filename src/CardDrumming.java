

import org.opencv.core.Core;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Hashtable;

/**
 * Created by garethmcfarlane on 19/05/15.
 * SID 430172980
 *
 */




public class CardDrumming extends JFrame

{
    static {
     System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
    }

    private Panel drumPanel;
    private HSVTest HSVPanel;


    public CardDrumming(String mode) {
        super("Card Drumming");

        Container c = getContentPane();
        c.setLayout(new BorderLayout());




        //Check which mode is selected.
        if (mode.equals("calibrate")) {
            HSVPanel = new HSVTest();
            c.add(HSVPanel,BorderLayout.CENTER);
            HSVPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
            HSVPanel.setName("Calibration");
        } else {
            drumPanel = new Panel();
            c.add(drumPanel, BorderLayout.CENTER);
            drumPanel.setName("Card Drumming");
            drumPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
        }


        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                if (mode.equals("calibrate")) {

                } else {
                    drumPanel.closeDown();
                }
                System.exit(0);
            }
        });

        setResizable(true);
        c.setBounds(100, 100, 1280, 720);
        setBounds(100, 100, 1280, 720);



        pack();
        setLocationRelativeTo(null);
        setVisible(true);
        setExtendedState(Frame.MAXIMIZED_BOTH);
    }

    public static void main(String args[]) {
            new CardDrumming(args[0]);
    }




}
