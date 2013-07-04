package plugins;

import gui.Linox;
import gui.dialog.ChoiceDialog;
import gui.dialog.ParameterComboBox;
import gui.dialog.ParameterSlider;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;

import static org.opencv.core.Core.convertScaleAbs;
import static org.opencv.imgproc.Imgproc.getStructuringElement;
import static org.opencv.imgproc.Imgproc.morphologyEx;

public class MorphologyTransformationsPlugin extends AbstractPlugin {
    int morph_elem = 0;
    int morph_size = 0;
    int morph_operator = 0;
    final int max_kernel_size = 21;
    String morphologyOperation, kernelType;

    public MorphologyTransformationsPlugin() {
        title = "Morphology Transformations";
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
        Linox.getInstance().getStatusBar().setProgress("Morphology Transformations", 0, 100);

        showDialog("Choose params");
        if (exit) {
            return;
        }

        MorphologyOperations();

        DataCollector.INSTANCE.setLaplasiantImg(result.clone());

        Linox.getInstance().getStatusBar().setProgress("Morphology Transformations", 100, 100);
    }

    protected void showDialog(String name) {
        //to select Morphology operation
        ParameterComboBox morphologyOperations = new ParameterComboBox("Type of transformation:",
                new String[]{"Opening", "Closing", "Gradient", "Top Hat", "Black Hat"});

        //to select kernel type
        ParameterComboBox kernelTypes = new ParameterComboBox("Type of kernel:",
                new String[]{"Rect", "Cross", "Ellipse"});

        //to choose kernel size
        ParameterSlider kernelSize = new ParameterSlider("Size of kernel:\n 2n+1", 1, max_kernel_size, 1);

        ChoiceDialog cd = new ChoiceDialog();
        cd.setTitle(name);
        cd.addParameterComboBox(morphologyOperations);
        cd.addParameterComboBox(kernelTypes);
        cd.addParameterSlider(kernelSize);

        cd.pack();
        cd.setVisible(true);
        if (cd.wasCanceled()) {
            exit = true;
            setErrMessage("canceled");
        }
        morphologyOperation = cd.getValueComboBox(morphologyOperations);
        kernelType = cd.getValueComboBox(kernelTypes);
        morph_operator = getCodeMorphOperation();
        morph_elem = getCodeKernelType();
        morph_size = cd.getValueSlider(kernelSize);
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
