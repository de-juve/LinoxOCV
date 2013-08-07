package plugins.imageOperations;

import entities.DataCollector;
import entities.Shed;

import java.util.TreeMap;

public class ImageOperationSmart extends ImageOperation {

    @Override
    protected void defineValues(int[] closing, int[] opening) {
    }

    @Override
    protected void defineValues(int[] closing, int[] opening, TreeMap<Integer, Shed> closingSheds, TreeMap<Integer, Shed> openingSheds) {

        for (int i = 0; i < closing.length; i++) {
            int areaCls, areaOpn;

            areaCls = countHomogenAreaSize(i, closingSheds, true);
            areaOpn = countHomogenAreaSize(i, openingSheds, false);
            if (areaOpn > 0 && areaCls >= areaOpn) {
                values.add(i, closing[i]);
            } else if (areaOpn > 0) {
                values.add(i, opening[i]);
            } else if (areaCls > 0) {
                values.add(i, closing[i]);
            } else {
                values.add(i, Math.min(closing[i], opening[i]));
            }
        }
    }

    private int countHomogenAreaSize(Integer p, TreeMap<Integer, Shed> sheds, boolean prev) {
        int count;
        Integer label;

        if (prev) {
            int[] labels = DataCollector.INSTANCE.getPrevShedLabels();
            label = labels[p];
        } else {
            int[] labels = DataCollector.INSTANCE.getShedLabels();
            label = labels[p];
        }

        count = sheds.get(label).size();
        return count;
    }
}
