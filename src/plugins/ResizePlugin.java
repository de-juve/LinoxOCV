package plugins;

import gui.Linox;
import gui.dialog.ParameterComboBox;
import gui.dialog.ParameterJPanel;
import gui.dialog.ParameterSlider;
import org.opencv.core.Mat;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;

public class ResizePlugin extends AbstractPlugin {
    ParameterComboBox ratioComboBox;
    ParameterSlider ratioSlider;

    public ResizePlugin() {
        title = "Resize";
    }

    @Override
    public void run() {
        Linox.getInstance().getStatusBar().setProgress( title, 0, 100 );

        showParamsPanel( "Choose params" );
        if ( exit ) {
            return;
        }
    }

    public static Mat resize( Mat image, String type, int ratio ) {
        Size newSize;
        if ( type == "Increase" ) {
            newSize = new Size( image.width() * ratio, image.height() * ratio );
        } else {
            newSize = new Size( image.width() / ratio, image.height() / ratio );
        }
        Mat result = new Mat( newSize, image.type() );
        Imgproc.resize( image, result, newSize, 0, 0, Imgproc.INTER_LANCZOS4 );
        return result;
    }


    @Override
    public void getParams( ParameterJPanel panel ) {
        String type = panel.getValueComboBox( ratioComboBox );
        int ratio = panel.getValueSlider( ratioSlider );

        result = resize( image, type, ratio );

        if ( tabs == 0 ) {
            pluginListener.addImageTab();
            tabs++;
        } else {
            pluginListener.replaceImageTab();
        }
    }

    protected void showParamsPanel( String name ) {
        ratioComboBox = new ParameterComboBox( "Type of ration", new String[]{ "Increase", "Decrease" } );
        ratioSlider = new ParameterSlider( "Ratio:", 1, 10, 1 );


        ParameterJPanel panel = new ParameterJPanel( name, this );
        panel.addParameterComboBox( ratioComboBox );
        panel.addParameterSlider( ratioSlider );

        Linox.getInstance().addParameterJPanel( panel );
    }

}
