import org.opencv.core.*;
import org.opencv.core.Point;

import java.awt.*;

/**
 * Created by garethmcfarlane on 19/05/15.
 * SID 430172980
 *
 */


public class MIDIManager {
    private final static int NUM_ROWS = 3;
    private final static int NUM_COLS = 3;

    private MIDIPlayer player;
    private Drum[] drums;

    private int numSticks;
    private Drum[] currDrums;



    public MIDIManager(int width, int height, int num) {
        int colwidth = width/NUM_COLS;
        int rowwidth = height/NUM_ROWS;
        numSticks = num;


        player = new MIDIPlayer();

        drums = new Drum[NUM_ROWS*NUM_COLS];
        int xCoord = 0;
        int yCoord = 0;
        int i = 0;

        for (int row = 0; row < NUM_ROWS; row++) {
            xCoord = 0;
            for (int cols = 0; cols < NUM_COLS; cols++) {
                drums[i] = new Drum(MIDIPlayer.getInstrumentName(i),xCoord,yCoord,colwidth,rowwidth,player);
                drums[i].start();
                xCoord += colwidth;
                i++;
            }
            yCoord += rowwidth;
        }

        currDrums = new Drum[numSticks];
        for (int j = 0; j <numSticks; j++) {
            currDrums[j] = null;
        }
    }


    public void draw(Graphics g) {
        for (Drum drum: drums) {
            drum.draw(g);
        }
    }

    public void stopPlaying() {
        for (Drum drum : drums) {
            drum.stopPlaying();
        }
        player.close();
    }



    public void startBeating(int sIDx,int x, int y) {
        if (sIDx < 0 || sIDx >= numSticks) {
            //error
            return;
        }

        if (currDrums[sIDx] != null && currDrums[sIDx].contains(new Point(x,y))) {
            currDrums[sIDx].startBeating(x,y);
        } else {
            if (currDrums[sIDx] != null) {
                currDrums[sIDx].stopBeating();
                currDrums[sIDx] = null;
            }
            for (Drum drum : drums) {
                if (drum.startBeating(x,y)) {
                    currDrums[sIDx] = drum;
                    break;
                }
            }
        }
    }




    public void stopBeating(int sIDx) {
        if (sIDx < 0 || (sIDx >= numSticks)) {
            return;
        }

        if (currDrums[sIDx] != null) {
            currDrums[sIDx].stopBeating();
            currDrums[sIDx] = null;
        }
    }
}