package entities;

import org.opencv.core.CvType;
import org.opencv.core.Mat;

import java.util.ArrayList;

/**
 * Created by dm on 01.02.14.
 */
public class Mask {
    private Mat mask;
    private int roadWidth;

    public Mask(int rows, int _roadWidth) {
        // while( rows % 3 != 0) rows++;

        mask = new Mat(rows, rows, CvType.CV_8UC1);
        roadWidth = _roadWidth;
    }

    public void fill(Point start, Mat image) {
        byte[] buff = new byte[(int) mask.total() * mask.channels()];

        int idx = 0;
        for (int y = start.y; y < mask.rows(); y++) {
            for (int x = start.x; x < mask.cols(); x++) {
                for (int k = 0; k < mask.channels(); k++) {
                    buff[idx] = (byte) image.get(y, x)[k];
                }
            }
        }
        mask.put(0, 0, buff);
    }

    public void analyze(MaskType type) {
        ArrayList<Point> bg1, bg2, fg;
        fg = new ArrayList<>();
        bg1 = new ArrayList<>();
        bg2 = new ArrayList<>();

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
                    // System.out.println("y= "+y+" x= "+x+" x<: "+ (mask.rows() - y - half - 1));
                    Point p = new Point( x, y );
                    if ( !fg.contains( p ) ) {
                        bg1.add( p );
                    } else {
                        // System.out.println("contain "+p);
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
        System.out.println();
        System.out.println( "cols: " + mask.cols() + " rows: " + mask.rows() );
        System.out.println("TYPE:" + type + " Mask:\n" + mask.dump());
        System.out.println("BG1: " + bg1.toString());
        System.out.println("FG:" + fg.toString());
        System.out.println("BG2: " + bg2.toString());

    }

    public enum MaskType {
        LToR, UToD, LUToRD, LDToRU;
    }
}


