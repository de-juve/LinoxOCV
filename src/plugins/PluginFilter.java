package plugins;


import org.opencv.core.Mat;

public interface PluginFilter extends Runnable {
    void initProcessor(Mat ip);

    Mat getResult(boolean addToStack);

    boolean exit();

    void run();

    String getTitle();

    String getErrMessage();
}
