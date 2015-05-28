import org.opencv.core.*;
import org.opencv.imgproc.Imgproc;
import org.opencv.videoio.VideoCapture;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.BufferedReader;
import java.io.FileReader;

/**
 * Created by garethmcfarlane on 19/05/15.
 * SID 430172980
 *
 */

public class HSVTest extends JPanel implements Runnable{
    private static final int WIDTH=1280;
    private static final int HEIGHT = 720;

    private static final int DELAY = 100;
    private static final int IMG_SCALE = 2;

    private int mouseX;
    private int mouseY;

    private static int CAMERA_ID = 0;
    private static int NUM_DETECTORS = 2;



    public String type;

    private int imageCount = 0;
    private Long totalTime = 0l;
    private Font msgFont;

    private volatile boolean isRunning;
    private volatile boolean isFinished;
    private Mat snapIm = null;


    private JSlider hmin,hmax,smin,smax,vmin,vmax;

    public HSVTest() {
        setBackground(Color.white);
        initSliders();
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


        VideoCapture webcam = new VideoCapture();
        webcam.open(CAMERA_ID);
        snapIm = new Mat();
        Long duration;
        isRunning = true;
        isFinished = false;

        while (isRunning) {
            Long startTime = System.currentTimeMillis();

            webcam.read(snapIm);
            //Core.flip(snapIm, snapIm, 1);
            imageCount++;


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

        System.out.println("Program terminated");
        isFinished = true;


    }

    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D)g;
        g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION,RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        if (snapIm != null) {
            //Gets thresholded image and displays the HSV ranges.
            g2.drawImage(ImageConvert.mat2Img(getHSV(snapIm)), 0, 0, this);
            g2.setColor(Color.WHITE);
            g2.fillRect(0,0,1280,70);
            String text = "Hue Minimum: " + Integer.toString(hmin.getValue()) + ", Hue Maximum: " + Integer.toString(hmax.getValue()) + "Saturation Minimum: " + Integer.toString(smin.getValue()) + ", Saturation Maximum: " + Integer.toString(smax.getValue()) + "Value Minimum: " + Integer.toString(vmin.getValue()) + ", Value Maximum: " + Integer.toString(vmax.getValue());
            g2.drawString(text,WIDTH/5,100);
        }
    }


    private Mat getHSV(Mat snapIm) {
        //Thresholding
        Mat thresholded = snapIm.clone();
        Imgproc.cvtColor(thresholded, thresholded, Imgproc.COLOR_BGR2HSV);
        Core.inRange(thresholded, new Scalar(hmin.getValue(), smin.getValue(), vmin.getValue()), new Scalar(hmax.getValue(), hmax.getValue(), vmax.getValue()), thresholded);


        //Opening
        Imgproc.erode(thresholded,thresholded, Imgproc.getStructuringElement(Imgproc.MORPH_ELLIPSE, new Size(5, 5)));
        Imgproc.dilate(thresholded,thresholded, Imgproc.getStructuringElement(Imgproc.MORPH_ELLIPSE, new Size(5, 5)));

        //Closing
        Imgproc.dilate(thresholded,thresholded, Imgproc.getStructuringElement(Imgproc.MORPH_ELLIPSE, new Size(5, 5)));
        Imgproc.erode(thresholded,thresholded, Imgproc.getStructuringElement(Imgproc.MORPH_ELLIPSE, new Size(5, 5)));


        return thresholded;
    }

    private void initSliders() {
        //Initialising sliders.
        hmin = new JSlider(JSlider.HORIZONTAL,0,179,90);
        hmin.setBorder(BorderFactory.createTitledBorder("Hue Minimum"));
        hmax = new JSlider(JSlider.HORIZONTAL,0,180,90);
        hmax.setBorder(BorderFactory.createTitledBorder("Hue Maximum"));
        smin = new JSlider(JSlider.HORIZONTAL,0,255,120);
        smin.setBorder(BorderFactory.createTitledBorder("Saturation Minimum"));
        smax = new JSlider(JSlider.HORIZONTAL,0,255,120);
        smax.setBorder(BorderFactory.createTitledBorder("Saturation Maximum"));
        vmin= new JSlider(JSlider.HORIZONTAL,0,255,120);
        vmin.setBorder(BorderFactory.createTitledBorder("Value Minimum"));
        vmax = new JSlider(JSlider.HORIZONTAL,0,255,120);
        vmax.setBorder(BorderFactory.createTitledBorder("Value Maximum"));
        this.add(hmin);
        this.add(hmax);
        this.add(smin);
        this.add(smax);
        this.add(vmin);
        this.add(vmax);

    }


}

