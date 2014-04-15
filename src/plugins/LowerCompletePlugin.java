package plugins;

import entities.DataCollector;
import entities.PixelsMentor;
import entities.Point;
import gui.Linox;
import org.opencv.core.Mat;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;

public class LowerCompletePlugin extends AbstractPlugin {
    Mat gray;
    Queue<Point> queue = new LinkedList<>();
    int[] level;
    int distination;

    public LowerCompletePlugin() {
        title = "Lower complete";
    }

    @Override
    public void run() {
        long start = System.nanoTime();
        print(this.title + " begin");
        Linox.getInstance().getStatusBar().setProgress( title, 0, 100 );

        gray = GrayscalePlugin.run( image, false );

        InitQueue();

        distination = 1;
        queue.add( new Point( -1, -1 ) );
        while ( !queue.isEmpty() ) {
            Point point = queue.remove();
            if ( point.x == -1 && queue.size() > 0 ) {
                queue.add( new Point( -1, -1 ) );
                distination++;
            } else if ( point.x > -1 ) {
                level[id( point.x, point.y )] = distination;
                int lum = ( int ) gray.get( point.y, point.x )[0];
                ArrayList<Integer> neighbors = PixelsMentor.defineNeighboursIdsWithSameValue( id( point.x, point.y ), gray );
                for ( Integer nid : neighbors ) {
                    if ( level[nid] == 0 ) {
                        level[nid] = -1;
                        queue.add( new Point( x( nid ), y( nid ) ) );
                    }
                }
                /*for (int j = Math.max(0, point.y - 1); j <= Math.min(image.height() - 1, point.y + 1); j++) {
                    for (int i = Math.max(0, point.x - 1); i <= Math.min(image.width() - 1, point.x + 1); i++) {
                        int nlum = (int) gray.get(j, i)[0];
                        if (point.x != i && point.y != j && lum == nlum && level[i + j * image.width()] == 0) {
                            level[id(i, j)] = -1;
                            queue.add(new Point(i, j));
                        }
                    }
                }*/
            }
        }

        DataCollector.INSTANCE.setLowerCompletion( level );
        Linox.getInstance().getStatusBar().setProgress( title, 100, 100 );

        long end = System.nanoTime();
        long traceTime = end-start;
        print(this.title + " finish: " + traceTime);

        if ( pluginListener != null ) {
            pluginListener.addImageTab();
            pluginListener.finishPlugin();
        }
    }

    private void InitQueue() {
        level = new int[( int ) image.total()];
        for ( int row = 0; row < image.height(); row++ ) {
            for ( int col = 0; col < image.width(); col++ ) {
                int lum = ( int ) gray.get( row, col )[0];
                level[id( col, row )] = 0;
                N:
                {
                    ArrayList<Point> neighbors = PixelsMentor.getNeighborhoodOfPixel( col, row, image, 1 );
                    for ( Point n : neighbors ) {
                        int nlum = ( int ) gray.get( n.y, n.x )[0];
                        if ( lum > nlum ) {
                            level[id( col, row )] = -1;
                            queue.add( new Point( col, row ) );
                            break N;

                        }

                    }
                }
            }
        }
    }
}
