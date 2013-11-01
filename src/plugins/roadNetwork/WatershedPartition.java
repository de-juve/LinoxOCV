package plugins.roadNetwork;

import entities.DataCollector;
import entities.Line;
import entities.PixelsMentor;
import entities.Point;
import org.opencv.core.Mat;

import java.util.*;

public class WatershedPartition {
    Mat image, watershedImage;
    ArrayList<Point> crossroadPoints;
    HashMap<Integer, Point> watershedPoints;
    ArrayList<Line> lines;
    int[] shedLabels, lineLabels;
    boolean[] bcrossroadPoints, used;

    public void partitionNetwork( Mat img ) {
        image = img;
        watershedPoints = DataCollector.INSTANCE.getWatershedPoints();
        watershedImage = DataCollector.INSTANCE.getWatershedImg();
        shedLabels = DataCollector.INSTANCE.getShedLabels();
        bcrossroadPoints = new boolean[shedLabels.length];
        crossroadPoints = new ArrayList<>();

        if ( watershedPoints.isEmpty() || shedLabels.length < 1 ) {
            return;
        }

        findCrossroadPoints();
        Mat test = image.clone();
        double[] ccolor = new double[]{ 0, 0, 255 };
        for ( Point p : crossroadPoints ) {
            test.put( p.y, p.x, ccolor );
        }
        DataCollector.INSTANCE.addtoHistory( "crossroad points", test );

        /*partitionLines();
        test = image.clone();
        Random random = new Random( 1 );
        for(Map.Entry<Integer, Point> p : watershedPoints.entrySet()) {
            test.put(p.getValue().y, p.getValue().x, new double[]{random.nextInt( 255 ), random.nextInt( 255 ), random.nextInt( 255 )});
        }
        DataCollector.INSTANCE.addtoHistory("partition points", test);
        createEdges();*/
    }

    private void findCrossroadPoints() {
        int radiusOfNeighborhood = 1;
        for ( Point point : watershedPoints.values() ) {
            ArrayList<Integer> neigh = PixelsMentor.defineNeighboursIds( id( point ), image );
            ArrayList<Integer> area = new ArrayList<>();

            for ( Integer n : neigh ) {
                int shedLabel = shedLabels[n];
                if ( watershedImage.get( y( n ), x( n ) )[0] != 255 && !area.contains( shedLabel ) && shedLabel != shedLabels[id( point )] ) {
                    area.add( shedLabel );
                }
            }

            neigh = PixelsMentor.defineAllNeighboursOfPixel( point.x, point.y, image, radiusOfNeighborhood );
            int[] wsh = new int[neigh.size()];
            for ( int j = 0; j < neigh.size(); j++ ) {
                wsh[j] = ( int ) watershedImage.get( y( neigh.get( j ) ), x( neigh.get( j ) ) )[0];
            }
            boolean isCrossPoint = false;
            if ( area.size() < 3 ) {
                CrossroadMask masks;
                masks = new CrossroadMask( new int[]{
                        1, 0, 1,
                        0, 1, 0,
                        1, 0, 0 } );
                if ( masks.R( wsh ) >= 255 ) {
                    isCrossPoint = true;
                }
                masks = new CrossroadMask( new int[]{
                        1, 0, 0,
                        0, 1, 1,
                        1, 0, 0 } );
                if ( masks.R( wsh ) >= 255 ) {
                    isCrossPoint = true;
                }
                masks = new CrossroadMask( new int[]{
                        1, 0, 0,
                        0, 1, 0,
                        1, 0, 1 } );
                if ( masks.R( wsh ) >= 255 ) {
                    isCrossPoint = true;
                }
                masks = new CrossroadMask( new int[]{
                        1, 0, 1,
                        0, 1, 0,
                        0, 0, 1 } );
                if ( masks.R( wsh ) >= 255 ) {
                    isCrossPoint = true;
                }
                masks = new CrossroadMask( new int[]{
                        1, 0, 1,
                        0, 1, 0,
                        0, 1, 0 } );
                if ( masks.R( wsh ) >= 255 ) {
                    isCrossPoint = true;
                }
                masks = new CrossroadMask( new int[]{
                        1, 0, 0,
                        0, 1, 1,
                        0, 1, 0 } );
                if ( masks.R( wsh ) >= 255 ) {
                    isCrossPoint = true;
                }
                masks = new CrossroadMask( new int[]{
                        0, 0, 1,
                        1, 1, 0,
                        0, 0, 1 } );
                if ( masks.R( wsh ) >= 255 ) {
                    isCrossPoint = true;
                }
                masks = new CrossroadMask( new int[]{
                        0, 0, 0,
                        1, 1, 1,
                        0, 1, 0 } );
                if ( masks.R( wsh ) >= 255 ) {
                    isCrossPoint = true;
                }
                masks = new CrossroadMask( new int[]{
                        0, 1, 0,
                        1, 1, 1,
                        0, 0, 0 } );
                if ( masks.R( wsh ) >= 255 ) {
                    isCrossPoint = true;
                }
                masks = new CrossroadMask( new int[]{
                        0, 1, 0,
                        1, 1, 0,
                        0, 1, 0 } );
                if ( masks.R( wsh ) >= 255 ) {
                    isCrossPoint = true;
                }
                masks = new CrossroadMask( new int[]{
                        0, 0, 1,
                        1, 1, 0,
                        0, 1, 0 } );
                if ( masks.R( wsh ) >= 255 ) {
                    isCrossPoint = true;
                }
                masks = new CrossroadMask( new int[]{
                        0, 1, 0,
                        1, 1, 0,
                        0, 0, 1 } );
                if ( masks.R( wsh ) >= 255 ) {
                    isCrossPoint = true;
                }
                masks = new CrossroadMask( new int[]{
                        0, 0, 1,
                        0, 1, 0,
                        1, 0, 1 } );
                if ( masks.R( wsh ) >= 255 ) {
                    isCrossPoint = true;
                }
                masks = new CrossroadMask( new int[]{
                        0, 1, 0,
                        0, 1, 0,
                        1, 0, 1 } );
                if ( masks.R( wsh ) >= 255 ) {
                    isCrossPoint = true;
                }
                masks = new CrossroadMask( new int[]{
                        0, 1, 0,
                        0, 1, 1,
                        1, 0, 0 } );
                if ( masks.R( wsh ) >= 255 ) {
                    isCrossPoint = true;
                }
                masks = new CrossroadMask( new int[]{
                        0, 1, 0,
                        0, 1, 1,
                        0, 1, 0 } );
                if ( masks.R( wsh ) >= 255 ) {
                    isCrossPoint = true;
                }
            }

            if ( area.size() > 2 || isCrossPoint ) {
                bcrossroadPoints[id( point )] = true;
                crossroadPoints.add( point );
            }
        }
    }

    private void partitionLines() {
        lines = new ArrayList<>();
        boolean[] createNewLine = new boolean[shedLabels.length];
        used = new boolean[shedLabels.length];
        lineLabels = new int[shedLabels.length];
        int label = 0;

        Queue<Point> queue = new LinkedList<>();

        Point point = getUnusedWatershedPixel();
        if ( point == null ) {
            return;
        }
        createNewLine[id( point )] = true;
        queue.add( point );

        while ( true ) {
            while ( !queue.isEmpty() ) {
                point = queue.poll();
                Line line;

                if ( used[id( point )] )
                    continue;

                ArrayList<Integer> wneigh = PixelsMentor.defineNeighboursIdsWithSameValue( id( point ), watershedImage );

                if ( createNewLine[id( point )] ) {
                    line = new Line();
                    createNewLine[id( point )] = false;
                    line.label = label;
                    lines.add( label, line );
                    label++;
                } else {
                    int usedNeighboure = getFirstUsedNeighboure( wneigh );
                    line = lines.get( lineLabels[usedNeighboure] );
                }

                lineLabels[id( point )] = line.label;
                line.add( point );
                used[id( point )] = true;

                removeDupesAndSort( wneigh );
                if ( wneigh.size() > 0 ) {
                    if ( bcrossroadPoints[id( point )] ) {
                        //multiple crossing point
                        while ( bcrossroadPoints[wneigh.get( 0 )] ) {
                            Integer cp = wneigh.get( 0 );
                            // watershedPoints.g
                            lineLabels[cp] = lineLabels[id( point )];
                            line.add( watershedPoints.get( cp ) );

                            used[cp] = true;
                            wneigh.remove( cp );
                            wneigh.addAll( PixelsMentor.defineNeighboursIdsWithSameValue( cp, watershedImage ) );
                            wneigh.remove( ( Integer ) id( point ) );
                            removeDupesAndSort( wneigh );
                            if ( wneigh.size() == 0 )
                                break;
                        }
                    }
                    for ( Integer s : wneigh ) {
                        if ( createNewLine[s] )
                            createNewLine[s] = false;
                        if ( !used[s] )
                            queue.add( watershedPoints.get( s ) );
                    }
                    if ( bcrossroadPoints[id( point )] ) {
                        // node.setEnd(p);
                        for ( Integer s : wneigh )
                            createNewLine[s] = true;
                    }
                }
            }
            point = getUnusedWatershedPixel();
            if ( point != null ) {
                createNewLine[id( point )] = true;
                queue.add( point );
            } else
                break;
        }
    }

    private void createEdges() {


        for ( Point crossPoint : crossroadPoints ) {

            ArrayList<Integer> wn = PixelsMentor.defineNeighboursIdsWithSameValue( id( crossPoint ), watershedImage );
            for ( Integer n : wn ) {
                for ( Integer m : wn ) {
                    if ( lineLabels[n] != lineLabels[m] ) {
                        lines.get( lineLabels[n] ).addConnection( lines.get( lineLabels[m] ) );
                        lines.get( lineLabels[m] ).addConnection( lines.get( lineLabels[n] ) );
                    }
                }
            }

        }
    }


    /*private void correctNodes() {
        //GetterNeighboures getterN = new GetterNeighboures(DataCollection.INSTANCE.getWshPoints());
        for (Map.Entry<Integer, Point> point : watershedPoints.entrySet()) {
            if (!bcrossroadPoints[point.getKey()]) {
                ArrayList<Integer> wn = PixelsMentor.defineNeighboursIdsWithSameValue( point.getKey(), watershedImage );
                for (Integer n : wn) {
                    if (lineLabels[n] != lineLabels[point.getKey()] && !bcrossroadPoints[n] &&
                            lineLabels[n] > 0) {

                            ArrayList<Integer> elms = NodeWorker.getInstance().getNodeByLabel(DataCollection.INSTANCE.getNodeLabel(n)).getElements();
                            NodeWorker.getInstance().unionNodes(DataCollection.INSTANCE.getNodeLabel(i), DataCollection.INSTANCE.getNodeLabel(n));
                            for (Integer elm : elms) {
                                DataCollection.INSTANCE.setNodeLabel(elm, DataCollection.INSTANCE.getNodeLabel(n));
                            }
                        }
                    }
                }
            }
        }
        NodeWorker.getInstance().sortNodesElements(width, height);
    }
    */

    private Point getUnusedWatershedPixel() {
        for ( Map.Entry<Integer, Point> point : watershedPoints.entrySet() ) {
            if ( !used[point.getKey()] ) {
                return point.getValue();
            }
        }
        return null;
    }

    private Integer getFirstUsedNeighboure( ArrayList<Integer> wneigh ) {
        for ( Integer n : wneigh ) {
            if ( used[n] && !bcrossroadPoints[n] )
                return n;
        }
        return -1;
    }

    private void removeDupesAndSort( ArrayList<Integer> wneight ) {
        ArrayList<Integer> duplicat = new ArrayList<>();
        for ( Integer p : wneight ) {
            if ( !used[p] ) {
                if ( bcrossroadPoints[p] )
                    duplicat.add( 0, p );
                else
                    duplicat.add( duplicat.size(), p );
            }
        }
        wneight.clear();
        wneight.addAll( duplicat );
    }

    protected int id( Point point ) {
        return id( point.x, point.y );
    }

    protected int id( int x, int y ) {
        return x + y * image.width();
    }


    protected int x( int id ) {
        return id % image.width();
    }

    protected int y( int id ) {
        return id / image.width();
    }
}
