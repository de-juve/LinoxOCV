package plugins;

import gui.Linox;
import gui.dialog.ParameterJPanel;
import gui.dialog.ParameterSlider;
import org.opencv.core.Mat;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;

public class ResizePlugin extends AbstractPlugin {
    int tabs = 0;
    ParameterSlider widthSlider, heightSlider;

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

    public static Mat resize( Mat image, Size size ) {
        Mat result = new Mat();
        Imgproc.resize( image, result, new Size( 500, 500 ), 0, 0, Imgproc.INTER_LANCZOS4 );
        return result;
    }


    @Override
    public void getParams( ParameterJPanel panel ) {
        int width = panel.getValueSlider( widthSlider );
        int height = panel.getValueSlider( heightSlider );
        Size size = new Size( width, height );

        resize( image, size );

        if ( tabs == 0 ) {
            pluginListener.addImageTab();
            tabs++;
        } else {
            pluginListener.replaceImageTab();
        }
    }

    protected void showParamsPanel( String name ) {
        widthSlider = new ParameterSlider( "Width:", 1, image.width() * 10, image.width() );
        heightSlider = new ParameterSlider( "Height:", 1, image.height() * 10, image.height() );

        ParameterJPanel panel = new ParameterJPanel( name, this );
        panel.addParameterSlider( widthSlider );
        panel.addParameterSlider( heightSlider );

        Linox.getInstance().addParameterJPanel( panel );
    }

}
