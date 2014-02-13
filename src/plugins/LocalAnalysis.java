package plugins;


import entities.DataCollector;
import entities.Mask;
import entities.Point;
import gui.Linox;
import gui.dialog.ParameterJPanel;
import gui.dialog.ParameterSlider;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.imgproc.Imgproc;
import plugins.morphology.MorphologyPlugin;

import java.util.ArrayList;
import java.util.HashMap;

public class LocalAnalysis extends AbstractPlugin {
    private int area_size, mask_w, mask_h, road_w, threshold;
    private HashMap<Integer, Point> wpoints;
    Mat mresult;
    Mat wresult;

    ParameterSlider maskWidth = new ParameterSlider( "Mask width:", 3, 30, 9 );
    ParameterSlider maskHeight = new ParameterSlider( "Mask height:", 3, 30, 9 );
    ParameterSlider roadWidth = new ParameterSlider( "Road width:", 1, 30, 3 );
    ParameterSlider thresholdSlider = new ParameterSlider( "Threshold for different between road and background:", 10, 60, 20 );
    ParameterSlider kernelSize = new ParameterSlider( "Size of area closing:", 1, 30, 5 );

    public LocalAnalysis() {
        title = "Local Analysis";
    }

    @Override
    public void run() {
        Linox.getInstance().getStatusBar().setProgress( title, 0, 100 );

        mresult = image.clone();
        wresult = image.clone();
        if ( ( image.channels() == 3 || image.channels() == 4 ) && image.type() != CvType.CV_8UC1 ) {
            Imgproc.cvtColor( image, image, Imgproc.COLOR_BGR2Lab );
        }

        ArrayList<Mat> channels = new ArrayList<Mat>();
        Core.split( image, channels );
        image = channels.get( 0 );
        pluginListener.addImageTab( "grey", image );

        showParamsPanel( "Choose params" );
        if ( exit ) {
            return;
        }
    }

    @Override
    public void getParams( ParameterJPanel panel ) {
        mask_w = panel.getValueSlider( maskWidth );
        mask_h = panel.getValueSlider( maskHeight );
        road_w = panel.getValueSlider( roadWidth );
        area_size = panel.getValueSlider( kernelSize );
        threshold = panel.getValueSlider( thresholdSlider );

        analyse();

        if ( tabs == 0 ) {
            pluginListener.addImageTab();
            tabs++;
        } else {
            pluginListener.replaceImageTab();
        }
    }

    protected void showParamsPanel( String name ) {
        ParameterJPanel panel = new ParameterJPanel( name, this );
        panel.addParameterSlider( maskWidth );
        panel.addParameterSlider( maskHeight );
        panel.addParameterSlider( roadWidth );
        panel.addParameterSlider( kernelSize );
        panel.addParameterSlider( thresholdSlider );

        Linox.getInstance().addParameterJPanel( panel );
    }

    private void analyse() {
        MorphologyPlugin morphology = new MorphologyPlugin();
        WatershedPlugin watershed = new WatershedPlugin();
        wpoints = new HashMap<>();

        Mask mask = new Mask( mask_w, mask_h, road_w, image.type(), threshold );
        L:
        for ( int y = 0; y < image.rows(); y += mask_w / 2 ) {
            if ( y + mask_w > image.rows() ) {
                break;
            }
            for ( int x = 0; x < image.cols(); x += mask_h / 2 ) {
                if ( x + mask_h > image.cols() ) {
                    break;
                }
                mask.fill( new Point( x, y ), image );
                for ( Mask.MaskType type : Mask.MaskType.values() ) {
                    if ( mask.analyze( type ) ) {
                        drawRect( mask_w, mask_h, mresult, y, x );

                        morphology.initImage( mask.getMask() );
                        morphology.run( "Closing", area_size );
                        Mat mImage = morphology.getResult( false );

                        watershed.initImage( mImage );
                        watershed.run();
                        Mat wImage = watershed.getResult( false );

//                            (Linox.getInstance().getImageStore()).addImageTab( type.name(), mask.getMask() );
//                            (Linox.getInstance().getImageStore()).addImageTab( type.name()+"m", mImage );
//                            (Linox.getInstance().getImageStore()).addImageTab( type.name()+"w", wImage );
                        // System.out.println(DataCollector.INSTANCE.getWatershedPoints());
                        addWPoints( x, y, DataCollector.INSTANCE.getWatershedPoints(), image.cols() );
                        // wpoints.putAll( DataCollector.INSTANCE.getWatershedPoints() );
                        //  break L;
                    }
                }
            }
        }
        drawWatershed( wresult );
        pluginListener.addImageTab( "mresult", mresult );
        pluginListener.addImageTab( "wresult", wresult );
        result = wresult.clone();
    }

    private void addWPoints( int x, int y, HashMap<Integer, Point> watershedPoints, int cols ) {
        for ( Point point : watershedPoints.values() ) {
            int col = x + point.x;
            int row = y + point.y;
            int id = col + row * cols;
            wpoints.put( id, new Point( col, row ) );
        }
    }


    private void drawRect( int mask_w, int mask_h, Mat result, int y, int x ) {
        for ( int j = y; j < y + mask_h; j++ ) {
            for ( int i = x; i < x + mask_w; i++ ) {
                if ( j >= y + 1 && j < y + mask_h - 1 ) {
                    result.put( j, x, 0, 0, 255 );
                    result.put( j, x + mask_w - 1, 0, 0, 255 );
                } else {
                    result.put( j, i, 0, 0, 255 );
                }
            }
        }
    }

    private void drawWatershed( Mat image ) {
        for ( Point point : wpoints.values() ) {
            image.put( point.y, point.x, 0, 0, 255 );
        }
    }
}
