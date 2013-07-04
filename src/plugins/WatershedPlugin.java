package plugins;

import gui.Linox;
import org.opencv.core.Mat;

public class WatershedPlugin extends AbstractPlugin {
    public WatershedPlugin() {
        title = "Watershed";
    }

    @Override
    public Mat getResult(boolean addToStack) {
        if (result == null) {
            if (addToStack) {
                //DataCollection.INSTANCE.addtoHistory(result);
            }
        }

        return result;
    }

    @Override
    public void run() {
        Linox.getInstance().getStatusBar().setProgress("Watershed", 0, 100);

        //showParamsPanel("Choose params");
        if (exit) {
            return;
        }


        DataCollector.INSTANCE.setLaplasiantImg(result.clone());

        Linox.getInstance().getStatusBar().setProgress("Watershed", 100, 100);
    }
}
