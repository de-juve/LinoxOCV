package entities;

import org.opencv.core.Mat;

import java.util.ArrayList;

/**
 * Created by dm on 01.02.14.
 */
public class Mask {
    private Mat mask;
    private int roadWidth;
    private Part bg1, bg2, fg;

    public Mask( int rows, int _roadWidth, int type ) {
        // while( rows % 3 != 0) rows++;

        mask = new Mat( rows, rows, type );
        roadWidth = _roadWidth;
    }

    public void fill(Point start, Mat image) {
        byte[] buff = new byte[(int) mask.total() * mask.channels()];


        for ( int y = start.y, j = 0; y < image.rows() && j < mask.rows(); y++, j++ ) {
            for ( int x = start.x, i = 0; x < image.cols() && i < mask.cols(); x++, i++ ) {
                for (int k = 0; k < mask.channels(); k++) {
                    buff[i + j * mask.cols()] = ( byte ) image.get( y, x )[k];
                }
            }
        }
        mask.put( 0, 0, buff );
        System.out.println();
        System.out.println( start );
    }

    public void partition( MaskType type ) {
        fg = new Part();
        bg1 = new Part();
        bg2 = new Part();

        if (type.equals(MaskType.LToR)) {
            int bg1_y_end = (mask.rows() - roadWidth) / 2 - 1;
            int fg_y_end = bg1_y_end + roadWidth;

            for (int y = 0; y < mask.rows(); y++) {
                for (int x = 0; x < mask.cols(); x++) {
                    if (y >= 0 && y <= bg1_y_end) {
                        bg1.add(new Point(x, y));
                    } else if (y > bg1_y_end && y <= fg_y_end) {
                        fg.add(new Point(x, y));
                    } else {
                        bg2.add(new Point(x, y));
                    }
                }
            }
        } else if (type.equals(MaskType.UToD)) {
            int bg1_x_end = (mask.cols() - roadWidth) / 2 - 1;
            int fg_x_end = bg1_x_end + roadWidth;

            for (int y = 0; y < mask.rows(); y++) {
                for (int x = 0; x < mask.cols(); x++) {
                    if (x >= 0 && x <= bg1_x_end) {
                        bg1.add(new Point(x, y));
                    } else if (x > bg1_x_end && x <= fg_x_end) {
                        fg.add(new Point(x, y));
                    } else {
                        bg2.add(new Point(x, y));
                    }
                }
            }
        } else if (type.equals(MaskType.LUToRD)) {
            int half = (int) Math.floor((double) roadWidth / 2);
            int big_half = (int) Math.ceil((double) roadWidth / 2);
            for (int y = 0; y < mask.rows(); y++) {
                for (int x = Math.max(0, y - half); x < Math.min(mask.cols(), y + big_half); x++) {
                    fg.add(new Point(x, y));
                }
                for ( int x = Math.min( mask.cols() - 1, y + big_half ); x < mask.cols(); x++ ) {
                    Point p = new Point( x, y );
                    if ( !fg.contains( p ) ) {
                        bg1.add( p );
                    }
                }
                for (int x = 0; x < Math.max(0, y - half); x++) {
                    Point p = new Point( x, y );
                    if ( !fg.contains( p ) ) {
                        bg2.add( p );
                    }
                }
            }
        } else {
            int half = (int) Math.floor((double) roadWidth / 2);
            int big_half = (int) Math.ceil((double) roadWidth / 2);
            for (int y = 0; y < mask.rows(); y++) {
                for (int x = Math.max(0, mask.rows() - y - half - 1); x < Math.min(mask.cols(), mask.rows() - y + big_half - 1); x++) {
                    fg.add(new Point(x, y));
                }
                for ( int x = 0; x < Math.max( 1, mask.rows() - y - half - 1 ); x++ ) {
                    Point p = new Point( x, y );
                    if ( !fg.contains( p ) ) {
                        bg1.add( p );
                    }
                }
                for ( int x = Math.min( mask.cols() - 1, mask.rows() - y + big_half - 1 ); x < mask.cols(); x++ ) {
                    Point p = new Point( x, y );
                    if ( !fg.contains( p ) ) {
                        bg2.add( p );
                    }
                }
            }
        }


        System.out.println( "TYPE:" + type );       //+ " Mask:\n" + mask.dump()
        analyze();
    }

    public void analyze() {
        fg.countAvrL();
        bg1.countAvrL();
        bg2.countAvrL();
        System.out.println( "BG1: " + bg1.avrL );
        System.out.println( "FG:" + fg.avrL );
        System.out.println( "BG2: " + bg2.avrL );
        System.out.println();
    }

    public enum MaskType {
        LToR, UToD, LUToRD, LDToRU;
    }

    public class Part {
        ArrayList<Point> points;
        double avrL;
        double contrast;

        public Part() {
            points = new ArrayList<>();
            avrL = 0;
            contrast = 0;
        }

        public void add( Point p ) {
            points.add( p );
        }

        public boolean contains( Point p ) {
            return points.contains( p );
        }

        public void countAvrL() {
            for ( Point p : points ) {
                avrL += mask.get( p.y, p.x )[0];
            }
            avrL /= points.size();
        }
    }
}


