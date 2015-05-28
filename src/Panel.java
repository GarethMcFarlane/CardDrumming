import org.opencv.core.Mat;
import org.opencv.core.*;
import org.opencv.core.Point;
import org.opencv.imgproc.Imgproc;
import org.opencv.videoio.VideoCapture;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.BufferedReader;
import java.io.FileReader;


/**
 * Created by garethmcfarlane on 19/05/15.
 * SID 430172980
 *
 */
public class Panel extends JPanel implements Runnable {
    private static final int WIDTH=1280;
    private static final int HEIGHT = 720;

    private static final int DELAY = 100;
    private static final int IMG_SCALE = 2;

    private int mouseX;
    private int mouseY;

    private static int CAMERA_ID = 0;
    private static int NUM_DETECTORS = 2;


    private static final int HUE_LOWER = 0;
    private static final int HUE_UPPER = 179;

    private static final int SAT_LOWER = 0;
    private static final int SAT_UPPER = 255;

    private static final int VAL_LOWER = 0;
    private static final int VAL_UPPER = 255;


    private int imageCount = 0;
    private Long totalTime = 0l;
    private Font msgFont;

    private volatile boolean isRunning;
    private volatile boolean isFinished;
    private Mat snapIm = null;


    private SquareDetector[] detectors;
    private boolean haveDetectors = false;

    private MIDIManager drummer;

    private JSlider hmin,hmax,smin,smax,vmin,vmax;

    public Panel() {
        setBackground(Color.white);
        //if (type.equals("calibrate")) {
           // initSliders();
        //}
        super.addMouseListener(new MouseListener() {
            @Override
            public void mouseClicked(MouseEvent e) {
                mouseX = e.getX();
                mouseY = e.getY();
            }

            @Override
            public void mousePressed(MouseEvent e) {

            }

            @Override
            public void mouseReleased(MouseEvent e) {

            }

            @Override
            public void mouseEntered(MouseEvent e) {

            }

            @Override
            public void mouseExited(MouseEvent e) {

            }
        });
        msgFont = new Font("SansSerif",Font.BOLD,18);

        new Thread(this).start();
    }

    public Dimension getSize() {
        return new Dimension(WIDTH,HEIGHT);
    }


    public void run() {
        drummer = new MIDIManager(WIDTH,HEIGHT,NUM_DETECTORS);

        VideoCapture webcam = new VideoCapture();
        webcam.open(CAMERA_ID);
        snapIm = new Mat();
        Long duration;
        isRunning = true;
        isFinished = false;
        initDetectors(WIDTH,HEIGHT);

        while (isRunning) {
            Long startTime = System.currentTimeMillis();

            webcam.read(snapIm);
            //Core.flip(snapIm, snapIm, 1);
            imageCount++;
            updateDetectors(snapIm);
            repaint();

            duration = System.currentTimeMillis() - startTime;
            totalTime -= duration;
            if(duration < DELAY) {
                try {
                    Thread.sleep(DELAY-duration);
                }
                catch (Exception e) {}
            }
        }

        drummer.stopPlaying();
        System.out.println("Program terminated");
        isFinished = true;


    }

    private void initDetectors(int width, int height) {
        detectors = new SquareDetector[NUM_DETECTORS];
        detectors[1] = new SquareDetector(width,height);
        detectors[0] = new SquareDetector(width,height);
        readHSV("../HSV.txt", detectors[1]);
        readHSV("../HSV2.txt",detectors[0]);
        haveDetectors = true;
    }


    public void readHSV(String file, SquareDetector detector) {
        try {
            BufferedReader in = new BufferedReader(new FileReader(file));
            int[] vals = extractInts(in.readLine(),HUE_LOWER, HUE_UPPER);
            detector.setHueRange(vals[0],vals[1]);

            vals = extractInts(in.readLine(),SAT_LOWER,SAT_UPPER);
            detector.setSatRange(vals[0],vals[1]);

            vals = extractInts(in.readLine(),VAL_LOWER,VAL_UPPER);
            detector.setValRange(vals[0],vals[1]);

            in.close();
            System.out.println("Read HSV ranges from " + file);
        }
        catch(Exception e) {
            System.out.println("Could not read from " + file);
        }
    }



    private int[] extractInts(String line, int lower, int upper) {
        int[] vals = new int[2];
        vals[0] = lower;
        vals[1] = upper;

        String[] toks = line.split("\\s+");
        try {
            vals[0] = Integer.parseInt(toks[1]);
            vals[1] = Integer.parseInt(toks[2]);
        }
        catch(Exception e) {
            System.out.println("Could not read line");
        }
        return vals;
    }

    //Find new squares
    private void updateDetectors(Mat image) {

        Point center;
        for (int i = 0; i < NUM_DETECTORS; ++i) {
            if (detectors[i].findRect(image)) {
                center = detectors[i].getCenter();
                drummer.startBeating(i,(int)center.x,(int)center.y);
            } else {
                drummer.stopBeating(i);
            }
        }
    }


    //Paint Mat, boxes, etc.
    public void paintComponent(Graphics g) {

        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D)g;
        g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION,RenderingHints.VALUE_INTERPOLATION_BILINEAR);

        if (snapIm != null) {
                    g2.drawImage(ImageConvert.mat2Img(snapIm), 0, 0, this);
        }


        if (drummer != null) {
            drummer.draw(g2);
        }

        if (haveDetectors) {
            drawBoxes(g2);
        }

        writeStats(g2);
    }


    public void drawBoxes(Graphics2D g2) {
        g2.setPaint(Color.YELLOW);
        g2.setStroke(new BasicStroke(4));

        Polygon box;
        for (SquareDetector detector : detectors) {
            box = detector.getBoundedBox();
            if (box != null) {
                g2.drawPolygon(box);
            }
        }
    }


    private void writeStats(Graphics2D g2) {
        g2.setFont(msgFont);
        g2.setColor(Color.BLUE);

        if (imageCount > 0) {
            String statsMsg = String.format("Snap Average Time: %.1f ms", ((double)totalTime/imageCount));
            g2.drawString(statsMsg,5,HEIGHT-10);
        } else {
            g2.drawString("Loading image",5,HEIGHT-10);
        }
    }


    public void closeDown() {
        isRunning = false;
        while (!isFinished) {
            try {
                Thread.sleep(DELAY);
            }
            catch(Exception e) {}
        }
    }




}
