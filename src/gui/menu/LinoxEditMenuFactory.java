package gui.menu;

import plugins.*;
import plugins.morphology.MorphologyCompilationPlugin;
import plugins.morphology.MorphologyPlugin;
import plugins.roadNetwork.Builder;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.io.File;
import java.util.ArrayList;

public class LinoxEditMenuFactory {
    private final ArrayList<JMenuItem> items = new ArrayList<>();

    public ArrayList<JMenuItem> getItems() {
        return items;
    }

    public LinoxEditMenuFactory() {
        final PluginRunner pluginRunner = new PluginRunner();

        final Action resize = new AbstractAction("Resize") {
            @Override
            public void actionPerformed(ActionEvent e) {
                pluginRunner.setPlugin(new ResizePlugin());
                Thread myThready = new Thread(pluginRunner);
                myThready.start();
            }
        };

        final Action compress = new AbstractAction("Compress") {
            @Override
            public void actionPerformed(ActionEvent e) {
                pluginRunner.setPlugin(new CompressPlugin());
                Thread myThready = new Thread(pluginRunner);
                myThready.start();
            }
        };

        final Action grayscale = new AbstractAction("Grayscale") {
            @Override
            public void actionPerformed(ActionEvent e) {
                pluginRunner.setPlugin(new GrayscalePlugin());
                Thread myThready = new Thread(pluginRunner);
                myThready.start();
            }
        };

        final Action invert = new AbstractAction("Invert") {
            @Override
            public void actionPerformed(ActionEvent e) {
                pluginRunner.setPlugin(new InvertPlugin());
                Thread myThready = new Thread(pluginRunner);
                myThready.start();
            }
        };

        final Action gradient = new AbstractAction("Gradient") {
            @Override
            public void actionPerformed(ActionEvent e) {
                pluginRunner.setPlugin(new GradientPlugin());
                Thread myThready = new Thread(pluginRunner);
                myThready.start();
            }
        };

        final Action laplasian = new AbstractAction("Laplasian") {
            @Override
            public void actionPerformed(ActionEvent e) {
                pluginRunner.setPlugin(new LaplasianPlugin());
                Thread myThready = new Thread(pluginRunner);
                myThready.start();
            }
        };

        final Action homotopy = new AbstractAction("Homotopy") {
            @Override
            public void actionPerformed(ActionEvent e) {
                pluginRunner.setPlugin(new HomotopyPlugin());
                Thread myThready = new Thread(pluginRunner);
                myThready.start();
            }
        };

        final Action median = new AbstractAction("Median") {
            @Override
            public void actionPerformed(ActionEvent e) {
                pluginRunner.setPlugin(new MedianPlugin());
                Thread myThready = new Thread(pluginRunner);
                myThready.start();
            }
        };

        final Action lower = new AbstractAction("Lower completion") {
            @Override
            public void actionPerformed(ActionEvent e) {
                pluginRunner.setPlugin(new LowerCompletePlugin());
                Thread myThready = new Thread(pluginRunner);
                myThready.start();
            }
        };

        final Action morphologyTransformation = new AbstractAction("Morphology OpenCV") {
            @Override
            public void actionPerformed(ActionEvent e) {
                pluginRunner.setPlugin(new MorphologyTransformationsPlugin());
                Thread myThready = new Thread(pluginRunner);
                myThready.start();
            }
        };

        final Action morphology = new AbstractAction("Morphology") {
            @Override
            public void actionPerformed(ActionEvent e) {
                pluginRunner.setPlugin(new MorphologyPlugin());
                Thread myThready = new Thread(pluginRunner);
                myThready.start();
            }
        };

        final Action morphologyCompilation = new AbstractAction("Morphology compilations") {
            @Override
            public void actionPerformed(ActionEvent e) {
                pluginRunner.setPlugin(new MorphologyCompilationPlugin());
                Thread myThready = new Thread(pluginRunner);
                myThready.start();
            }
        };

        final Action watershed = new AbstractAction("Watershed") {
            @Override
            public void actionPerformed(ActionEvent e) {
                pluginRunner.setPlugin(new WatershedPlugin());
                Thread myThready = new Thread(pluginRunner);
                myThready.start();
            }
        };

        final Action watershedSegmenter = new AbstractAction("Watershed segmenter") {
            @Override
            public void actionPerformed(ActionEvent e) {
                pluginRunner.setPlugin(new WatershedSegmenterPlugin());
                Thread myThready = new Thread(pluginRunner);
                myThready.start();
            }
        };

        final Action shedCluster = new AbstractAction("Shed cluster") {
            @Override
            public void actionPerformed(ActionEvent e) {
                pluginRunner.setPlugin(new ShedClusterPlugin());
                Thread myThready = new Thread(pluginRunner);
                myThready.start();
            }
        };

        final Action shedPainter = new AbstractAction("Shed paint") {
            @Override
            public void actionPerformed(ActionEvent e) {
                pluginRunner.setPlugin(new ShedPainterPlugin());
                Thread myThready = new Thread(pluginRunner);
                myThready.start();
            }
        };

        final Action histogram = new AbstractAction("Histogram") {
            @Override
            public void actionPerformed(ActionEvent e) {
                pluginRunner.setPlugin(new HistogramPlugin());
                Thread myThready = new Thread(pluginRunner);
                myThready.start();
            }
        };

        final Action hough = new AbstractAction("Hough transform") {
            @Override
            public void actionPerformed(ActionEvent e) {
                pluginRunner.setPlugin(new HoughTransformPlugin());
                Thread myThready = new Thread(pluginRunner);
                myThready.start();
            }
        };

        final Action ols = new AbstractAction("Ordinary least squares") {
            @Override
            public void actionPerformed(ActionEvent e) {
                pluginRunner.setPlugin(new OLSPlugin());
                Thread myThready = new Thread(pluginRunner);
                myThready.start();
            }
        };

        final Action canny = new AbstractAction("Canny") {
            @Override
            public void actionPerformed(ActionEvent e) {
                pluginRunner.setPlugin(new CannyPlugin());
                Thread myThready = new Thread(pluginRunner);
                myThready.start();
            }
        };

        final Action roadBuilder = new AbstractAction("Road builder") {
            @Override
            public void actionPerformed(ActionEvent e) {
                pluginRunner.setPlugin(new Builder());
                Thread myThready = new Thread(pluginRunner);
                myThready.start();
            }
        };

        final Action matting = new AbstractAction("Image matting") {
            @Override
            public void actionPerformed(ActionEvent e) {
                pluginRunner.setPlugin(new ImageMattingPlugin());
                Thread myThready = new Thread(pluginRunner);
                myThready.start();
            }
        };

        final Action localAnalyse = new AbstractAction("Local analyse") {
            @Override
            public void actionPerformed(ActionEvent e) {
                pluginRunner.setPlugin(new LocalAnalysis());
                Thread myThready = new Thread(pluginRunner);
                myThready.start();
            }
        };

        final Action globalAnalyse = new AbstractAction("Global analyse") {
            @Override
            public void actionPerformed(ActionEvent e) {
                pluginRunner.setPlugin(new GlobalAnalysis());
                Thread myThready = new Thread(pluginRunner);
                myThready.start();
            }
        };

        final Action test = new AbstractAction("Test (interpolate)") {
            @Override
            public void actionPerformed(ActionEvent e) {
                pluginRunner.setPlugin(new TestPlugin());
                Thread myThready = new Thread(pluginRunner);
                myThready.start();
            }
        };

        items.add(new JMenuItem(resize));
        items.add(new JMenuItem(compress));
        items.add(new JMenuItem(grayscale));
        items.add(new JMenuItem(invert));
        items.add(new JMenuItem(gradient));
        items.add(new JMenuItem(laplasian));
        items.add(new JMenuItem(lower));
        items.add(new JMenuItem(homotopy));
        items.add(new JMenuItem(median));
        items.add(new JMenuItem(morphologyTransformation));
        items.add(new JMenuItem(morphology));
        items.add(new JMenuItem(morphologyCompilation));
        items.add(new JMenuItem(watershed));
        items.add(new JMenuItem(watershedSegmenter));
        items.add(new JMenuItem(shedCluster));
        items.add(new JMenuItem(shedPainter));
        items.add(new JMenuItem(histogram));
        items.add(new JMenuItem(hough));
        items.add(new JMenuItem(ols));
        items.add(new JMenuItem(canny));
        items.add(new JMenuItem(roadBuilder));
        items.add(new JMenuItem(matting));
        items.add(new JMenuItem(localAnalyse));
        items.add(new JMenuItem(globalAnalyse));
        items.add(new JMenuItem(test));

        for (JMenuItem item : items) {
            item.setEnabled(false);
        }

        /*JMenuItem item = new JMenuItem(test1);
        item.setEnabled(true);
        items.add(item);

        item = new JMenuItem(test2);
        item.setEnabled(true);
        items.add(item);*/

    }

    private void loadImage(File file) {
//        ij.ImagePlus img =   IJ.openImage(file.getPath());
//        DataCollection.INSTANCE.setImageOriginal(new ImagePlus(file.getName(), img.getProcessor().convertToRGB()));
    }
}
