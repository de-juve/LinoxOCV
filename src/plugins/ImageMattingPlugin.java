package plugins;

import entities.Connection;
import entities.DataCollector;
import entities.PixelsMentor;
import entities.Point;
import gui.Linox;
import gui.dialog.ParameterButton;
import gui.dialog.ParameterJPanel;
import gui.dialog.ParameterSlider;
import gui.menu.ImageJPanel;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.Scalar;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Queue;

//import org.opencv.core.Point;

public class ImageMattingPlugin extends AbstractPlugin {
    ParameterSlider widthSlider;
    ParameterButton foregroundButton, backgroundButton;

    HashMap<Point, Mat> W, L;
    ArrayList<Connection> connections;
    ArrayList<Point> fpoints1, fpoints, bpoints, fEdgePoints, bEdgePoints;
    boolean[] fpoints_b, bpoints_b;
    double b = 1 / Math.pow( 255, 2 );
    Mat gray;

    public ImageMattingPlugin() {
        title = "Image matting";
    }

    @Override
    public void run() {
        Linox.getInstance().getStatusBar().setProgress( title, 0, 100 );

        showParamsPanel( "Choose params" );
        if ( exit ) {
            return;
        }
    }

    public Mat matting() {
        //calculate();
        gray = GrayscalePlugin.run( image, false );


        bEdgePoints = new ArrayList<>();

        //1 этап выделение с порогом и формирование фона около интересующего объекта
        stage1();
        result = image.clone();
        for ( Point fp : fpoints ) {
            result.put( fp.y, fp.x, 0, 0, 255 );
        }
        DataCollector.INSTANCE.addtoHistory( "foreground", result );
        //2 этап выделение с порогом и формирование объектов фона
        stage2();
        result = image.clone();
        for ( Point fp : bpoints ) {
            result.put( fp.y, fp.x, 255, 0, 0 );
        }
        DataCollector.INSTANCE.addtoHistory( "background", result );
        //3 этап выделение с сравнением между фоном и интресующим объектом
        stage3();

        result = image.clone();
        for ( Point fp : fpoints ) {
            result.put( fp.y, fp.x, 0, 0, 255 );
        }
        for ( Point fp : bpoints ) {
            result.put( fp.y, fp.x, 255, 0, 0 );
        }


        return result;
    }

    private void stage1() {
        fpoints_b = new boolean[( int ) image.total()];
        fEdgePoints = new ArrayList<>();
        extractEdgePoints( fEdgePoints, fpoints, fpoints_b );

        Queue<Point> fQueue = new LinkedList<>( fEdgePoints );
        ArrayList<Point> sample, neighs;
        sample = new ArrayList<>();
        while ( !fQueue.isEmpty() ) {
            Point fp = fQueue.poll();
            neighs = PixelsMentor.getNeighborhoodOfPixel( fp.x, fp.y, image, 1 );
            for ( Point np : neighs ) {
                if ( fpoints_b[id( np.x, np.y )] ) {
                    continue;
                }
                if ( sample.isEmpty() ) {
                    sample = generateSample( np, fpoints_b );
                }
                double w = 0;
                for ( Point sp : sample ) {
                    sp.weight = weight( np, sp, gray );// / distance( np, sp );
                    w += sp.weight;
                }
                np.fDegree = w;
                if ( np.fDegree * 100 / sample.size() > 95 ) {
                    fpoints.add( np );
                    fpoints_b[id( np.x, np.y )] = true;
                    addEdgePoint( np, fEdgePoints, fpoints_b );
                    fQueue.add( np );
                } else {
                    bpoints.add( np );
                }
            }
        }
    }

    private void stage2() {
        bpoints_b = new boolean[( int ) image.total()];
        bEdgePoints = new ArrayList<>();
        extractEdgePoints( bEdgePoints, bpoints, bpoints_b );

        Queue<Point> fQueue = new LinkedList<>( bEdgePoints );
        ArrayList<Point> sample, neighs;
        sample = new ArrayList<>();
        while ( !fQueue.isEmpty() ) {
            Point fp = fQueue.poll();
            neighs = PixelsMentor.getNeighborhoodOfPixel( fp.x, fp.y, image, 1 );
            for ( Point np : neighs ) {
                if ( bpoints_b[id( np.x, np.y )] || fpoints_b[id( np.x, np.y )] ) {
                    continue;
                }
                if ( sample.isEmpty() ) {
                    sample = generateSample( np, bpoints_b );
                }
                double w = 0;
                for ( Point sp : sample ) {
                    sp.weight = weight( np, sp, gray );// / distance( np, sp );
                    w += sp.weight;
                }
                np.bDegree = w;
                if ( np.bDegree * 100 / sample.size() > 95 ) {
                    if ( fpoints_b[id( np.x, np.y )] && np.bDegree > np.fDegree ) {
                        fpoints.remove( np );
                        fpoints_b[id( np.x, np.y )] = false;
                    }
                    bpoints.add( np );
                    bpoints_b[id( np.x, np.y )] = true;
                    addEdgePoint( np, bEdgePoints, bpoints_b );
                    fQueue.add( np );
                }
            }
        }
    }

    private void stage3() {
        fpoints_b = new boolean[( int ) image.total()];
        bpoints_b = new boolean[( int ) image.total()];
        fEdgePoints = new ArrayList<>();
        bEdgePoints = new ArrayList<>();
        fpoints = fpoints1;

        extractEdgePoints( fEdgePoints, fpoints, fpoints_b );
        extractEdgePoints( bEdgePoints, bpoints, bpoints_b );

        Queue<Point> fQueue = new LinkedList<>( fEdgePoints );
        ArrayList<Point> fSample, bSample, neighs;
        Point cfp, bfp;
        cfp = bfp = fEdgePoints.get( 0 );
        fSample = new ArrayList<>();
        bSample = new ArrayList<>();
        while ( !fQueue.isEmpty() ) {
            Point fp = fQueue.poll();
            neighs = PixelsMentor.getNeighborhoodOfPixel( fp.x, fp.y, image, 1 );
            for ( Point np : neighs ) {
                if ( fpoints_b[id( np.x, np.y )] ) {
                    continue;
                }
                if ( fSample.isEmpty() ) {
                    fSample = generateSample( np, fpoints_b );
                    bSample = generateSample( np, bpoints_b );
                    cfp = closesPoint( np, fpoints_b );
                    bfp = closesPoint( np, bpoints_b );
                }
                double w = 0;
                for ( Point sp : fSample ) {
                    sp.weight = weight( np, sp, gray );// / distance( np, sp );
                    w += sp.weight;
                }
                np.fDegree = w;
                w = 0;
                for ( Point sp : bSample ) {
                    sp.weight = weight( np, sp, gray );// / distance( np, sp );
                    w += sp.weight;
                }
                np.bDegree = w;
                if ( np.fDegree > np.bDegree || ( np.fDegree * 100 / fSample.size() > 95 && distance( np, cfp ) < distance( np, bfp ) ) ) {
                    fpoints.add( np );
                    fpoints_b[id( np.x, np.y )] = true;
                    addEdgePoint( np, fEdgePoints, fpoints_b );
                    fQueue.add( np );
                } else {
                    bpoints.add( np );
                    bpoints_b[id( np.x, np.y )] = true;
                    addEdgePoint( np, bEdgePoints, bpoints_b );
                }
            }
        }

    }

    private void addEdgePoint( Point p, ArrayList<Point> points, boolean[] points_b ) {
        if ( isEdgePoint( p, points_b ) ) {
            ArrayList<Point> neighs = PixelsMentor.getNeighborhoodOfPixel( p.x, p.y, image, 2 );
            for ( Point np : neighs ) {
                if ( points_b[id( np.x, np.y )] && !isEdgePoint( np, points_b ) ) {
                    points.remove( np );
                }
            }
            points.add( p );
        }
    }

    private ArrayList<Point> generateSample( Point p, boolean[] points_b ) {
        ArrayList<Point> sample = new ArrayList<>();
        Point cp = closesPoint( p, points_b );
        Queue<Point> queue = new LinkedList<>();
        queue.add( cp );
        while ( !queue.isEmpty() && sample.size() < 20 ) {
            cp = queue.poll();
            if ( !sample.contains( cp ) ) {
                sample.add( cp );
            }

            for ( Connection con : cp.connections ) {
                if ( !sample.contains( con.p2 ) && !queue.contains( con.p2 ) ) {
                    queue.add( con.p2 );
                }
            }
        }
        return sample;
    }

    private void extractEdgePoints( ArrayList<Point> edgePoints, ArrayList<Point> points, boolean[] points_b ) {

        Mat img = image.clone();
        for ( Point fp : points ) {
            points_b[id( fp.x, fp.y )] = true;
        }
        for ( Point fp : points ) {
            if ( isEdgePoint( fp, points_b ) ) {
                edgePoints.add( fp );
                img.put( fp.y, fp.x, 0, 255, 0 );
            }
        }
        DataCollector.INSTANCE.addtoHistory( "edge points", img );
        for ( Point p : edgePoints ) {
            for ( Point n : edgePoints ) {
                if ( PixelsMentor.isNeighbours( p, n ) && !p.equals( n ) ) {
                    p.addConnection( new Connection( p, n, distance( p, n ) ) );
                }
            }
        }
    }

    private boolean isEdgePoint( Point p, boolean[] points_b ) {
        ArrayList<Point> neighs = PixelsMentor.getNeighborhoodOfPixel( p.x, p.y, image, 1 );
        for ( Point n : neighs ) {
            if ( !points_b[id( n.x, n.y )] ) {
                return true;
            }
        }
        return false;
        // return  !fpoints.containsAll( neighs );
    }

    private double distance( Point p, Point n ) {
        return Math.sqrt( Math.pow( p.x - n.x, 2 ) + Math.pow( p.y - n.y, 2 ) );
    }

    private double weight( Point p, Point n, Mat img ) {
        return Math.exp( -b * Math.pow( img.get( p.y, p.x )[0] - img.get( n.y, n.x )[0], 2 ) );
    }

    private Point closesPoint( Point p, boolean[] points_b ) {
        Point cp = p;
        double d = Double.POSITIVE_INFINITY;
        ArrayList<Point> neighs = PixelsMentor.getNeighborhoodOfPixel( p.x, p.y, image, 2 );
        for ( Point ep : neighs ) {
            if ( isEdgePoint( ep, points_b ) && distance( p, ep ) < d && ep.connections.size() > 0 ) {
                d = distance( p, ep );
                cp = ep;
            }
        }
     /*   for ( Point ep : edgePoints ) {
            if ( distance( p, ep ) < d && ep.connections.size() > 0 ) {
                d = distance( p, ep );
                cp = ep;
            }
        }*/
        return cp;
    }

    private void calculate() {
        Mat gray = GrayscalePlugin.run( image, false );
        connections = new ArrayList<>();
        W = new HashMap<>();
        L = new HashMap<>();
        double b = 0;
        for ( int row_i = 0; row_i < gray.rows(); row_i++ ) {
            for ( int col_i = 0; col_i < gray.cols(); col_i++ ) {
                int gi = ( int ) gray.get( row_i, col_i )[0];
                Point p = new Point( col_i, row_i );
                ArrayList<Point> neighs = PixelsMentor.getNeighborhoodOfPixel( p.x, p.y, gray, 1 );
                for ( Point np : neighs ) {
                    if ( np.y < p.y || ( np.y == p.y && np.x <= p.x ) ) {
                        continue;
                    }
                    int gj = ( int ) gray.get( np.y, np.x )[0];
                    double g = Math.pow( gi - gj, 2 );
                    Connection conn = new Connection( p, np, g );
                    // if(!connections.contains( conn )) {
                    connections.add( conn );
                    //}
                }

            /*    Mat m = new Mat( image.size(), gray.type() );
                for ( int row_j = 0; row_j < gray.rows(); row_j++ ) {
                    for ( int col_j = 0; col_j < gray.cols(); col_j++ ) {
                        int gj = ( int ) gray.get( row_j, col_j )[0];
                        double g = Math.pow( gi - gj, 2 );
                        b += Math.pow( g, 2 );
                        m.put( row_j, col_j, g );
                    }
                }
                W.put( p, m );*/
            }
        }
        b = b / ( image.total() * 2 );
       /* Mat D = new Mat( image.size(), image.type() );
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
            D.put( entry.getKey().y, entry.getKey().x, d );
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
        }*/
    }


    @Override
    public void getParams( ParameterJPanel jpanel ) {
        Linox.getInstance().getImageStore().setCursor( new Cursor( Cursor.WAIT_CURSOR ) );
        ImageJPanel panel = Linox.getInstance().getImageStore().getSelectedTab();
        panel.resetMouseMotionListener();
        fpoints = jpanel.getValueButton( foregroundButton );
        fpoints1 = new ArrayList<>( fpoints );
        bpoints = new ArrayList<>();
//        bpoints = jpanel.getValueButton( backgroundButton );

        if ( fpoints.size() == 0 ) { //&& bpoints.size() == 0 ) {
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
        pluginListener.addImageTab( title, srcPanel.image.clone() );
        tabs++;

        final Scalar fcolor = new Scalar( 0, 0, 255, 0 );
        final Scalar bcolor = new Scalar( 255, 0, 0, 0 );

        widthSlider = new ParameterSlider( "Width of brash:", 1, 50, 5 );
        foregroundButton = new ParameterButton( new AbstractAction( "Paint foreground" ) {
            Mat _image;
            ImageJPanel panel;

            @Override
            public void actionPerformed( ActionEvent e ) {
                panel = Linox.getInstance().getImageStore().getSelectedTab();
                _image = panel.image.clone();
                panel.pButton = foregroundButton;
                panel.pSlider = widthSlider;
                Linox.getInstance().getImageStore().setCursor( new Cursor( Cursor.DEFAULT_CURSOR ) );
                panel.setMouseListener( new MouseAdapter() {
                    @Override
                    public void mouseDragged( MouseEvent e ) {
                        if ( panel.pButton.getMousePressed() ) {
                            org.opencv.core.Point p = new org.opencv.core.Point( e.getX(), e.getY() );
                            Core.line( _image, p, p, fcolor, panel.pSlider.getValue() );
                            pluginListener.replaceImageTab( title, _image );
                            panel.pButton.addPoint( new Point( e.getX(), e.getY() ), panel.pSlider.getValue(), fcolor, _image );
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
            Mat _image;
            ImageJPanel panel;

            @Override
            public void actionPerformed( ActionEvent e ) {
                panel = Linox.getInstance().getImageStore().getSelectedTab();
                _image = panel.image.clone();
                panel.pButton = backgroundButton;
                panel.pSlider = widthSlider;
                Linox.getInstance().getImageStore().setCursor( new Cursor( Cursor.DEFAULT_CURSOR ) );
                panel.setMouseListener( new MouseAdapter() {
                    @Override
                    public void mouseDragged( MouseEvent e ) {
                        if ( panel.pButton.getMousePressed() ) {
                            org.opencv.core.Point p = new org.opencv.core.Point( e.getX(), e.getY() );
                            Core.line( _image, p, p, bcolor, panel.pSlider.getValue() );
                            pluginListener.replaceImageTab( title, _image );
                            panel.pButton.addPoint( new Point( e.getX(), e.getY() ), panel.pSlider.getValue(), bcolor, _image );
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
        // jpanel.addParameterButton( backgroundButton );

        Linox.getInstance().addParameterJPanel( jpanel );
    }
}
