package test;

import entities.DataCollector;
import entities.Mask;
import entities.Point;
import gui.Linox;
import gui.menu.LinoxMenuStore;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.highgui.Highgui;
import org.opencv.imgproc.Imgproc;
import plugins.WatershedPlugin;
import plugins.morphology.MorphologyPlugin;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by dm on 02.02.14.
 */
public class T1 {
    private HashMap<Integer, Point> wpoints;

    public T1() {
        String dir = System.getProperty("user.dir") + "/resource/test_0";
        File[] files = new File(dir).listFiles();
        Mat image;

        //int mask_w = 6;
        //int road_w = 2;
        int mask_w = 9;
        int mask_h = 9;
        int road_w = 3;
        int area_size = 3;

        for (File file : files) {

            if (!file.isFile() || !file.getName().contains(".png") || file.getName().contains("0.png") || !file.getName().contains("ekb3.png")) {
                continue;
            }

            String name = file.getName().substring(0, file.getName().indexOf("."));
            System.out.println( name );

            image = Highgui.imread(file.getAbsolutePath(), Highgui.CV_LOAD_IMAGE_COLOR);
            Mat result = image.clone();
            Mat wresult = image.clone();
            if ( ( image.channels() == 3 || image.channels() == 4 ) && image.type() != CvType.CV_8UC1 ) {
                Imgproc.cvtColor( image, image, Imgproc.COLOR_BGR2Lab );
            }

            ArrayList<Mat> channels = new ArrayList<Mat>();
            Core.split( image, channels );
            image = channels.get( 0 );
            (Linox.getInstance().getImageStore()).addImageTab(name, image);

            MorphologyPlugin morphology = new MorphologyPlugin();
            WatershedPlugin watershed = new WatershedPlugin();
            wpoints = new HashMap<>();

            Mask mask = new Mask( mask_w, mask_h, road_w, image.type(), 10 );
            L:
            for ( int y = 0; y < image.rows(); y += mask_w / 2 ) {
                if ( y + mask_w > image.rows() ) {
                    break;
                }
                for (int x = 0; x < image.cols(); x += mask_h / 2) {
                    if (x + mask_h > image.cols()) {
                        break;
                    }
                    mask.fill(new Point(x, y), image);
                    for (Mask.MaskType type : Mask.MaskType.values()) {
                        if ( mask.analyze( type ) ) {
                            drawRect( mask_w, mask_h, result, y, x );

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
            ( Linox.getInstance().getImageStore() ).addImageTab( name, result );
            ( Linox.getInstance().getImageStore() ).addImageTab( name, wresult );
            ( ( LinoxMenuStore ) Linox.getInstance().getMenuStore() ).setEnableEditToolsItems( true );
            break;


//            MorphologyPlugin mPlugin = new MorphologyPlugin();
//            mPlugin.initImage(image.clone());
//            mPlugin.run("Closing", areaSize);
//            Mat mImage = mPlugin.getResult(false);
//            WatershedPlugin wPlugin = new WatershedPlugin();
//            wPlugin.initImage(mImage);
//            wPlugin.run();
//            Mat wImage = wPlugin.getResult(false);
//
//            Highgui.imwrite(dir + "/" + name + "_" + areaSize + ".png", wImage);
//
//            break;
        }
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
