package gui.menu;

import org.opencv.core.Mat;

import java.util.ArrayList;

public class LinoxImageFactory {
    private final ArrayList<ImageJPanel> items = new ArrayList<>();
    private final ArrayList<TestImageJPanel> testItems = new ArrayList<>();

    public ImageJPanel addImage( String title, Mat image ) {
        final ImageJPanel panel = new ImageJPanel( title, image );
        items.add( panel );
        return panel;
    }

    public TestImageJPanel addTestImages( String title, Mat image1, Mat image2 ) {
        final TestImageJPanel panel = new TestImageJPanel( title, image1, image2 );
        testItems.add( panel );
        return panel;
    }

    public ImageJPanel replaceImage( String title, Mat image ) {
        for ( ImageJPanel p : items ) {
            if ( p.title == title ) {
                p.setImage( image );
                return p;
            }
        }
        return addImage( title, image );
    }

    public TestImageJPanel replaceTestImage( String title, Mat image1, Mat image2 ) {
        for ( TestImageJPanel p : testItems ) {
            if ( p.title == title ) {
                p.setImage1( image1 );
                p.setImage2( image2 );
                return p;
            }
        }
        return addTestImages( title, image1, image2 );
    }


    public ArrayList<ImageJPanel> getItems() {
        return items;
    }

    public ArrayList<TestImageJPanel> getTestItems() {
        return testItems;
    }

    LinoxImageFactory() {
        items.add( new ImageJPanel() );
        testItems.add( new TestImageJPanel() );
    }
}

