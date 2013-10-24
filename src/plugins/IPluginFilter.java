package plugins;


import gui.dialog.ParameterJPanel;
import gui.menu.IPluginRunner;
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

    void addRunListener( IPluginRunner runner );

    void finish();
}
