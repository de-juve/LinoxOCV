package test;

import entities.Mask;
import entities.Point;
import gui.Linox;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.highgui.Highgui;
import org.opencv.imgproc.Imgproc;

import java.io.File;
import java.util.ArrayList;

/**
 * Created by dm on 02.02.14.
 */
public class T1 {
    public T1() {
        String dir = System.getProperty("user.dir") + "/resource/test_0";
        File[] files = new File(dir).listFiles();
        Mat image;

        for (File file : files) {

            if (!file.isFile() || !file.getName().contains(".png") || file.getName().contains("0.png") || !file.getName().contains("ekb3.png")) {
                continue;
            }

            String name = file.getName().substring(0, file.getName().indexOf("."));
            System.out.println( name );
            int areaSize = 1000;

            image = Highgui.imread(file.getAbsolutePath(), Highgui.CV_LOAD_IMAGE_COLOR);
            Mat result = image.clone();
            if ( ( image.channels() == 3 || image.channels() == 4 ) && image.type() != CvType.CV_8UC1 ) {
                Imgproc.cvtColor( image, image, Imgproc.COLOR_BGR2Lab );
            }

            ArrayList<Mat> channels = new ArrayList<Mat>();
            Core.split( image, channels );
            image = channels.get( 0 );
            (Linox.getInstance().getImageStore()).addImageTab(name, image);

            //int mask_w = 6;
            //int road_w = 2;

            int mask_w = 9;
            int mask_h = 9;
            int road_w = 3;

            Mask mask = new Mask(mask_w, mask_h, road_w, image.type());
            for (int y = 0; y < image.rows(); y += mask_w / 2) {
                if ( y + mask_w > image.rows() ) {
                    break;
                }
                for (int x = 0; x < image.cols(); x += mask_h / 2) {
                    if (x + mask_h > image.cols()) {
                        break;
                    }
                    mask.fill(new Point(x, y), image);
                    for (Mask.MaskType type : Mask.MaskType.values()) {
                        if (mask.partition(type)) {
                            for (int j = y; j < y + mask_h; j++) {
                                for (int i = x; i < x + mask_w; i++) {
                                    if (j >= y + 1 && j < y + mask_h - 1) {
                                        result.put(j, x, 0, 0, 255);
                                        result.put(j, x + mask_w - 1, 0, 0, 255);

                                    } else {
                                        result.put(j, i, 0, 0, 255);
                                    }
                                }
                            }

                        }
                    }
                }
            }
            (Linox.getInstance().getImageStore()).addImageTab(name, result);
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
