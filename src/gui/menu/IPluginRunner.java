package gui.menu;

import org.opencv.core.Mat;
import plugins.IPluginFilter;

public interface IPluginRunner extends Runnable {
    void addImageTab();

    void addImageTab( String title, Mat image );

    void replaceImageTab();

    void replaceImageTab( String title, Mat image );

    void setPlugin( IPluginFilter plugin );

    void stopPlugin();

    void finishPlugin();

    void done();
}
