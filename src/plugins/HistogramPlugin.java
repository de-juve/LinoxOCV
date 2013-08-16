package plugins;

import gui.Linox;
import org.opencv.core.*;
import org.opencv.imgproc.Imgproc;

import java.util.ArrayList;

public class HistogramPlugin extends AbstractPlugin {
    private ArrayList<Mat> images;
    private MatOfInt channels;
    private Mat mask;
    private Mat hist;
    private MatOfInt histSize;
    private MatOfFloat ranges;

    public Core.MinMaxLocResult minMaxLocResult;
    public MatOfDouble mean, stdDev;

    public HistogramPlugin() {
        title = "Histogram";
    }

    @Override
    public void run() {
        Linox.getInstance().getStatusBar().setProgress( title, 0, 100 );

      /*  histSize = new MatOfInt(256, 256);
        ranges = new MatOfFloat(0.0f, 255.0f,
                0.0f, 255.0f);
        channels = new MatOfInt(0, 1);*/

        histSize = new MatOfInt( 256 );
        ranges = new MatOfFloat( 0.0f, 255.0f );
        channels = new MatOfInt( 0 );

        mask = new Mat();
        hist = new Mat();

        mean = new MatOfDouble( 0 );
        stdDev = new MatOfDouble( 0 );


        computeHistogram( image );

        int hist_w = 256;
        int hist_h = 256;
        int bin_w = ( int ) Math.round( ( double ) hist_w / 256 );

        Mat histImage = new Mat( hist_h, hist_w, CvType.CV_8UC3, new Scalar( 0, 0, 0 ) );
        Core.normalize( hist, hist, 0, histImage.rows(), Core.NORM_MINMAX, -1, new Mat() );

        minMaxLocResult = Core.minMaxLoc( hist );
        double max_val = minMaxLocResult.maxVal;

        Core.meanStdDev( hist, mean, stdDev );
        System.out.println( mean.get( 0, 0 )[0] );
        System.out.println( stdDev.get( 0, 0 )[0] );

        for ( int i = 1; i < 256; i++ ) {
            float binVal = ( float ) hist.get( i, 0 )[0];

            int height = ( int ) Math.round( binVal * hist_h / max_val );
            Core.line
                    ( histImage
                            , new Point( i, hist_h - height ), new Point( i, hist_h )
                            , new Scalar( 255, 255, 255 ) );
          /*  Core.line( histImage, new Point( bin_w*(i-1), hist_h - Math.round(hist.get(i-1, 0)[0]) ) ,
                    new Point( bin_w*(i), hist_h - Math.round(hist.get(i, 0)[0]) ),
                    new Scalar( 255, 255, 255)*//*, 2, 8, 0*//*  );*/
        }
        result = histImage;

        Linox.getInstance().getStatusBar().setProgress( title, 100, 100 );

        if ( pluginListener != null ) {
            pluginListener.addImageTab();
            pluginListener.finishPlugin();
        }
    }

    public void computeHistogram( Mat image ) {
        images = new ArrayList<>();
        images.add( image );
        Imgproc.calcHist( images, channels, mask, hist, histSize, ranges, false );
    }

}
