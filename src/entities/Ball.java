package entities;

import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;

public class Ball {
    Point position;
    public Mat mask;
    public Mat ball;

    public Ball() {
        ball = new Mat( 3, 3, CvType.CV_8U );
        mask = new Mat( 3, 3, CvType.CV_8U );
        byte[] buff = new byte[]{ 0, 1, 0, 1, 1, 1, 0, 1, 0 };
        mask.put( 0, 0, buff );
    }

    public int response() {
        return ( int ) Core.sumElems( ball ).val[0];
    }


}
