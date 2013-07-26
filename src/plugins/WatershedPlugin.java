package plugins;

import entities.*;
import gui.Linox;
import org.opencv.core.Mat;
import plugins.morphology.MorphologyPlugin;

import java.util.ArrayList;
import java.util.TreeMap;

public class WatershedPlugin extends AbstractPlugin {
    Mat gray;
    int[] lowerCompletion, shedLabels;
    TreeMap<Integer, ArrayList<Integer>> steepestNeighboures;
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

        Linox.getInstance().getStatusBar().setProgress("Watershed", 100, 100);

        if (pluginListener != null) {
            pluginListener.addImageTab();
            pluginListener.finishPlugin();
        }
    }

    private void constructDAG() {
        maximum = new boolean[(int) image.total()];
        steepestNeighboures = new TreeMap<>();
        gray = DataCollector.INSTANCE.getGrayImg();
        lowerCompletion = DataCollector.INSTANCE.getLowerCompletion();
        MassiveWorker.INSTANCE.sort(gray, lowerCompletion);
        shedLabels = DataCollector.INSTANCE.getShedLabels();

        //start from pixels with min lower completion
        ArrayList<Integer> ids = MassiveWorker.INSTANCE.getIds();

        for (int i = ids.size() - 1; i > -1; i--) {
            int id = ids.get(i);

            ArrayList<Integer> neighboures = PixelsMentor.defineNeighboursIdsWithLowerValue(id, gray);
            if (neighboures.isEmpty()) {
                ArrayList<Integer> eqneighboures = PixelsMentor.defineNeighboursIdsWithSameValue(id, gray);
                neighboures.clear();
                for (Integer eq : eqneighboures) {
                    if (lowerCompletion[eq] < lowerCompletion[id]) {
                        neighboures.add(eq);
                    }
                }
            }
            if (!neighboures.isEmpty()) {
                maximum[id] = true;
                steepestNeighboures.remove(id);
                steepestNeighboures.put(id, neighboures);
                neighbouresNotMax(id);
            } else {
                //define canonical element of min region if it need and if we can
                maximum[id] = false;
                Shed shed = ShedCollector.INSTANCE.getShed(shedLabels[id]);
                Point canonical = shed.getCanonical();

                if (canonical.equals(N)) {
                    canonical.x = id % image.width();
                    canonical.y = id / image.width();
                    shed.setCanonical(canonical);
                }
                ArrayList<Integer> ar = new ArrayList<>(1);
                ar.add(0, canonical.x + canonical.y * image.width());
                steepestNeighboures.remove(id);
                steepestNeighboures.put(id, ar);
            }
        }
    }

    private void neighbouresNotMax(int id) {
        int x = id % image.width();
        int y = id / image.width();
        ArrayList<Integer> neighboures = PixelsMentor.getNeighborsOfPixel(x, y, image, 1);//defineNeighboursIds(id, width, height);
        for (Integer n : neighboures) {
            int nx = n % image.width();
            int ny = n / image.width();
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
        ArrayList<Integer> stN = steepestNeighboures.get(p);
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
