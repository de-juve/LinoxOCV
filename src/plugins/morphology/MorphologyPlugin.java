package plugins.morphology;

import entities.*;
import entities.Point;
import gui.Linox;
import gui.dialog.ParameterComboBox;
import gui.dialog.ParameterJPanel;
import gui.dialog.ParameterSlider;
import org.opencv.core.Mat;
import plugins.AbstractPlugin;
import plugins.GrayscalePlugin;

import java.awt.*;
import java.util.ArrayList;
import java.util.Random;
import java.util.TreeMap;

public class MorphologyPlugin extends AbstractPlugin {
    String morphologyOperation;
    int max_kernel_size = 10000, morph_size;
    int[] area, last, representative, status;
    Mat gray;
    int[] shedLabels;

    final static int NONE = -300;
    final static int NOT_ANALYZED = -400;

    ParameterComboBox morphologyOperations = new ParameterComboBox("Type of transformation:",
            new String[]{"Opening", "Closing"});

    //to choose kernel size
    ParameterSlider kernelSize = new ParameterSlider("Size of kernel:\n 2n+1", 1, max_kernel_size, 1);

    public MorphologyPlugin() {
        title = "Morphology";
    }

    @Override
    public void run() {
        Linox.getInstance().getStatusBar().setProgress(title, 0, 100);

        showParamsPanel("Choose params");
        if (exit) {
            return;
        }
    }

    public void run(String operation, int size) {
        ShedCollector.INSTANCE.clear();
        morphologyOperation = operation;
        morph_size = size;
        morphologyOperations();
        DataCollector.INSTANCE.setShedLabels(shedLabels);
    }

    @Override
    public void getParams(ParameterJPanel panel) {
        morphologyOperation = panel.getValueComboBox(morphologyOperations);
        morph_size = panel.getValueSlider(kernelSize);

        morphologyOperations();

        if (tabs == 0) {
            pluginListener.addImageTab();
            tabs++;
        } else {
            pluginListener.replaceImageTab();
        }
    }

    protected void showParamsPanel(String name) {
        ParameterJPanel panel = new ParameterJPanel(name, this);
        panel.addParameterComboBox(morphologyOperations);
        panel.addParameterSlider(kernelSize);

        Linox.getInstance().addParameterJPanel(panel);
    }

    private void morphologyOperations() {
        init();
        gray = GrayscalePlugin.run(image, false);
        MassiveWorker.INSTANCE.sort(gray);

        if (morphologyOperation == "Closing") {
            for (int h = MassiveWorker.INSTANCE.getMax(); h >= MassiveWorker.INSTANCE.getMin(); h--) {
                preflood(h);
            }
        } else {
            for (int h = MassiveWorker.INSTANCE.getMin(); h <= MassiveWorker.INSTANCE.getMax(); h++) {
                preflood(h);
            }
        }

        ArrayList<Integer> ids = MassiveWorker.INSTANCE.getIds();

        Random rand = new Random();
        boolean[] analyzed = new boolean[ids.size()];
        int shedLabel;
        //start from pixels with min property
        for (Integer p : ids) {
            if (analyzed[p]) {
                continue;
            }
            int root = status[p];
            if (root < 0) {
                root = p;
            }
            while (status[root] >= 0) {
                root = status[root];
            }

            int val = status[root];

            if (!analyzed[root]) {
                shedLabel = root;
                shedLabels[root] = shedLabel;
                ShedCollector.INSTANCE.addShed(new Shed(shedLabel, new Color(rand.nextFloat(), rand.nextFloat(), rand.nextFloat())));
                ShedCollector.INSTANCE.addElementToShed(shedLabel, new Point(x(root), y(root)));
            } else {
                shedLabel = shedLabels[root];
            }
            analyzed[root] = true;

            while (p != root) {
                int tmp = status[p];
                status[p] = val;

                shedLabels[p] = shedLabel;
                ShedCollector.INSTANCE.addElementToShed(shedLabel, new Point(x(p), y(p)));
                analyzed[p] = true;

                p = tmp;
            }
        }

        DataCollector.INSTANCE.setStatus(status);
        DataCollector.INSTANCE.setShedLabels(shedLabels);

        result = new Mat(image.rows(), image.cols(), image.type());
        byte[] buff = new byte[(int) image.total() * image.channels()];

        int j = 0;
        for (int i = 0; i < status.length; i++) {
            int bright = -status[i] - 1;
            status[i] = bright;
            for (int k = 0; k < image.channels(); k++) {
                buff[j] = (byte) status[i];
                j++;
            }
        }
        result.put(0, 0, buff);
    }

    private void init() {
        status = new int[image.width() * image.height()];
        shedLabels = new int[status.length];
        area = new int[256];
        last = new int[256];
        representative = new int[256];

        for (int i = 0; i < last.length; i++) {
            last[i] = NONE;
            area[i] = 0;
            representative[i] = NONE;
        }

        for (int i = 0; i < status.length; i++) {
            status[i] = NOT_ANALYZED;
        }
    }

    private void preflood(int grayLevel) {
        TreeMap<Integer, ArrayList<Integer>> map = MassiveWorker.INSTANCE.getMap();
        if (!map.containsKey(grayLevel)) {
            return;
        }
        ArrayList<Integer> pixels = map.get(grayLevel);
        for (Integer pixelId : pixels) {
            if (status[pixelId] != NOT_ANALYZED) {
                continue;
            }
            status[pixelId] = last[grayLevel];
            last[grayLevel] = pixelId;
            representative[grayLevel] = pixelId;
            flood(grayLevel);
        }
    }

    protected int flood(int h) {
        while (last[h] != NONE) {

            //propagation at level h
            //get point from queue
            int p = last[h];
            last[h] = status[last[h]];

            //set to its representative element
            status[p] = representative[h];

            //get neighboures
            //ArrayList<Integer> neighs = PixelsMentor.defineNeighboursIds(p, gray);
            ArrayList<Integer> neighs = PixelsMentor.defineNeighboursIdsWidthDiagonalCondition(p, gray);
            for (Integer nid : neighs) {
                if (status[nid] != NOT_ANALYZED) {
                    continue;
                }
                int m = (int) gray.get(y(nid), x(nid))[0];
                // set representative element if none
                if (representative[m] == NONE) {
                    representative[m] = nid;
                }
                //add to queue
                status[nid] = last[m];
                last[m] = nid;
                if (morphologyOperation.equals("Closing")) {
                    while (m < h) {
                        m = flood(m);
                    }
                } else {
                    while (m > h) {
                        m = flood(m);
                    }
                }
            }
            area[h]++;
        }
        int m = h;
        //parent settings
        if (morphologyOperation.equals("Closing")) {
            m = h + 1;
            while (m <= 255 && representative[m] == NONE) {
                ++m;
            }
            if (m <= 255) {
                if (area[h] < morph_size) {
                    status[representative[h]] = representative[m];
                    area[m] += area[h];
                } else {
                    //area[m] = criteria;
                    status[representative[h]] = -h - 1;
                }
            } else {
                status[representative[h]] = -h - 1;
            }
        } else {
            m = h - 1;
            while (m >= 0 && representative[m] == NONE) {
                --m;
            }
            if (m >= 0) {
                if (area[h] < morph_size) {
                    status[representative[h]] = representative[m];
                    area[m] += area[h];
                } else {
                    // area[m] = criteria;
                    status[representative[h]] = -h - 1;
                }
            } else {
                status[representative[h]] = -h - 1;
            }
        }

        // reset attribute of extracted connected component at level h
        area[h] = 0;
        last[h] = NONE;
        representative[h] = NONE;
        return m;
    }

}
