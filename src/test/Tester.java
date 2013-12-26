package test;

import entities.PixelsMentor;
import entities.Point;
import gui.Linox;
import gui.menu.ExtensionFileFilter;
import org.opencv.core.Mat;
import org.opencv.highgui.Highgui;

import javax.swing.*;
import javax.swing.filechooser.FileFilter;
import java.io.File;
import java.util.ArrayList;

public class Tester {
    Mat perfectImage;
    Mat testImage;
    int width, height;
    ArrayList<Point> testPoints, perfectPoints, goodPoints, errorPoints;
    int radius = 3;

    public Tester() {
        String dir = System.getProperty( "user.dir" ) + "/resource";
        JFileChooser fileChooser = new JFileChooser( dir );

        FileFilter filter1 = new ExtensionFileFilter( "JPG and JPEG", new String[]{ "JPG", "JPEG" } );
        fileChooser.setFileFilter( filter1 );
        FileFilter filter2 = new ExtensionFileFilter( "PNG and BMP", new String[]{ "PNG", "BMP" } );
        fileChooser.setFileFilter( filter2 );
        FileFilter filter3 = new ExtensionFileFilter( "ALL", new String[]{ "JPG", "JPEG", "PNG", "BMP", "TIFF", "GIF" } );
        fileChooser.setFileFilter( filter3 );
        fileChooser.setMultiSelectionEnabled( true );
        // Ask user for the location of the image file
        if ( fileChooser.showOpenDialog( null ) != JFileChooser.APPROVE_OPTION ) {
            JOptionPane.showMessageDialog( Linox.getInstance(), "Can't open file chooser dialog" );
            return;
        }


// Retrieve the selected files.
        File[] files = fileChooser.getSelectedFiles();
        if ( files.length > 2 ) {
            JOptionPane.showMessageDialog( Linox.getInstance(), "Choose only two files" );
            return;
        }
        Mat[] images = new Mat[2];
        int i = 0;
        for ( File file : files ) {
            images[i] = Highgui.imread( file.getAbsolutePath(), Highgui.CV_LOAD_IMAGE_COLOR );
            i++;
        }
        if ( images[0].width() != images[1].width() || images[0].height() != images[1].height() ) {
            JOptionPane.showMessageDialog( Linox.getInstance(), "Choose images with one size" );
            return;
        }
        ( Linox.getInstance().getImageStore() ).addTestImageTab( files[0].getName(), images[0], images[1] );

        width = images[0].width();
        height = images[0].height();

        a:
        for ( int row = 0; row < height; row++ ) {
            for ( int col = 0; col < width; col++ ) {
                int b = ( int ) images[0].get( row, col )[0];
                int g = ( int ) images[0].get( row, col )[1];
                int r = ( int ) images[0].get( row, col )[2];
                if ( r == 255 && g == 0 && b == 0 ) {
                    perfectImage = images[0];
                    testImage = images[1];
                    break a;
                } else {
                    perfectImage = images[1];
                    testImage = images[0];
                    break a;
                }
            }
        }

        analyzeTestImage();
        analyzePerfectImage();

        if ( testPoints.isEmpty() || perfectPoints.isEmpty() ) {
            JOptionPane.showMessageDialog( Linox.getInstance(), "Perfect or test points not founded" );
            return;
        }

        goodPoints = new ArrayList<>();
        errorPoints = new ArrayList<>();
        for ( Point tPoint : testPoints ) {
            if ( analyzeNeighboures( tPoint ) ) {
                goodPoints.add( tPoint );
            } else {
                errorPoints.add( tPoint );
            }
        }
        String message = "Good points: " + goodPoints.size() + "\nBad points: " + errorPoints.size() +
                "\nAll test points: " + testPoints.size() + "\nAll perfect points: " + perfectPoints.size();
        JOptionPane.showMessageDialog( Linox.getInstance(), message );

    }

    private void analyzeTestImage() {
        testPoints = new ArrayList<>();
        for ( int row = 0; row < height; row++ ) {
            for ( int col = 0; col < width; col++ ) {
                int lum = ( int ) testImage.get( row, col )[0];
                if ( lum == 255 ) {
                    testPoints.add( new Point( col, row ) );
                }
            }
        }
    }

    private boolean analyzeNeighboures( Point p ) {
        ArrayList<Integer> neighbours = PixelsMentor.defineAllNeighboursOfPixel( p.x, p.y, perfectImage, radius );
        for ( Integer id : neighbours ) {
            int b = ( int ) perfectImage.get( y( id ), x( id ) )[0];
            int g = ( int ) perfectImage.get( y( id ), x( id ) )[1];
            int r = ( int ) perfectImage.get( y( id ), x( id ) )[2];
            if ( r == 255 && g == 0 && b == 0 ) {
                return true;
            }
        }
        return false;
    }

    private void analyzePerfectImage() {
        perfectPoints = new ArrayList<>();
        for ( int row = 0; row < height; row++ ) {
            for ( int col = 0; col < width; col++ ) {
                int b = ( int ) perfectImage.get( row, col )[0];
                int g = ( int ) perfectImage.get( row, col )[1];
                int r = ( int ) perfectImage.get( row, col )[2];
                if ( r == 255 && g == 0 && b == 0 ) {
                    perfectPoints.add( new Point( col, row ) );
                }
            }
        }
    }

    private int id( int x, int y ) {
        return x + y * width;
    }

    private int x( int id ) {
        return id % width;
    }

    private int y( int id ) {
        return id / width;
    }
}
