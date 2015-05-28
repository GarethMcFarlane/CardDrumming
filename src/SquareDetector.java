/**
 * Created by garethmcfarlane on 19/05/15.
 * SID 430172980
 *
 */
import org.opencv.core.*;
import org.opencv.core.Point;
import org.opencv.imgproc.Imgproc;




import java.awt.*;


import java.awt.image.BufferedImage;
import java.util.ArrayList;

public class SquareDetector {

    public static final int NUM_POINTS = 4;

    public static int LowH = 0;
    public static int HighH = 179;


    public static int LowS = 0;
    public static int HighS = 255;

    public static  int LowV = 0;
    public static int HighV = 255;


    private int hueLower,hueUpper,satLower,satUpper,valLower,valUpper;

    private Mat hsvImage;
    private Mat imgThreshed;
    private Mat thresh;
    private Rect largebox;


    private boolean foundBox = false;
    private int[] xPoints, yPoints;
    private Point center;
    private int angle;



    public SquareDetector(int width, int height) {
        hsvImage = new Mat();
        imgThreshed = new Mat();
        xPoints = new int[NUM_POINTS];
        yPoints = new int[NUM_POINTS];
        largebox = new Rect();

        center = new Point();
        angle = 0;

        hueLower = LowH;
        hueUpper = HighH;
        satLower = LowS;
        satUpper = HighS;
        valLower = LowV;
        valUpper = HighV;
    }

    public boolean findRect(Mat image) {

        //Thresholding
        Imgproc.cvtColor(image, hsvImage, Imgproc.COLOR_BGR2HSV);
        Core.inRange(hsvImage, new Scalar(hueLower, satLower, valLower), new Scalar(valUpper, satUpper, valUpper), imgThreshed);

        //Opening
        Imgproc.erode(imgThreshed,imgThreshed, Imgproc.getStructuringElement(Imgproc.MORPH_ELLIPSE, new Size(5, 5)));
        Imgproc.dilate(imgThreshed,imgThreshed, Imgproc.getStructuringElement(Imgproc.MORPH_ELLIPSE, new Size(5, 5)));

        //Closing
        Imgproc.dilate(imgThreshed,imgThreshed, Imgproc.getStructuringElement(Imgproc.MORPH_ELLIPSE, new Size(5, 5)));
        Imgproc.erode(imgThreshed, imgThreshed, Imgproc.getStructuringElement(Imgproc.MORPH_ELLIPSE, new Size(5, 5)));

        Rect maxBox = largestBox(imgThreshed);


        if (maxBox != null) {
            foundBox = true;
            extractBoxInfo(maxBox);
        } else {
            foundBox = false;
        }
        return foundBox;
    }

    //Uses contouring to find the largest box.
    public Rect largestBox(Mat image) {


        ArrayList<MatOfPoint> contours = new ArrayList<MatOfPoint>();
        MatOfPoint2f approxCurve = new MatOfPoint2f();

        Imgproc.findContours(image, contours, new Mat(), Imgproc.RETR_LIST, Imgproc.CHAIN_APPROX_SIMPLE);

        Rect largestbox = new Rect();

        //Iterate through every found rect and find the biggest one.
        for (int i = 0; i < contours.size(); ++i) {
            MatOfPoint2f contour2f = new MatOfPoint2f(contours.get(i).toArray());
            double aproxDistance = Imgproc.arcLength(contour2f,true)*0.02;
            Imgproc.approxPolyDP(contour2f,approxCurve,aproxDistance,true);
            MatOfPoint points = new MatOfPoint(approxCurve.toArray());
            Rect rect = Imgproc.boundingRect(points);


            if (rect.width > largestbox.width && rect.height > largestbox.height) {
                largestbox = rect;
            }

        }
        return largestbox;
    }




 private void extractBoxInfo(Rect box) {



    center = new Point(box.x + box.width/2,box.y + box.height/2);
    xPoints[0] = box.x;
    xPoints[1] = box.x + box.width;
     xPoints[3] = box.x;
     xPoints[2] = box.x + box.width;

     yPoints[0] = box.y;
     yPoints[1] = box.y;
     yPoints[3] = box.y + box.height;
     yPoints[2] = box.y + box.height;


     this.largebox = box;

 }


    public Polygon getBoundedBox() {
        return ((foundBox) ? new Polygon(xPoints,yPoints,NUM_POINTS) : null);
    }


    public Point getCenter() {
        return ((foundBox) ? center : null);
    }


    public BufferedImage getHSVImage() {
        return ImageConvert.mat2Img(hsvImage);
    }

    public BufferedImage getThresholdImag() {
        return ImageConvert.mat2Img(imgThreshed);
    }



    public void setHueRange(int lower, int upper) {
        hueLower = lower;
        hueUpper = upper;
    }

    public void setSatRange(int lower, int upper) {
        satLower = lower;
        satUpper = upper;
    }

    public void setValRange(int lower, int upper) {
        valLower = lower;
        valUpper = upper;
    }
}
