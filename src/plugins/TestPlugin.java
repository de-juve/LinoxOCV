package plugins;

import gui.Linox;
import org.opencv.core.Mat;

public class TestPlugin extends AbstractPlugin {
    public TestPlugin() {
        title = "Test";
    }

    @Override
    public void run() {
        Linox.getInstance().getStatusBar().setProgress( title, 0, 100 );
        Mat grey = GrayscalePlugin.run( image, false );


        Linox.getInstance().getStatusBar().setProgress( title, 100, 100 );

        if ( pluginListener != null ) {
            pluginListener.addImageTab();
            pluginListener.finishPlugin();
        }
    }
}
