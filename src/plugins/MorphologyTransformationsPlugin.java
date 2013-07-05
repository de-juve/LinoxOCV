package plugins;

import entities.DataCollector;
import gui.Linox;
import gui.dialog.ParameterComboBox;
import gui.dialog.ParameterJPanel;
import gui.dialog.ParameterSlider;
import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Size;

import static org.opencv.imgproc.Imgproc.getStructuringElement;
import static org.opencv.imgproc.Imgproc.morphologyEx;

public class MorphologyTransformationsPlugin extends AbstractPlugin {
    int tabs = 0;
    int morph_elem = 0;
    int morph_size = 0;
    int morph_operator = 0;
    final int max_kernel_size = 21;
    String morphologyOperation, kernelType;
    //to select Morphology operation
    ParameterComboBox morphologyOperations = new ParameterComboBox("Type of transformation:",
            new String[]{"Opening", "Closing", "Gradient", "Top Hat", "Black Hat"});

    //to select kernel type
    ParameterComboBox kernelTypes = new ParameterComboBox("Type of kernel:",
            new String[]{"Rect", "Cross", "Ellipse"});

    //to choose kernel size
    ParameterSlider kernelSize = new ParameterSlider("Size of kernel:\n 2n+1", 1, max_kernel_size, 1);

    public MorphologyTransformationsPlugin() {
        title = "Morphology Transformations";
    }

    @Override
    public void run() {
        Linox.getInstance().getStatusBar().setProgress("Morphology Transformations", 0, 100);

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
        morphologyOperation = panel.getValueComboBox(morphologyOperations);
        kernelType = panel.getValueComboBox(kernelTypes);
        morph_operator = getCodeMorphOperation();
        morph_elem = getCodeKernelType();
        morph_size = panel.getValueSlider(kernelSize);

        MorphologyOperations();

        if(tabs == 0) {
            pluginListener.addImageTab();
            tabs++;
        } else {
            pluginListener.replaceImageTab();
        }
    }

    @Override
    public void finish() {
        Linox.getInstance().getStatusBar().setProgress("Morphology Transformations", 100, 100);
        Linox.getInstance().removeParameterJPanel();
        pluginListener.finishPlugin();
    }

    protected void showParamsPanel(String name) {
        ParameterJPanel panel = new ParameterJPanel(name, this);
        panel.addParameterComboBox(morphologyOperations);
        panel.addParameterComboBox(kernelTypes);
        panel.addParameterSlider(kernelSize);

        Linox.getInstance().addParameterJPanel(panel);
    }


    private void MorphologyOperations() {
        // Since MORPH_X : 2,3,4,5 and 6
        int operation = morph_operator + 2;

        Mat element = getStructuringElement( morph_elem, new Size( 2*morph_size + 1, 2*morph_size+1 ), new Point( morph_size, morph_size ) );

        /// Apply the specified morphology operation
        morphologyEx( image, result, operation, element );
    }

    private int getCodeMorphOperation() {
        switch (morphologyOperation) {
            case "Opening" : return 0;
            case "Closing" : return 1;
            case "Gradient" : return 2;
            case "Top Hat" : return 3;
            case "Black Hat" : return 4;
            default: return -1;
        }
    }

    private int getCodeKernelType() {
        switch (kernelType) {
            case "Rect" : return 0;
            case "Cross" : return 1;
            case "Ellipse" : return 2;
            default: return -1;
        }
    }


}
