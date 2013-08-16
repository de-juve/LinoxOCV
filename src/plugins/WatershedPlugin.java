package plugins;

import entities.*;
import gui.Linox;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import plugins.morphology.MorphologyPlugin;

import java.util.ArrayList;
import java.util.TreeMap;

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
        Linox.getInstance().getStatusBar().setProgress( title, 0, 100 );

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

        /*DataCollector.INSTANCE.addtoHistory( "wsh", result );

        // Identify image pixels without objects
        Mat bg = new Mat( image.size(), image.type() );
        Imgproc.dilate( result, bg, new Mat(), new org.opencv.core.Point( -1, -1 ), 3 );
        Imgproc.threshold( bg, bg, 1, 128, Imgproc.THRESH_BINARY_INV );

        Core.add( result, bg, result );

        Core.extractChannel( result, result, 0 );

        result.convertTo( result, CvType.CV_32S );
        Imgproc.watershed( image, result );
        result.convertTo( result, CvType.CV_8U );
        ArrayList<Mat> channels = new ArrayList<>();
        channels.add( result );
        channels.add( result );
        channels.add( result );

        Core.merge( channels, result );*/


        mp.initImage( result );
        mp.run( "Closing", 1 );
        result = mp.result;

        Linox.getInstance().getStatusBar().setProgress( title, 100, 100 );

        if ( pluginListener != null ) {
            pluginListener.addImageTab();
            pluginListener.finishPlugin();
        }
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
        ArrayList<Integer> ids = MassiveWorker.INSTANCE.getIds();
        //start from pixels with min property
        for ( int i = ids.size() - 1; i >= 0; i-- ) {
            int p = ids.get( i );
            int rep = resolve( p );

            if ( rep == -1 ) {
                watershed[p] = 255;
            } else {
                watershed[p] = 0;
            }
        }
        DataCollector.INSTANCE.setWatershedPoints( watershed );

        byte[] buff = new byte[( int ) image.total() * image.channels()];

        int j = 0;
        for ( int i = 0; i < watershed.length; i++ ) {
            for ( int k = 0; k < image.channels(); k++ ) {
                buff[j] = ( byte ) watershed[i];
                j++;
            }
        }

        result.put( 0, 0, buff );
    }

    /**
     * Function for thinning the given binary image
     *
     * @param im Binary image with range = 0-255
     */
    void thinning( Mat im ) {
        Core.normalize( im, im, 0, 1, Core.NORM_MINMAX );
        //im /= 255;

        Mat prev = Mat.zeros( im.size(), im.type() );//CvType.CV_8UC1);
        Mat diff = new Mat( im.size(), im.type() );
        Mat m = new Mat( im.size(), im.type() );

        do {
            thinningIteration( im, 0 );
            thinningIteration( im, 1 );
            Core.absdiff( im, prev, diff );
            im.copyTo( prev );
            Core.extractChannel( diff, m, 0 );
        }
        while ( Core.countNonZero( m ) > 0 );

        Core.normalize( im, im, 0, 255, Core.NORM_MINMAX );
        //im *= 255;
    }


    /**
     * Perform one thinning iteration.
     * Normally you wouldn't call this function directly from your code.
     *
     * @param im   Binary image with range = 0-1
     * @param iter 0=even, 1=odd
     */
    Mat thinningIteration( Mat im, int iter ) {
        Mat marker = Mat.zeros( im.size(), CvType.CV_8UC1 );

        for ( int i = 1; i < im.rows() - 1; i++ ) {
            for ( int j = 1; j < im.cols() - 1; j++ ) {
                int p2 = ( byte ) im.get( i - 1, j )[0];
                int p3 = ( byte ) im.get( i - 1, j + 1 )[0];
                int p4 = ( byte ) im.get( i, j + 1 )[0];
                int p5 = ( byte ) im.get( i + 1, j + 1 )[0];
                int p6 = ( byte ) im.get( i + 1, j )[0];
                int p7 = ( byte ) im.get( i + 1, j - 1 )[0];
                int p8 = ( byte ) im.get( i, j - 1 )[0];
                int p9 = ( byte ) im.get( i - 1, j - 1 )[0];

                int A = ( p2 == 0 && p3 == 1 ) ? 1 : 0;
                A += ( p3 == 0 && p4 == 1 ) ? 1 : 0;
                A += ( p4 == 0 && p5 == 1 ) ? 1 : 0;
                A += ( p5 == 0 && p6 == 1 ) ? 1 : 0;
                A += ( p6 == 0 && p7 == 1 ) ? 1 : 0;
                A += ( p7 == 0 && p8 == 1 ) ? 1 : 0;
                A += ( p8 == 0 && p9 == 1 ) ? 1 : 0;
                A += ( p9 == 0 && p2 == 1 ) ? 1 : 0;

                int B = p2 + p3 + p4 + p5 + p6 + p7 + p8 + p9;
                int m1 = iter == 0 ? ( p2 * p4 * p6 ) : ( p2 * p4 * p8 );
                int m2 = iter == 0 ? ( p4 * p6 * p8 ) : ( p2 * p6 * p8 );

                if ( A == 1 && ( B >= 2 && B <= 6 ) && m1 == 0 && m2 == 0 )
                    marker.put( i, j, 1 );
            }
        }

        return marker;
    }
}
