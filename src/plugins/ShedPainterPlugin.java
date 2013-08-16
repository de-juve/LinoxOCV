package plugins;

import entities.DataCollector;
import entities.ShedCollector;
import gui.Linox;
import org.opencv.core.Mat;

import java.awt.*;

public class ShedPainterPlugin extends AbstractPlugin {
    public ShedPainterPlugin() {
        title = "Shed painter";
    }

    @Override
    public void run() {
        if ( ShedCollector.INSTANCE.size() == 0 ) {
            exit = true;
            setErrMessage( "Empty sheds" );
            pluginListener.stopPlugin();
            return;
        }

        Linox.getInstance().getStatusBar().setProgress( title, 0, 100 );

        result = run( image );

        Linox.getInstance().getStatusBar().setProgress( title, 100, 100 );

        if ( pluginListener != null ) {
            pluginListener.addImageTab();
            pluginListener.finishPlugin();
        }
    }

    public static Mat run( Mat image ) {
        Mat result = new Mat( image.rows(), image.cols(), image.type() );//CvType.CV_8U);

        Color[] colors = new Color[image.width() * image.height()];
        int[] labels = DataCollector.INSTANCE.getShedLabels();
        for ( int i = 0; i < labels.length; i++ ) {
            colors[i] = ShedCollector.INSTANCE.getShed( labels[i] ).getColor();
        }

        byte[] buff = new byte[( int ) image.total() * image.channels()];

        int j = 0;
        for ( int i = 0; i < colors.length; i++ ) {
            buff[j] = ( byte ) colors[i].getBlue();
            j++;
            buff[j] = ( byte ) colors[i].getGreen();
            j++;
            buff[j] = ( byte ) colors[i].getRed();
            j++;
        }
        result.put( 0, 0, buff );

        return result;
    }
}
