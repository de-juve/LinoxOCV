package gui.menu;

import org.opencv.core.Mat;

import java.util.ArrayList;

public class LinoxImageFactory {
    private final ArrayList<ImageJPanel> items = new ArrayList<>();

    public ImageJPanel addImage( String title, Mat image ) {
        final ImageJPanel panel = new ImageJPanel( title, image );
        items.add( panel );
        return panel;
    }

    public ArrayList<ImageJPanel> getItems() {
        return items;
    }

    LinoxImageFactory() {
        items.add( new ImageJPanel() );
    }
}

