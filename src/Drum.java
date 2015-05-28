import org.opencv.core.Point;

import java.awt.*;
/**
 * Created by garethmcfarlane on 19/05/15.
 * SID 430172980
 *
 */

public class Drum extends Thread{

    private static final int HIT_SIZE = 12;
    private static final int BEAT_LENGTH = 200;
    private static final int MAX_DELAY = 250;

    private static final Color TRANS_PALE = new Color(255,255,200,75);


    private int x;
    private int y;
    private int radius;
    private String name;
    private int xHit = -1;
    private int yHit = -1;


    private Font msgFont;
    private FontMetrics fm = null;
    private int xNamePos,yNamePos;

    private MIDIPlayer player;
    private volatile boolean isPlaying = true;
    private volatile boolean drumIsBeating = false;
    private int repeatDelay = MAX_DELAY;


    public Drum(String name, int x, int y, int width, int height, MIDIPlayer p) {
        this.x = x + width/2;
        this.y = y + height/2;
        this.radius = (width < height) ? width/2 : height/2;
        this.name = name;
        player = p;
        msgFont = new Font("SansSerif",Font.BOLD,18);
    }


    public void draw(Graphics g) {
        drawDrum(g);

        if (drumIsBeating) {
            g.setColor(Color.RED);
            g.fillOval(xHit-HIT_SIZE/2,yHit-HIT_SIZE/2,HIT_SIZE,HIT_SIZE);
        }
    }



    private void drawDrum(Graphics g) {
        if (fm == null) {
            fm = g.getFontMetrics();
            xNamePos = x - fm.stringWidth(name) / 2;
            yNamePos = y + fm.getAscent() - (fm.getAscent() + fm.getDescent()) / 2;
        }

            g.setColor(TRANS_PALE);
            g.fillOval(x-radius,y-radius,radius*2,radius*2);


            g.setColor(Color.YELLOW.brighter());
            g.setFont(msgFont);
            g.drawString(name,xNamePos,yNamePos);


    }


    public void run() {
        int beatdelay = MAX_DELAY;
        int beatlength = BEAT_LENGTH;
        if (this.name.equals("Open Hi-Hat") || this.name.equals("Hand Clap")) {
            beatdelay = MAX_DELAY/2;
            beatlength = BEAT_LENGTH/2;
        }
        while (isPlaying) {
            if (drumIsBeating) {
                player.drumOn(name);
                wait(BEAT_LENGTH);
                player.drumOff(name);
                wait(beatdelay);
            }
        }
    }

    public boolean contains(Point p) {
        double xDist = p.x - x;
        double yDist = p.y - y;
        return ((xDist*xDist + yDist*yDist) <= (radius*radius));
    }


    private void wait(int delay) {
        try {
            Thread.sleep(delay);
        } catch (Exception e) {

        }
    }


    public void stopPlaying() {
        isPlaying = false;
    }



    public boolean startBeating(int x, int y) {
        double ratio = radiusRatio(x,y);
        if(ratio > 1.0) {
            return false;
        }

        xHit = x;
        yHit = y;
        drumIsBeating = true;
        return true;
    }


    private double radiusRatio(int xval, int yval) {
        int xDist = xval - x;
        int yDist = yval - y;
        return Math.sqrt(xDist*xDist + yDist * yDist)/radius;
    }

    public void stopBeating() {
        xHit = -1;
        yHit = -1;
        drumIsBeating = false;
    }
}
