package plugins.morphology;

import entities.DataCollector;
import entities.Shed;
import entities.ShedCollector;
import gui.Linox;
import gui.dialog.ParameterComboBox;
import gui.dialog.ParameterJPanel;
import gui.dialog.ParameterSlider;
import org.opencv.core.Mat;
import plugins.AbstractPlugin;
import plugins.imageOperations.ImageOperation;
import plugins.imageOperations.ImageOperationFactory;

import java.util.TreeMap;

public class MorphologyCompilationPlugin extends AbstractPlugin {
    ImageOperationFactory factory;
    int[] resultOfClosing, resultOfOpening, results;
    String morphologyCompilation;
    int max_kernel_size = 10000, morph_size;
    ParameterComboBox morphologyOperations;
    ParameterSlider kernelSize;
    int tabs = 0;

    public MorphologyCompilationPlugin() {
        factory = new ImageOperationFactory();
        title = "Morphological compilation";
    }

    /*@Override
    public ImagePlus getResult(boolean addToStack) {
        if (result == null) {
            create(imageProcessor, results);
            result = new ImagePlus(typeCompilation + " " + criteria + " " + DataCollection.INSTANCE.getImageOriginal().getTitle(), imageProcessor);
            if (addToStack) {

                ImageProcessor ip = imageProcessor.duplicate();
                Color[] colors = new Color[width * height];
                for (int i = 0; i < DataCollection.INSTANCE.getShedLabels().length; i++) {
                    colors[i] = ShedWorker.getInstance().getShedColor(DataCollection.INSTANCE.getShedLabel(i));
                }
                create(ip, colors);
                DataCollection.INSTANCE.addtoHistory(new ImagePlus("area closing colors " + criteria + " " + DataCollection.INSTANCE.getImageOriginal().getTitle(), ip));
                DataCollection.INSTANCE.addtoHistory(result);

            }
        }

        return result;
    }*/

    @Override
    public void run() {
        showParamsPanel("Choose params");
        if (exit) {
            return;
        }
    }

    @Override
    public void cancel() {
        Linox.getInstance().removeParameterJPanel();
        exit = true;
        setErrMessage("canceled");
        pluginListener.stopPlugin();
    }

    @Override
    public void getParams(ParameterJPanel panel) {
        morphologyCompilation = panel.getValueComboBox(morphologyOperations);
        morph_size = panel.getValueSlider(kernelSize);

        MorphologyCompilation();

        if (tabs == 0) {
            pluginListener.addImageTab();
            tabs++;
        } else {
            pluginListener.replaceImageTab();
        }
    }

    @Override
    public void finish() {
        //DataCollector.INSTANCE.setShedLabels(shedLabels);

        Linox.getInstance().getStatusBar().setProgress("Morphology", 100, 100);
        Linox.getInstance().removeParameterJPanel();
        pluginListener.finishPlugin();
    }

    private void MorphologyCompilation() {
        MorphologyPlugin morph = new MorphologyPlugin();
        morph.initImage(image);
        morph.run("Closing", morph_size);
        resultOfClosing = DataCollector.INSTANCE.getStatus();
        TreeMap<Integer, Shed> closingSheds = (TreeMap<Integer, Shed>) ShedCollector.INSTANCE.getSheds().clone();
        DataCollector.INSTANCE.setPrevShedLabels(DataCollector.INSTANCE.getShedLabels());

        morph.run("Opening", morph_size);
        resultOfOpening = DataCollector.INSTANCE.getStatus();
        TreeMap<Integer, Shed> openingSheds = (TreeMap<Integer, Shed>) ShedCollector.INSTANCE.getSheds().clone();

        results = new int[resultOfClosing.length];

        ImageOperation imageOperation = factory.createImageOperation(morphologyCompilation);
        imageOperation.setWidth(image.width());
        imageOperation.setHeight(image.height());
        imageOperation.createImage(resultOfClosing, resultOfOpening, closingSheds, openingSheds, results);

        result = new Mat(image.rows(), image.cols(), image.type());
        byte[] buff = new byte[(int) image.total() * image.channels()];

        int j = 0;
        for (int i = 0; i < results.length; i++) {
            for (int k = 0; k < image.channels(); k++) {
                buff[j] = (byte) results[i];
                j++;
            }
        }
        result.put(0, 0, buff);

        /*create(imageProcessor, results);
        type = "equaling";
        morfRun();
        results = DataCollection.INSTANCE.getStatuses();    */
    }


    protected void showParamsPanel(String name) {
        morphologyOperations = new ParameterComboBox("Type of compilation:",
                factory.getOperationMap().keySet().toArray(new String[0]));

        //to choose kernel size
        kernelSize = new ParameterSlider("Size of kernel:\n 2n+1", 1, max_kernel_size, 1);

        ParameterJPanel panel = new ParameterJPanel(name, this);
        panel.addParameterComboBox(morphologyOperations);
        panel.addParameterSlider(kernelSize);

        Linox.getInstance().addParameterJPanel(panel);
    }
}
