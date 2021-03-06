package test;

import org.opencv.core.Mat;
import org.opencv.highgui.Highgui;
import plugins.WatershedPlugin;
import plugins.morphology.MorphologyPlugin;

import java.io.File;

/**
 * Created by it on 28.01.14.
 */
public class AutoWatershed {
    public AutoWatershed() {
        String dir = System.getProperty("user.dir") + "/resource/test_0";
        File[] files = new File(dir).listFiles();
        Mat image;

        for (File file : files) {

            if (!file.isFile() || !file.getName().contains(".png") || file.getName().contains("0.png")) {
                continue;
            }

            String name = file.getName().substring(0, file.getName().indexOf("."));
            int areaSize = 1000;

            image = Highgui.imread(file.getAbsolutePath(), Highgui.CV_LOAD_IMAGE_COLOR);
            MorphologyPlugin mPlugin = new MorphologyPlugin();
            mPlugin.initImage(image.clone());
            mPlugin.run("Closing", areaSize);
            Mat mImage = mPlugin.getResult(false);
            WatershedPlugin wPlugin = new WatershedPlugin();
            wPlugin.initImage(mImage);
            wPlugin.run();
            Mat wImage = wPlugin.getResult(false);

            Highgui.imwrite(dir + "/" + name + "_" + areaSize + ".png", wImage);

        }

    }
}
