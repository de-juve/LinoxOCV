package gui.menu;

import gui.Linox;
import plugins.DataCollector;
import org.opencv.core.Mat;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.ArrayList;

public class LinoxFileMenuFactory {
    private final ArrayList<JMenuItem> items = new ArrayList<>();

    public ArrayList<JMenuItem> getItems() {
        return items;
    }

    public LinoxFileMenuFactory() {
        final Action openImage = new AbstractAction("Open Image") {
            @Override
            public void actionPerformed(ActionEvent e) {
                Linox.getInstance().setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
                try {
                    // Load image and update display. If new image was not loaded do nothing.
                    final Mat img = ((LinoxMenuStore)Linox.getInstance().getMenuStore()).openImage();
                    if(img != null) {
                        Linox.getInstance().getStatusBar().setStatus( "image : width = " + img.width() + " height = " + img.height());
                    }
                } finally {
                    Linox.getInstance().setCursor(Cursor.getDefaultCursor());
                }
            }
        };

        final Action saveImage = new AbstractAction("Save Image") {
            @Override
            public void actionPerformed(ActionEvent e) {
                Linox.getInstance().setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
                try {
                    if(DataCollector.INSTANCE.getImageOriginal() != null) {
                        ((LinoxMenuStore)Linox.getInstance().getMenuStore()).saveImage(DataCollector.INSTANCE.getImageOriginal());
                    }
                } finally {
                    Linox.getInstance().setCursor(Cursor.getDefaultCursor());
                }
            }
        };

       /* final Action saveStackHistory = new AbstractAction("Save Images Of Stack History") {
            @Override
            public void actionPerformed(ActionEvent e) {
                Linox.getInstance().setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
                try {
                    String dir_r =   System.getProperty("user.dir") + "/resource/stackHistory/";
                    ImageStack stack =  DataCollection.INSTANCE.getHistoryStack();

                    for(int i = 1; i <=  stack.getSize(); i++) {
                        IJ.save(new ImagePlus("1.jpg", stack.getProcessor(i)), dir_r + stack.getSliceLabel(i));

                    }
                } finally {
                    Linox.getInstance().setCursor(Cursor.getDefaultCursor());
                }
            }
        };*/

        final Action closeImage = new AbstractAction("Close Image") {
            @Override
            public void actionPerformed(ActionEvent e) {
                Linox.getInstance().setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
                try {
                    ((LinoxMenuStore)Linox.getInstance().getMenuStore()).closeImage();
                } finally {
                    Linox.getInstance().setCursor(Cursor.getDefaultCursor());
                }
            }
        };

        final Action exit = new AbstractAction("Exit") {
            @Override
            public void actionPerformed(ActionEvent e) {
                Linox.getInstance().setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
                try {
                    ((LinoxMenuStore)Linox.getInstance().getMenuStore()).exit();
                } finally {
                    Linox.getInstance().setCursor(Cursor.getDefaultCursor());
                }
            }
        };

        items.add(new JMenuItem(openImage));
        items.add(new JMenuItem(saveImage));
       // items.add(new JMenuItem(saveStackHistory));
        items.add(new JMenuItem(closeImage));
        items.add(new JMenuItem(exit));
        // items.add(new JMenuItem(test));

    }
}
