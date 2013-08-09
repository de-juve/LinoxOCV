package plugins;

import entities.*;
import gui.Linox;
import org.opencv.core.Mat;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;
import plugins.morphology.MorphologyPlugin;

import java.util.ArrayList;
import java.util.TreeMap;

public class WatershedPlugin extends AbstractPlugin {
    Mat gray;
    int[] lowerCompletion, shedLabels;
    TreeMap<Integer, ArrayList<Integer>> steepestNeighbours;
    boolean[] maximum;
    final Point N = new Point(-1, -1);

    public WatershedPlugin() {
        title = "Watershed";
    }

    @Override
    public void run() {
        Linox.getInstance().getStatusBar().setProgress("Watershed", 0, 100);

        GrayscalePlugin.run(image, true);

        MorphologyPlugin mp = new MorphologyPlugin();
        mp.initImage(DataCollector.INSTANCE.getGrayImg());
        mp.run("Closing", 1);
        result = mp.result;

        LowerCompletePlugin lcp = new LowerCompletePlugin();
        lcp.initImage(result);
        lcp.run();

        constructDAG();
        flood();


       /* Imgproc.threshold(result, result, 127, 255, Imgproc.THRESH_BINARY);
        Mat skel = new Mat(result.size(), result.type(), new Scalar(0));
        Mat temp = new Mat(result.size(),result.type()), eroded = new Mat(result.size(),result.type());//CvType.CV_8UC1 );
        Mat m = new Mat();*/
        Mat element = Imgproc.getStructuringElement(Imgproc.MORPH_CROSS, new Size(3, 3));
        // boolean done;

        Imgproc.dilate(result, result, element);

       /* do
        {
            Imgproc.erode(result, eroded, element);
            Imgproc.dilate(eroded, temp, element);
            Core.subtract(result, temp, temp);
            Core.bitwise_or(skel, temp, skel);
            eroded.copyTo(result);

            Core.extractChannel(result, m, 0);
            done = (Core.countNonZero(m) == 0);
        } while (!done);

        skel.copyTo(result);*/

        mp.initImage(result);
        mp.run("Closing", 1);
        result = mp.result;

        Linox.getInstance().getStatusBar().setProgress("Watershed", 100, 100);

        if (pluginListener != null) {
            pluginListener.addImageTab();
            pluginListener.finishPlugin();
        }
    }

    private void constructDAG() {
        maximum = new boolean[(int) image.total()];
        steepestNeighbours = new TreeMap<>();
        gray = DataCollector.INSTANCE.getGrayImg();
        lowerCompletion = DataCollector.INSTANCE.getLowerCompletion();
        MassiveWorker.INSTANCE.sort(gray, lowerCompletion);
        shedLabels = DataCollector.INSTANCE.getShedLabels();

        //start from pixels with min lower completion
        ArrayList<Integer> ids = MassiveWorker.INSTANCE.getIds();

        for (int i = ids.size() - 1; i > -1; i--) {
            int id = ids.get(i);

            ArrayList<Integer> neighbours = PixelsMentor.defineNeighboursIdsWithLowerValue(id, gray);
            if (neighbours.isEmpty()) {
                ArrayList<Integer> eqNeighbours = PixelsMentor.defineNeighboursIdsWithSameValue(id, gray);
                neighbours.clear();
                for (Integer eq : eqNeighbours) {
                    if (lowerCompletion[eq] < lowerCompletion[id]) {
                        neighbours.add(eq);
                    }
                }
            }
            if (!neighbours.isEmpty()) {
                maximum[id] = true;
                steepestNeighbours.remove(id);
                steepestNeighbours.put(id, neighbours);
                neighbouresNotMax(id);
            } else {
                //define canonical element of min region if it need and if we can
                maximum[id] = false;
                Shed shed = ShedCollector.INSTANCE.getShed(shedLabels[id]);
                Point canonical = shed.getCanonical();

                if (canonical.equals(N)) {
                    canonical.x = x(id);
                    canonical.y = y(id);
                    shed.setCanonical(canonical);
                }
                ArrayList<Integer> ar = new ArrayList<>(1);
                ar.add(0, id(canonical.x, canonical.y));
                steepestNeighbours.remove(id);
                steepestNeighbours.put(id, ar);
            }
        }
    }

    private void neighbouresNotMax(int id) {
        int x = x(id);
        int y = y(id);
        ArrayList<Integer> neighboures = PixelsMentor.getNeighborsOfPixel(x, y, image, 1);//defineNeighboursIds(id, width, height);
        for (Integer n : neighboures) {
            int nx = x(n);
            int ny = y(n);
            if (maximum[n] && (gray.get(ny, nx)[0] < gray.get(y, x)[0] ||
                    (gray.get(ny, nx)[0] == gray.get(y, x)[0] &&
                            lowerCompletion[n] < lowerCompletion[id]))) {
                maximum[n] = false;
                neighbouresNotMax(n);
            }
        }
    }

    //Recursive function for resolving the downstream paths of the lower complete graph
    //Returns representative element of pixel p, or W if p is a watershed pixel
    private int resolve(int p) {
        int i = 0;
        int rep = -2;
        ArrayList<Integer> stN = steepestNeighbours.get(p);
        if (stN == null)
            return rep;
        int con = stN.size();
        while (i < con && rep != -1) {
            int sln = stN.get(i);
            if (sln != p && sln != -1) {
                sln = resolve(sln);
                if (sln > -1) {
                    stN.set(i, sln);
                }
            }
            if (i == 0) {
                rep = stN.get(i);
            } else if (sln != rep && sln > -1 && rep > -1) {
                rep = -1;
                stN.clear();
                stN.add(-1);
            }
            i++;
        }
        return rep;
    }

    private void flood() {
        result = new Mat(image.rows(), image.cols(), image.type());
        int[] watershed = new int[(int) image.total()];
        ArrayList<Integer> ids = MassiveWorker.INSTANCE.getIds();
        //start from pixels with min property
        for (int i = ids.size() - 1; i >= 0; i--) {
            int p = ids.get(i);
            int rep = resolve(p);

            if (rep == -1) {
                watershed[p] = 255;
            } else {
                watershed[p] = 0;
            }
        }
        DataCollector.INSTANCE.setWatershedPoints(watershed);

        byte[] buff = new byte[(int) image.total() * image.channels()];

        int j = 0;
        for (int i = 0; i < watershed.length; i++) {
            for (int k = 0; k < image.channels(); k++) {
                buff[j] = (byte) watershed[i];
                j++;
            }
        }

        result.put(0, 0, buff);
    }
}
