package plugins;

import gui.Linox;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.imgproc.Imgproc;

public class GrayscalePlugin extends AbstractPlugin {
    public GrayscalePlugin() {
        title = "Grayscale";
    }

    @Override
    public Mat getResult(boolean addToStack) {
        if (result == null) {
           // create(DataCollector.INSTANCE.getGrayscale());

            if (addToStack) {
                //DataCollection.INSTANCE.addtoHistory(result);
            }
        }

        return result;
    }

    @Override
    public void run() {
        Linox.getInstance().getStatusBar().setProgress("luminance", 0, 100);

        if(image.channels() == 3 || image.channels() == 4) {
            Imgproc.cvtColor(image, result,  Imgproc.COLOR_RGB2GRAY );
        } else {
            result = image;
        }

        DataCollector.INSTANCE.setGrayImg(result.clone());


        Linox.getInstance().getStatusBar().setProgress("luminance", 100, 100);
    }

    /*private void calculateLuminance() {
              for(int row = 0; row < image.height(); row++) {
              for(int col = 0; col < image.width(); col++) {
                  double[] px;
                  px = image.get(row, col);
                  double r = px[0];
                  double g = px[1];
                  double b = px[2];
                  int lum = (int) (0.299 * r + 0.587 * g + 0.114 * b);
                  int id = col + row*image.width();
                  DataCollector.INSTANCE.setGrayscale(id, lum);
                  Linox.getInstance().getStatusBar().setProgress("grayscale", id / image.width()*image.height(), 100);
              }
          }
    }*/
}
