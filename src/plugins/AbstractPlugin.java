package plugins;

import org.opencv.core.Mat;


public class AbstractPlugin implements PluginFilter {
    protected Mat image, result = null;

    public boolean exit = false;
    protected int criteria;
    protected String errMessage = "";
    protected String title = "";

    public Mat getResult(boolean addToStack) {
        return result;
    }

    public String getTitle() {
        return title;
    }

    public String getErrMessage() {
        return errMessage;
    }

    protected void setErrMessage(String message) {
        errMessage = message;
    }

    @Override
    public void run() {
    }

    public void initProcessor(Mat _image) {
        image = _image;
        result = new Mat(image.height(), image.width(), image.depth());
    }

    public boolean exit() {
        return exit;
    }

    protected void setCriteria(int _criteria) {
        criteria = _criteria;
    }

  protected void create(Integer[] array) {
        for (int i = 0; i < array.length; i++) {
            double[] data = new double[3];
            data[0] = array[i];
            data[1] = array[i];
            data[2] = array[i];
            int row = i / image.width();
            int col = i % image.width();
            result.put(row, col, data);
        }
     /* DataCollector.INSTANCE.setImageResult(result);
      DataCollector.INSTANCE.setImageResultTitle(title);*/
    }

   /*   protected void create(ImageProcessor ip, Color[] colors) {
        for (int i = 0; i < colors.length; i++) {
            int value = colors[i].getRGB();
            ip.set(i, value);
        }
    }

    protected void recoverWatershedLines() {
        DataCollection.INSTANCE.newWaterShedPoints();
        DataCollection.INSTANCE.newWshPoints(DataCollection.INSTANCE.getGrayscale().length);

        for (Integer i = 0; i < DataCollection.INSTANCE.getGrayscale().length; i++) {
            if (DataCollection.INSTANCE.getGrayscale(i) > 0) {
                DataCollection.INSTANCE.setWshPoint(i, 255);
                DataCollection.INSTANCE.addWaterShedPoint(i);
            } else {
                DataCollection.INSTANCE.setWshPoint(i, 0);
            }
        }
    }*/
}

