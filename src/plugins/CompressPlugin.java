package plugins;

import gui.Linox;
import gui.dialog.ParameterComboBox;
import gui.dialog.ParameterJPanel;
import gui.dialog.ParameterSlider;
import org.opencv.core.Mat;
import org.opencv.core.MatOfByte;
import org.opencv.core.MatOfInt;
import org.opencv.highgui.Highgui;

public class CompressPlugin extends AbstractPlugin {
    ParameterComboBox parameterComboBox;
    ParameterSlider parameterSlider;

    public CompressPlugin() {
        title = "Compress";
    }

    @Override
    public void run() {
        Linox.getInstance().getStatusBar().setProgress( title, 0, 100 );

        showParamsPanel( "Choose params" );
        if ( exit ) {
            return;
        }
    }

    public static Mat compress( Mat image, String type, int quality ) {
        MatOfInt params;
        if ( type == "PNG" ) {
            quality = quality * 9 / 100;
            params = new MatOfInt( Highgui.IMWRITE_PNG_COMPRESSION, quality );
        } else {
            params = new MatOfInt( Highgui.IMWRITE_JPEG_QUALITY, quality );
        }
        MatOfByte buff = new MatOfByte();
        Highgui.imencode( ".jpg", image, buff, params );
        Mat result;// = new Mat(buff.rows(), buff.cols(), buff.type());
        //result.put(0, 0, buff.toArray());
        result = Highgui.imdecode( buff, Highgui.CV_LOAD_IMAGE_COLOR );
        return result;
    }


    @Override
    public void getParams( ParameterJPanel panel ) {
        String type = panel.getValueComboBox( parameterComboBox );
        int quality = panel.getValueSlider( parameterSlider );

        result = compress( image, type, quality );

        if ( tabs == 0 ) {
            pluginListener.addImageTab();
            tabs++;
        } else {
            pluginListener.replaceImageTab();
        }
    }

    protected void showParamsPanel( String name ) {
        parameterComboBox = new ParameterComboBox( "Type of image", new String[]{ "JPEG", "PNG" } );
        parameterSlider = new ParameterSlider( "Quality:", 1, 100, 95 );


        ParameterJPanel panel = new ParameterJPanel( name, this );
        panel.addParameterComboBox( parameterComboBox );
        panel.addParameterSlider( parameterSlider );

        Linox.getInstance().addParameterJPanel( panel );
    }

}
