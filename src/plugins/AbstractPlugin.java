package plugins;

import entities.DataCollector;
import entities.Point;
import gui.Linox;
import gui.dialog.ParameterJPanel;
import gui.menu.IPluginRunner;
import org.opencv.core.Mat;


public class AbstractPlugin implements IPluginFilter {
    public int tabs = 0;
    protected Mat image, result = null;
    protected IPluginRunner pluginListener;
    public boolean exit = false;
    //  protected int criteria;
    protected String errMessage = "";
    protected String title = "";

    public Mat getResult(boolean addToStack) {
        if (addToStack) {
            DataCollector.INSTANCE.addtoHistory(title, result);
        }
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

    /**
     * Выводим сообщение в консоль или в другой out, если его переопределили
     * @param message
     */
    public static void printMessage(String message) {
        System.out.println(message);
    }

    protected void print(String message) {
        printMessage(message);
    }



    @Override
    public void run() {
    }

    @Override
    public void cancel() {
        Linox.getInstance().removeParameterJPanel();
        exit = true;
        setErrMessage("canceled");
        pluginListener.stopPlugin();
    }

    @Override
    public void finish() {
        Linox.getInstance().getStatusBar().setProgress(title, 100, 100);
        Linox.getInstance().removeParameterJPanel();
        pluginListener.finishPlugin();
    }

    @Override
    public void getParams(ParameterJPanel parameterJPanel) {
    }

    @Override
    public void addRunListener(IPluginRunner runner) {
        pluginListener = runner;
    }

    public void initImage(Mat _image) {
        image = _image;
        result = new Mat(image.height(), image.width(), image.depth());
    }

    public boolean exit() {
        return exit;
    }

    protected int id(Point point) {
        return id(point.x, point.y);
    }

    protected int id(int x, int y) {
        return x + y * image.width();
    }

    protected int x(int id) {
        return id % image.width();
    }

    protected int y(int id) {
        return id / image.width();
    }

    protected boolean isInBox(Point point) {
        return point.x >= 0 && point.x < image.width() && point.y >= 0 && point.y < image.height();
    }

    protected Mat setPointsToImage(int[] points) {
        Mat result = new Mat(image.rows(), image.cols(), image.type());
        byte[] buff = new byte[(int) image.total() * image.channels()];

        int j = 0;
        for (int i = 0; i < points.length; i++) {
            for (int k = 0; k < image.channels(); k++) {
                buff[j] = (byte) points[i];
                j++;
            }
        }

        result.put(0, 0, buff);
        return result;
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

