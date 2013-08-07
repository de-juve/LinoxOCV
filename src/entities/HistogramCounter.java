package entities;

import java.util.Arrays;

public class HistogramCounter {
    private double[] histogram;
    private double mean, absDev, stdDev, median;

    public void count(Integer[] luminances) {
        histogram = new double[256];
        mean = absDev = stdDev = median = 0;
        for (int i = 0; i < luminances.length; i++) {
            histogram[luminances[i]]++;
        }
        for (int i = 0; i < histogram.length; i++) {
            histogram[i] /= luminances.length;
            mean += (i) * histogram[i];
        }
        for (int i = 0; i < histogram.length; i++) {
            absDev += Math.abs(i - mean) * histogram[i];
            stdDev += Math.pow(i - mean, 2) * histogram[i];
        }
        stdDev = Math.sqrt(stdDev);
        Integer[] lum = luminances.clone();
        Arrays.sort(lum);
        median = lum[luminances.length / 2];
    }

    public double[] getHistogram() {
        return histogram;
    }

    public double getMean() {
        return mean;
    }

    public double getAbsDev() {
        return absDev;
    }

    public double getStdDev() {
        return stdDev;
    }

    public double getMedian() {
        return median;
    }

    public double getHistogramValue(int i) {
        if (i >= 0 && i < 256) {
            return histogram[i];
        }
        throw new ArrayIndexOutOfBoundsException(i);
    }
}

