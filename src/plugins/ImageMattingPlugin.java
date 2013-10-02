package plugins;

import entities.Vertex;
import gui.Linox;
import gui.dialog.ParameterButton;
import gui.dialog.ParameterJPanel;
import gui.dialog.ParameterSlider;
import gui.menu.ImageJPanel;
import org.opencv.core.*;
import plugins.dijkstra.Dijkstra;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.PriorityQueue;

public class ImageMattingPlugin extends AbstractPlugin {
    ParameterSlider widthSlider;
    ParameterButton foregroundButton, backgroundButton;
    ArrayList<Point> fpoints, bpoints;
    HashMap<entities.Point, Mat> W, L;

    public ImageMattingPlugin() {
        title = "Image matting";
    }

    @Override
    public void run() {
        Linox.getInstance().getStatusBar().setProgress( title, 0, 100 );
        // calculate();
        showParamsPanel( "Choose params" );
        if ( exit ) {
            return;
        }
    }

    private void calculate() {
        Mat gray = GrayscalePlugin.run( image, false );

        W = new HashMap<>();
        L = new HashMap<>();
        double b = 0;
        for ( int row_i = 0; row_i < gray.rows(); row_i++ ) {
            for ( int col_i = 0; col_i < gray.cols(); col_i++ ) {
                int gi = ( int ) gray.get( row_i, col_i )[0];
                entities.Point p = new entities.Point( col_i, row_i );
                Mat m = new Mat( image.size(), gray.type() );
                for ( int row_j = 0; row_j < gray.rows(); row_j++ ) {
                    for ( int col_j = 0; col_j < gray.cols(); col_j++ ) {
                        int gj = ( int ) gray.get( row_j, col_j )[0];
                        double g = Math.pow( gi - gj, 2 );
                        b += Math.pow( g, 2 );
                        m.put( row_j, col_j, g );
                    }
                }
                W.put( p, m );
            }
        }
        b = b / ( image.total() * 2 );
        Mat D = new Mat( image.size(), image.type() );
        for ( Map.Entry<entities.Point, Mat> entry : W.entrySet() ) {
            Mat m = entry.getValue();
            double d = 0;
            for ( int row = 0; row < m.rows(); row++ ) {
                for ( int col = 0; col < m.cols(); col++ ) {
                    double value = m.get( row, col )[0] / b;
                    m.put( row, col, value );
                    d += value;
                }
            }
            D.put( ( int ) entry.getKey().y, ( int ) entry.getKey().x, d );
        }

        for ( int row_i = 0; row_i < gray.rows(); row_i++ ) {
            for ( int col_i = 0; col_i < gray.cols(); col_i++ ) {
                entities.Point p = new entities.Point( col_i, row_i );
                Mat m = new Mat( image.size(), image.type() );
                Rect rect = new Rect( Math.max( 0, col_i - 1 ), Math.max( 0, row_i - 1 ), 3, 3 );
                for ( int row_j = 0; row_j < gray.rows(); row_j++ ) {
                    for ( int col_j = 0; col_j < gray.cols(); col_j++ ) {
                        Point pp = new Point( col_j, row_j );
                        if ( p.equals( pp ) ) {
                            m.put( row_j, col_j, D.get( row_i, col_i )[0] );
                        } else if ( rect.contains( pp ) ) {
                            m.put( row_j, col_j, -W.get( p ).get( row_j, col_j )[0] );
                        } else {
                            m.put( row_j, col_j, 0 );
                        }
                    }
                }
                L.put( p, m );
            }
        }
    }

    public Mat matting() {
        Point p = new Point( 0, 0 );
        HashMap<entities.Point, Vertex> vertexList = Dijkstra.createVertexes( image, W );
        PriorityQueue<Point> fQueue = new PriorityQueue<>( fpoints );
        while ( !fQueue.isEmpty() ) {
            Point point = fQueue.poll();
            Dijkstra.computePaths( vertexList.get( point ), vertexList );

            Vertex closesVertex = new Vertex( new entities.Point( -1, -1 ) );
            closesVertex.minDistance = Double.POSITIVE_INFINITY;
            for ( Point fp : fpoints ) {
                Vertex v = vertexList.get( fp );
                if ( v.minDistance < closesVertex.minDistance ) {
                    closesVertex = v;
                }
            }

        }

        return image;
    }


    @Override
    public void getParams( ParameterJPanel jpanel ) {
        ImageJPanel panel = Linox.getInstance().getImageStore().getSelectedTab();
        panel.resetMouseMotionListener();
        fpoints = jpanel.getValueButton( foregroundButton );
        bpoints = jpanel.getValueButton( backgroundButton );

        if ( fpoints.size() == 0 || bpoints.size() == 0 ) {
            errMessage = "Empty points!";
            pluginListener.stopPlugin();
            return;
        }

        result = matting();

        if ( tabs == 0 ) {
            pluginListener.addImageTab();
            tabs++;
        } else {
            pluginListener.replaceImageTab();
        }
    }

    protected void showParamsPanel( String name ) {
        ImageJPanel srcPanel = Linox.getInstance().getImageStore().getSelectedTab();
        final Mat _image = srcPanel.image.clone();
        pluginListener.addImageTab( title, _image );
        tabs++;
        final ImageJPanel panel = Linox.getInstance().getImageStore().getSelectedTab();

        final Scalar fcolor = new Scalar( 0, 0, 255, 0 );
        final Scalar bcolor = new Scalar( 255, 0, 0, 0 );

        widthSlider = new ParameterSlider( "Width of brash:", 1, 50, 10 );
        foregroundButton = new ParameterButton( new AbstractAction( "Paint foreground" ) {
            @Override
            public void actionPerformed( ActionEvent e ) {
                panel.pButton = foregroundButton;
                panel.pSlider = widthSlider;
                panel.setMouseListener( new MouseAdapter() {
                    @Override
                    public void mouseDragged( MouseEvent e ) {
                        if ( panel.pButton.getMousePressed() ) {
                            Point p = new Point( e.getX(), e.getY() );
                            Core.line( _image, p, p, fcolor, panel.pSlider.getValue() );
                            pluginListener.replaceImageTab( title, _image );
                            panel.pButton.addPoint( p, panel.pSlider.getValue(), fcolor, _image );
                        }
                    }

                    @Override
                    public void mousePressed( MouseEvent e ) {
                        panel.pButton.setMousePressed( true );
                        //  DataCollection.INSTANCE.newLine();
                    }

                    @Override
                    public void mouseReleased( MouseEvent e ) {
                        panel.pButton.setMousePressed( false );
                    }
                } );
            }
        } );
        backgroundButton = new ParameterButton( new AbstractAction( "Paint background" ) {
            @Override
            public void actionPerformed( ActionEvent e ) {
                panel.pButton = backgroundButton;
                panel.pSlider = widthSlider;
                panel.setMouseListener( new MouseAdapter() {
                    @Override
                    public void mouseDragged( MouseEvent e ) {
                        if ( panel.pButton.getMousePressed() ) {
                            Point p = new Point( e.getX(), e.getY() );
                            Core.line( _image, p, p, bcolor, panel.pSlider.getValue() );
                            pluginListener.replaceImageTab( title, _image );
                            panel.pButton.addPoint( p, panel.pSlider.getValue(), bcolor, _image );
                        }
                    }

                    @Override
                    public void mousePressed( MouseEvent e ) {
                        panel.pButton.setMousePressed( true );
                        //  DataCollection.INSTANCE.newLine();
                    }

                    @Override
                    public void mouseReleased( MouseEvent e ) {
                        panel.pButton.setMousePressed( false );
                    }
                } );
            }
        } );

        ParameterJPanel jpanel = new ParameterJPanel( name, this );
        jpanel.addParameterSlider( widthSlider );
        jpanel.addParameterButton( foregroundButton );
        jpanel.addParameterButton( backgroundButton );

        Linox.getInstance().addParameterJPanel( jpanel );
    }
}
