import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.imgproc.Imgproc;

import java.awt.image.BufferedImage;

/**
 * Created by garethmcfarlane on 19/05/15.
 * SID 430172980
 *
 */

public class ImageConvert {
    public static BufferedImage mat2Img (Mat in) {

        BufferedImage out;
       // Imgproc.cvtColor(in, in, 4);
        byte [] data = new byte [1280 * 720 * (int)in.elemSize()];
        int type;
        in.get(0, 0, data);

        if (in.channels() == 1)
            type = BufferedImage.TYPE_BYTE_GRAY;
        else
            type = BufferedImage.TYPE_3BYTE_BGR;

        out = new BufferedImage(1280, 720, type);

        out.getRaster().setDataElements(0, 0, 1280, 720, data);
        return out;
    }
}
