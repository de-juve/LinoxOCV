package test;

import entities.Mask;
import entities.Point;
import org.opencv.core.Mat;
import org.opencv.highgui.Highgui;

import java.io.File;

/**
 * Created by dm on 02.02.14.
 */
public class T1 {
    public T1() {
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

            int mask_w = 9;
            int road_w = 3;
            Mask mask = new Mask(mask_w, road_w);
            for (int y = 0; y < image.rows(); y += mask_w / 2) {
                for (int x = 0; x < image.cols(); x += mask_w / 2) {
                    mask.fill(new Point(x, y), image);
                    for (Mask.MaskType type : Mask.MaskType.values()) {
                        mask.analyze(type);
                    }
                    break;
                }
                break;
            }
            break;


//            MorphologyPlugin mPlugin = new MorphologyPlugin();
//            mPlugin.initImage(image.clone());
//            mPlugin.run("Closing", areaSize);
//            Mat mImage = mPlugin.getResult(false);
//            WatershedPlugin wPlugin = new WatershedPlugin();
//            wPlugin.initImage(mImage);
//            wPlugin.run();
//            Mat wImage = wPlugin.getResult(false);
//
//            Highgui.imwrite(dir + "/" + name + "_" + areaSize + ".png", wImage);
//
//            break;
        }
    }
}
