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

        final Action resize = new AbstractAction( "Resize" ) {
            @Override
            public void actionPerformed( ActionEvent e ) {
                pluginRunner.setPlugin( new ResizePlugin() );
                Thread myThready = new Thread( pluginRunner );
                myThready.start();
            }
        };

        final Action compress = new AbstractAction( "Compress" ) {
            @Override
            public void actionPerformed( ActionEvent e ) {
                pluginRunner.setPlugin( new CompressPlugin() );
                Thread myThready = new Thread( pluginRunner );
                myThready.start();
            }
        };

        final Action grayscale = new AbstractAction( "Grayscale" ) {
            @Override
            public void actionPerformed( ActionEvent e ) {
                pluginRunner.setPlugin( new GrayscalePlugin() );
                Thread myThready = new Thread( pluginRunner );
                myThready.start();
            }
        };

        final Action gradient = new AbstractAction( "Gradient" ) {
            @Override
            public void actionPerformed( ActionEvent e ) {
                pluginRunner.setPlugin( new GradientPlugin() );
                Thread myThready = new Thread( pluginRunner );
                myThready.start();
            }
        };

        final Action laplasian = new AbstractAction( "Laplasian" ) {
            @Override
            public void actionPerformed( ActionEvent e ) {
                pluginRunner.setPlugin( new LaplasianPlugin() );
                Thread myThready = new Thread( pluginRunner );
                myThready.start();
            }
        };

        final Action homotopy = new AbstractAction( "Homotopy" ) {
            @Override
            public void actionPerformed( ActionEvent e ) {
                pluginRunner.setPlugin( new HomotopyPlugin() );
                Thread myThready = new Thread( pluginRunner );
                myThready.start();
            }
        };

        final Action median = new AbstractAction( "Median" ) {
            @Override
            public void actionPerformed( ActionEvent e ) {
                pluginRunner.setPlugin( new MedianPlugin() );
                Thread myThready = new Thread( pluginRunner );
                myThready.start();
            }
        };

        final Action morphologyTransformation = new AbstractAction( "Morphology Transformation" ) {
            @Override
            public void actionPerformed( ActionEvent e ) {
                pluginRunner.setPlugin( new MorphologyTransformationsPlugin() );
                Thread myThready = new Thread( pluginRunner );
                myThready.start();
            }
        };

        final Action morphology = new AbstractAction( "Morphology" ) {
            @Override
            public void actionPerformed( ActionEvent e ) {
                pluginRunner.setPlugin( new MorphologyPlugin() );
                Thread myThready = new Thread( pluginRunner );
                myThready.start();
            }
        };

        final Action morphologyCompilation = new AbstractAction( "Morphology compilation" ) {
            @Override
            public void actionPerformed( ActionEvent e ) {
                pluginRunner.setPlugin( new MorphologyCompilationPlugin() );
                Thread myThready = new Thread( pluginRunner );
                myThready.start();
            }
        };

        final Action watershed = new AbstractAction( "Watershed" ) {
            @Override
            public void actionPerformed( ActionEvent e ) {
                pluginRunner.setPlugin( new WatershedPlugin() );
                Thread myThready = new Thread( pluginRunner );
                myThready.start();
            }
        };

        final Action watershedSegmenter = new AbstractAction( "Watershed segmenter" ) {
            @Override
            public void actionPerformed( ActionEvent e ) {
                pluginRunner.setPlugin( new WatershedSegmenterPlugin() );
                Thread myThready = new Thread( pluginRunner );
                myThready.start();
            }
        };

        final Action shedCluster = new AbstractAction( "Shed cluster" ) {
            @Override
            public void actionPerformed( ActionEvent e ) {
                pluginRunner.setPlugin( new ShedClusterPlugin() );
                Thread myThready = new Thread( pluginRunner );
                myThready.start();
            }
        };

        final Action shedPainter = new AbstractAction( "Shed paint" ) {
            @Override
            public void actionPerformed( ActionEvent e ) {
                pluginRunner.setPlugin( new ShedPainterPlugin() );
                Thread myThready = new Thread( pluginRunner );
                myThready.start();
            }
        };

        final Action histogram = new AbstractAction( "Histogram" ) {
            @Override
            public void actionPerformed( ActionEvent e ) {
                pluginRunner.setPlugin( new HistogramPlugin() );
                Thread myThready = new Thread( pluginRunner );
                myThready.start();
            }
        };

        final Action hough = new AbstractAction( "Hough transform" ) {
            @Override
            public void actionPerformed( ActionEvent e ) {
                pluginRunner.setPlugin( new HoughTransformPlugin() );
                Thread myThready = new Thread( pluginRunner );
                myThready.start();
            }
        };

        final Action ols = new AbstractAction( "Ordinary least squares" ) {
            @Override
            public void actionPerformed( ActionEvent e ) {
                pluginRunner.setPlugin( new OLSPlugin() );
                Thread myThready = new Thread( pluginRunner );
                myThready.start();
            }
        };

        final Action canny = new AbstractAction( "Canny" ) {
            @Override
            public void actionPerformed( ActionEvent e ) {
                pluginRunner.setPlugin( new CannyPlugin() );
                Thread myThready = new Thread( pluginRunner );
                myThready.start();
            }
        };

        final Action roadBuilder = new AbstractAction( "Road builder" ) {
            @Override
            public void actionPerformed( ActionEvent e ) {
                pluginRunner.setPlugin( new Builder() );
                Thread myThready = new Thread( pluginRunner );
                myThready.start();
            }
        };

        final Action matting = new AbstractAction( "Image matting" ) {
            @Override
            public void actionPerformed( ActionEvent e ) {
                pluginRunner.setPlugin( new ImageMattingPlugin() );
                Thread myThready = new Thread( pluginRunner );
                myThready.start();
            }
        };

        final Action test = new AbstractAction( "Test" ) {
            @Override
            public void actionPerformed( ActionEvent e ) {
                pluginRunner.setPlugin( new TestPlugin() );
                Thread myThready = new Thread( pluginRunner );
                myThready.start();
            }
        };


         /*
        final Action snakeMove = new AbstractAction("Movement of a  Snake") {
            @Override
            public void actionPerformed(ActionEvent e) {
                pluginRunner.setPlugin(new MovementOfSnake());
                Thread myThready = new Thread(pluginRunner);
                myThready.start();
            }
            @Override
            public void actionPerformed(ActionEvent e) {
                final ImageJPanel panel = Linox.getInstance().getImageStore().getSelectedTab();
                final ImagePlus image = panel.image.duplicate();

                panel.setButton(new AbstractAction("Paint line") {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        panel.setMouseListener(new MouseAdapter() {
                            @Override
                            public void mouseDragged(MouseEvent e) {
                                if (panel.getMousePressed()) {
                                    image.getProcessor().set(e.getX(), e.getY(), Color.RED.getRGB());
                                    panel.imageView.setIcon(new ImageIcon(image.getBufferedImage()));
                                    int id = e.getX() + e.getY() * image.getWidth();
                                    DataCollection.INSTANCE.addPointToLine(id);
                                }
                            }

                            @Override
                            public void mousePressed(MouseEvent e) {
                                panel.setMousePressed(true);
                                DataCollection.INSTANCE.newLine();
                            }

                            @Override
                            public void mouseReleased(MouseEvent e) {
                                panel.setMousePressed(false);
                                panel.resetMouseMotionListener();

                                panel.setButton(new AbstractAction("Move snake") {
                                    @Override
                                    public void actionPerformed(ActionEvent e) {
                                        pluginRunner.setPlugin(new MovementOfSnake());
                                        Thread myThready = new Thread(pluginRunner);
                                        myThready.start();
                                        panel.removeButton();
                                        panel.imageView.setIcon(new ImageIcon(panel.image.getBufferedImage()));
                                        (Linox.getInstance().getImageStore()).addImageTab(image.getTitle(), image.duplicate());
                                        image.close();
                                    }
                                }, true);
                            }
                        });
                    }
                }, true);

            }
        };
           final Action ols = new AbstractAction("Ordinary Least Squares") {
            @Override
            public void actionPerformed(ActionEvent e) {
                final ImageJPanel panel = Linox.getInstance().getImageStore().getSelectedTab();
                final ImagePlus image = panel.image.duplicate();

                panel.setButton(new AbstractAction("Paint line") {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        panel.setMouseListener(new MouseAdapter() {
                            @Override
                            public void mouseDragged(MouseEvent e) {
                                if (panel.getMousePressed()) {
                                    image.getProcessor().set(e.getX(), e.getY(), Color.RED.getRGB());
                                    panel.imageView.setIcon(new ImageIcon(image.getBufferedImage()));
                                    int id = e.getX() + e.getY() * image.getWidth();
                                    DataCollection.INSTANCE.addPointToLine(id);
                                }
                            }

                            @Override
                            public void mousePressed(MouseEvent e) {
                                panel.setMousePressed(true);
                                DataCollection.INSTANCE.newLine();
                            }

                            @Override
                            public void mouseReleased(MouseEvent e) {
                                panel.setMousePressed(false);
                                panel.resetMouseMotionListener();

                                panel.setButton(new AbstractAction("Regress") {
                                    @Override
                                    public void actionPerformed(ActionEvent e) {
                                        pluginRunner.setPlugin(new OLS());
                                        Thread myThready = new Thread(pluginRunner);
                                        myThready.start();
                                        panel.removeButton();
                                        panel.imageView.setIcon(new ImageIcon(panel.image.getBufferedImage()));
                                        (Linox.getInstance().getImageStore()).addImageTab(image.getTitle(), image.duplicate());
                                        image.close();
                                    }
                                }, true);
                            }
                        });
                    }
                }, true);

            }
        };

        final Action thickening = new AbstractAction("Line Thickening") {
            @Override
            public void actionPerformed(ActionEvent e) {
                final ImageJPanel panel = Linox.getInstance().getImageStore().getSelectedTab();
                final ImagePlus image = panel.image.duplicate();

                panel.setButton(new AbstractAction("Paint line") {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        panel.setMouseListener(new MouseAdapter() {
                            @Override
                            public void mouseDragged(MouseEvent e) {
                                if (panel.getMousePressed()) {
                                    image.getProcessor().set(e.getX(), e.getY(), Color.RED.getRGB());
                                    panel.imageView.setIcon(new ImageIcon(image.getBufferedImage()));
                                    int id = e.getX() + e.getY() * image.getWidth();
                                    DataCollection.INSTANCE.addPointToLine(id);
                                }
                            }

                            @Override
                            public void mousePressed(MouseEvent e) {
                                panel.setMousePressed(true);
                                DataCollection.INSTANCE.newLine();
                            }

                            @Override
                            public void mouseReleased(MouseEvent e) {
                                panel.setMousePressed(false);
                                panel.resetMouseMotionListener();

                                panel.setButton(new AbstractAction("Line thickening") {
                                    @Override
                                    public void actionPerformed(ActionEvent e) {
                                        pluginRunner.setPlugin(new LineThickening());
                                        Thread myThready = new Thread(pluginRunner);
                                        myThready.start();
                                        panel.removeButton();
                                        panel.imageView.setIcon(new ImageIcon(panel.image.getBufferedImage()));
                                        (Linox.getInstance().getImageStore()).addImageTab(image.getTitle(), image.duplicate());
                                        image.close();
                                    }
                                }, true);
                            }
                        });
                    }
                }, true);

            }
        };

        final Action test1 = new AbstractAction("Test closing and opening") {
            @Override
            public void actionPerformed(ActionEvent e) {
                Linox.getInstance().setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
                try {
                    String dir =  System.getProperty("user.dir") + "/resource/test_1";
                    File[] list = new File(dir).listFiles();
                    String dir_r =  dir + "/morf/";;
                    if (list != null) {
                        for (int i = 0; i < list.length; i++) {
                            if(!list[i].isFile() || list[i].getName().contains("r_o") || list[i].getName().contains("r_c") || list[i].getName().contains(".DS_Store"))
                                continue;

                            String name = list[i].getName().substring(0, list[i].getName().indexOf('.'));
                            String extension = list[i].getName().substring(list[i].getName().indexOf('.'));
                            loadImage(list[i]);

                            int criteria = 5000;
                            int size = DataCollection.INSTANCE.getImageOriginal().getWidth() *  DataCollection.INSTANCE.getImageOriginal().getHeight();

                            AreaOpening plugin = new AreaOpening();
                            plugin.setCriteria(Math.min(size, criteria));
                            plugin.initImage(DataCollection.INSTANCE.getImageOriginal().getProcessor().duplicate());
                            plugin.run();
                            if(plugin.exit()) {
                                return;
                            }
                            DataCollection.INSTANCE.setImageResult(plugin.getResult(true));
                            IJ.save(DataCollection.INSTANCE.getImageResult(), dir_r + name + "_opening_" + criteria + extension);

                            AreaClosing plugin2 = new AreaClosing();
                            plugin2.setCriteria(Math.min(size, criteria));
                            plugin2.initImage(DataCollection.INSTANCE.getImageOriginal().getProcessor());
                            plugin2.run();
                            if(plugin2.exit()) {
                                return;
                            }
                            DataCollection.INSTANCE.setImageResult(plugin2.getResult(true));
                            IJ.save(DataCollection.INSTANCE.getImageResult(), dir_r + name + "_closing_" + criteria + extension);

                            DataCollection.INSTANCE.getImageOriginal().close();
                            DataCollection.INSTANCE.getImageResult().close();
                        }
                    }
                } finally {
                    Linox.getInstance().setCursor(Cursor.getDefaultCursor());
                }
            }
        };

        final Action test2 = new AbstractAction("Test homotopy") {
            @Override
            public void actionPerformed(ActionEvent e) {
                Linox.getInstance().setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
                try {
                    String dir =  System.getProperty("user.dir") + "/resource/test_1/morf";
                    File[] list = new File(dir).listFiles();
                    String dir_r =   System.getProperty("user.dir") + "/resource/test_1/homotopy/";
                    if (list != null) {
                        for (int i = 0; i < list.length; i++) {

                            if(!list[i].isFile() || list[i].getName().contains(".DS_Store"))
                                continue;

                            String name = list[i].getName().substring(0, list[i].getName().indexOf('.'));
                            String extension = list[i].getName().substring(list[i].getName().indexOf('.'));
                            loadImage(list[i]);

                            HomotopyFilter plugin = new HomotopyFilter();
                            plugin.setAreaSizeX(13);
                            plugin.setAreaSizeY(13);
                            plugin.setDeviation(10);
                            plugin.initImage(DataCollection.INSTANCE.getImageOriginal().getProcessor().duplicate());
                            plugin.run();
                            if(plugin.exit()) {
                                return;
                            }

                            DataCollection.INSTANCE.setImageResult(plugin.getResult(true));
                            IJ.save(DataCollection.INSTANCE.getImageResult(), dir_r + name + "_hom" + extension);


                            DataCollection.INSTANCE.getImageOriginal().close();
                            DataCollection.INSTANCE.getImageResult().close();
                        }
                    }
                } finally {
                    Linox.getInstance().setCursor(Cursor.getDefaultCursor());
                }
            }
        };

        */

        items.add( new JMenuItem( resize ) );
        items.add( new JMenuItem( compress ) );
        items.add( new JMenuItem( grayscale ) );
        items.add( new JMenuItem( gradient ) );
        items.add( new JMenuItem( laplasian ) );
        items.add( new JMenuItem( homotopy ) );
        items.add( new JMenuItem( median ) );
        items.add( new JMenuItem( morphologyTransformation ) );
        items.add( new JMenuItem( morphology ) );
        items.add( new JMenuItem( morphologyCompilation ) );
        items.add( new JMenuItem( watershed ) );
        items.add( new JMenuItem( watershedSegmenter ) );
        items.add( new JMenuItem( shedCluster ) );
        items.add( new JMenuItem( shedPainter ) );
        items.add( new JMenuItem( histogram ) );
        items.add( new JMenuItem( hough ) );
        items.add( new JMenuItem( ols ) );
        items.add( new JMenuItem( canny ) );
        items.add( new JMenuItem( roadBuilder ) );
        items.add( new JMenuItem( matting ) );
        items.add( new JMenuItem( test ) );

        for ( JMenuItem item : items ) {
            item.setEnabled( false );
        }

        /*JMenuItem item = new JMenuItem(test1);
        item.setEnabled(true);
        items.add(item);

        item = new JMenuItem(test2);
        item.setEnabled(true);
        items.add(item);*/

    }

    private void loadImage( File file ) {
//        ij.ImagePlus img =   IJ.openImage(file.getPath());
//        DataCollection.INSTANCE.setImageOriginal(new ImagePlus(file.getName(), img.getProcessor().convertToRGB()));
    }
}
