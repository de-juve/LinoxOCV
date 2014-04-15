package plugins;

import entities.*;
import gui.Linox;
import org.opencv.core.Mat;
import plugins.morphology.MorphologyPlugin;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.TimeUnit;

public class WatershedPlugin extends AbstractPlugin {
    Mat gray;
    int[] lowerCompletion, shedLabels;
    TreeMap<Integer, ArrayList<Integer>> steepestNeighbours;
    boolean[] maximum;
    final Point N = new Point( -1, -1 );

    public WatershedPlugin() {
        title = "Watershed";
    }

    @Override
    public void run() {
        long start = System.nanoTime();
        print(this.title + " begin");
        Linox.getInstance().getStatusBar().setProgress(title, 0, 100);

        GrayscalePlugin.run( image, true );

        MorphologyPlugin mp = new MorphologyPlugin();
        mp.initImage( DataCollector.INSTANCE.getGrayImg() );
        mp.run( "Closing", 1 );
        result = mp.result;

        LowerCompletePlugin lcp = new LowerCompletePlugin();
        lcp.initImage( result );
        lcp.run();

        constructDAG();
        flood();

        removeOnePixelHoles();

        DataCollector.INSTANCE.addtoHistory( "wsh", result );
        DataCollector.INSTANCE.setWatershedImg( result );

        //Imgproc.dilate( result, result, new Mat(), new org.opencv.core.Point( -1, -1 ), 1 );

        mp.initImage( result );
        mp.run( "Closing", 1 );
        result = mp.result;


        /*removeSeparateLines();
        removeOnePixelHoles();
        removeSeparatePoints();
        mp.initImage( result );
        mp.run( "Closing", 100 );
        result = mp.result;*/


        //WatershedPartition wp  = new WatershedPartition();
        //wp.partitionNetwork( result );

        Linox.getInstance().getStatusBar().setProgress( title, 100, 100 );
        long end = System.nanoTime();
        long traceTime = end-start;
        print(this.title + " finish: " + TimeUnit.MILLISECONDS.convert(traceTime, TimeUnit.NANOSECONDS));

        if ( pluginListener != null ) {
            pluginListener.addImageTab();
            pluginListener.finishPlugin();
        }
    }

    private void removeOnePixelHoles() {
        HashMap<Integer, Point> wpoints = DataCollector.INSTANCE.getWatershedPoints();
        boolean[] wpoints_b = new boolean[( int ) image.total()];
        int[] watershed = new int[( int ) image.total()];
        for ( Map.Entry<Integer, Point> p : wpoints.entrySet() ) {
            wpoints_b[p.getKey()] = true;
            watershed[p.getKey()] = 255;
        }
        for ( int y = 1; y < image.rows() - 1; y++ ) {
            for ( int x = 1; x < image.cols() - 1; x++ ) {
                if ( !wpoints_b[id( x, y )] &&
                        (
                                (
                                        ( wpoints_b[id( x - 1, y )] && wpoints_b[id( x + 1, y )] ) ||
                                                ( wpoints_b[id( x - 1, y - 1 )] && wpoints_b[id( x + 1, y )] ) ||
                                                ( wpoints_b[id( x - 1, y + 1 )] && wpoints_b[id( x + 1, y )] ) ||
                                                ( wpoints_b[id( x - 1, y )] && wpoints_b[id( x + 1, y - 1 )] ) ||
                                                ( wpoints_b[id( x - 1, y )] && wpoints_b[id( x + 1, y + 1 )] )
                                ) && !wpoints_b[id( x, y - 1 )] && !wpoints_b[id( x, y + 1 )]
                        ) ||
                        (
                                (
                                        wpoints_b[id( x, y - 1 )] && wpoints_b[id( x, y + 1 )] ||
                                                wpoints_b[id( x - 1, y - 1 )] && wpoints_b[id( x, y + 1 )] ||
                                                wpoints_b[id( x + 1, y - 1 )] && wpoints_b[id( x, y + 1 )] ||
                                                wpoints_b[id( x, y - 1 )] && wpoints_b[id( x - 1, y + 1 )] ||
                                                wpoints_b[id( x, y - 1 )] && wpoints_b[id( x + 1, y + 1 )]
                                ) &&
                                        !wpoints_b[id( x - 1, y )] && !wpoints_b[id( x + 1, y )]
                        ) ||
                        (
                                wpoints_b[id( x - 1, y - 1 )] && wpoints_b[id( x + 1, y + 1 )] &&
                                        !wpoints_b[id( x - 1, y + 1 )] && !wpoints_b[id( x + 1, y - 1 )]
                        ) ||
                        (
                                wpoints_b[id( x - 1, y + 1 )] && wpoints_b[id( x + 1, y - 1 )] &&
                                        !wpoints_b[id( x - 1, y - 1 )] && !wpoints_b[id( x + 1, y + 1 )]
                        ) ||
                        (
                                (
                                        wpoints_b[id( x - 1, y )] && wpoints_b[id( x + 1, y + 1 )] ||
                                                wpoints_b[id( x, y - 1 )] && wpoints_b[id( x + 1, y + 1 )]
                                ) &&
                                        !wpoints_b[id( x - 1, y + 1 )] && !wpoints_b[id( x + 1, y - 1 )] &&
                                        !wpoints_b[id( x, y + 1 )] && !wpoints_b[id( x + 1, y )]
                        ) ||
                        (
                                (
                                        wpoints_b[id( x - 1, y - 1 )] && wpoints_b[id( x, y + 1 )] ||
                                                wpoints_b[id( x - 1, y - 1 )] && wpoints_b[id( x + 1, y )]
                                ) &&
                                        !wpoints_b[id( x - 1, y + 1 )] && !wpoints_b[id( x + 1, y - 1 )] &&
                                        !wpoints_b[id( x - 1, y )] && !wpoints_b[id( x, y - 1 )]
                        ) ||
                        (
                                (
                                        wpoints_b[id( x - 1, y )] && wpoints_b[id( x + 1, y - 1 )] ||
                                                wpoints_b[id( x, y + 1 )] && wpoints_b[id( x + 1, y - 1 )]
                                ) &&
                                        !wpoints_b[id( x - 1, y - 1 )] && !wpoints_b[id( x + 1, y + 1 )] &&
                                        !wpoints_b[id( x, y - 1 )] && !wpoints_b[id( x + 1, y )]
                        ) ||
                        (
                                (
                                        wpoints_b[id( x - 1, y + 1 )] && wpoints_b[id( x + 1, y )] ||
                                                wpoints_b[id( x - 1, y + 1 )] && wpoints_b[id( x, y - 1 )]
                                ) &&
                                        !wpoints_b[id( x - 1, y - 1 )] && !wpoints_b[id( x + 1, y + 1 )] &&
                                        !wpoints_b[id( x - 1, y )] && !wpoints_b[id( x, y + 1 )]
                        ) ) {

                    wpoints_b[id( x, y )] = true;
                    watershed[id( x, y )] = 255;
                    wpoints.put( id( x, y ), new Point( x, y ) );
                }
            }
        }

        DataCollector.INSTANCE.setWatershedPoints( wpoints );
        result = setPointsToImage( watershed );
    }

    private void removeSeparateLines() {
        shedLabels = DataCollector.INSTANCE.getShedLabels();
        HashMap<Integer, Point> watershedPoints = DataCollector.INSTANCE.getWatershedPoints();
        ArrayList<Integer> shed = new ArrayList<>();
        Mat watershedImage = DataCollector.INSTANCE.getWatershedImg();

        for ( Point point : watershedPoints.values() ) {
            ArrayList<Integer> neigh = PixelsMentor.defineNeighboursIds( id( point ), image );
            ArrayList<Integer> area = new ArrayList<>();

            for ( Integer n : neigh ) {
                int shedLabel = shedLabels[n];
                if ( watershedImage.get( y( n ), x( n ) )[0] != 255 && !area.contains( shedLabel ) && shedLabel != shedLabels[id( point )] ) {
                    area.add( shedLabel );
                }
            }

            if ( area.size() == 1 ) {
                shed.add( id( point ) );
            }
        }
        for ( Integer id : shed ) {
            watershedPoints.remove( id );
            watershedImage.put( y( id ), x( id ), new double[]{ 0, 0, 0 } );
        }
        DataCollector.INSTANCE.setWatershedImg( watershedImage );
        DataCollector.INSTANCE.setWatershedPoints( watershedPoints );
        result = watershedImage;
    }

    private void removeSeparatePoints() {
        HashMap<Integer, Point> watershedPoints = DataCollector.INSTANCE.getWatershedPoints();
        ArrayList<Integer> points = new ArrayList<>();
        Mat watershedImage = DataCollector.INSTANCE.getWatershedImg();
        for ( Point point : watershedPoints.values() ) {
            ArrayList<Integer> neigh = PixelsMentor.defineNeighboursIdsWithSameValue( id( point ), watershedImage );
            if ( neigh.size() < 2 ) {
                points.add( id( point ) );
            }
        }
        for ( Integer id : points ) {
            watershedPoints.remove( id );
            watershedImage.put( y( id ), x( id ), new double[]{ 0, 0, 0 } );
        }
        DataCollector.INSTANCE.setWatershedImg( watershedImage );
        DataCollector.INSTANCE.setWatershedPoints( watershedPoints );
        result = watershedImage;
    }

    private void constructDAG() {
        maximum = new boolean[( int ) image.total()];
        steepestNeighbours = new TreeMap<>();
        gray = DataCollector.INSTANCE.getGrayImg();
        lowerCompletion = DataCollector.INSTANCE.getLowerCompletion();
        MassiveWorker.INSTANCE.sort( gray, lowerCompletion );
        shedLabels = DataCollector.INSTANCE.getShedLabels();

        //start from pixels with min lower completion
        ArrayList<Integer> ids = MassiveWorker.INSTANCE.getIds();

        for ( int i = ids.size() - 1; i > -1; i-- ) {
            int id = ids.get( i );

            ArrayList<Integer> neighbours = PixelsMentor.defineNeighboursIdsWithLowerValue( id, gray );
            if ( neighbours.isEmpty() ) {
                ArrayList<Integer> eqNeighbours = PixelsMentor.defineNeighboursIdsWithSameValue( id, gray );
                neighbours.clear();
                for ( Integer eq : eqNeighbours ) {
                    if ( lowerCompletion[eq] < lowerCompletion[id] ) {
                        neighbours.add( eq );
                    }
                }
            }
            if ( !neighbours.isEmpty() ) {
                maximum[id] = true;
                steepestNeighbours.remove( id );
                steepestNeighbours.put( id, neighbours );
                neighbouresNotMax( id );
            } else {
                //define canonical element of min region if it need and if we can
                maximum[id] = false;
                Shed shed = ShedCollector.INSTANCE.getShed( shedLabels[id] );
                Point canonical = shed.getCanonical();

                if ( canonical.equals( N ) ) {
                    canonical.x = x( id );
                    canonical.y = y( id );
                    shed.setCanonical( canonical );
                }
                ArrayList<Integer> ar = new ArrayList<>( 1 );
                ar.add( 0, id( canonical.x, canonical.y ) );
                steepestNeighbours.remove( id );
                steepestNeighbours.put( id, ar );
            }
        }
    }

    private void neighbouresNotMax( int id ) {
        int x = x( id );
        int y = y( id );
        ArrayList<Integer> neighboures = PixelsMentor.defineNeighboursOfPixel( x, y, image, 1 );//defineNeighboursIds(id, width, height);
        for ( Integer n : neighboures ) {
            int nx = x( n );
            int ny = y( n );
            if ( maximum[n] && ( gray.get( ny, nx )[0] < gray.get( y, x )[0] ||
                    ( gray.get( ny, nx )[0] == gray.get( y, x )[0] &&
                            lowerCompletion[n] < lowerCompletion[id] ) ) ) {
                maximum[n] = false;
                neighbouresNotMax( n );
            }
        }
    }

    //Recursive function for resolving the downstream paths of the lower complete graph
    //Returns representative element of pixel p, or W if p is a watershed pixel
    private int resolve( int p ) {
        int i = 0;
        int rep = -2;
        ArrayList<Integer> stN = steepestNeighbours.get( p );
        if ( stN == null )
            return rep;
        int con = stN.size();
        while ( i < con && rep != -1 ) {
            int sln = stN.get( i );
            if ( sln != p && sln != -1 ) {
                sln = resolve( sln );
                if ( sln > -1 ) {
                    stN.set( i, sln );
                }
            }
            if ( i == 0 ) {
                rep = stN.get( i );
            } else if ( sln != rep && sln > -1 && rep > -1 ) {
                rep = -1;
                stN.clear();
                stN.add( -1 );
            }
            i++;
        }
        return rep;
    }

    private void flood() {
        result = new Mat( image.rows(), image.cols(), image.type() );
        int[] watershed = new int[( int ) image.total()];
        HashMap<Integer, Point> watershedPoints = new HashMap<>();

        ArrayList<Integer> ids = MassiveWorker.INSTANCE.getIds();
        //start from pixels with min property
        for ( int i = 0; i < ids.size(); i++ ) {
            //for (int i = ids.size() - 1; i >= 0; i--) {
            int p = ids.get( i );
            int rep = resolve( p );

            if ( rep == -1 ) {
                watershed[p] = 255;
                watershedPoints.put( p, new Point( x( p ), y( p ) ) );
            } else {
                watershed[p] = 0;
            }
        }
        DataCollector.INSTANCE.setWatershedPoints( watershedPoints );

        result = setPointsToImage( watershed );
    }
}
