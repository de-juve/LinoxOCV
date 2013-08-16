package plugins;


import gui.dialog.ParameterJPanel;
import gui.menu.PluginRunner;
import org.opencv.core.Mat;

public interface IPluginFilter extends Runnable {
    void initImage( Mat ip );

    Mat getResult( boolean addToStack );

    boolean exit();

    void run();

    String getTitle();

    String getErrMessage();

    void cancel();

    void getParams( ParameterJPanel parameterJPanel );

    void addRunListener( PluginRunner runner );

    void finish();
}
