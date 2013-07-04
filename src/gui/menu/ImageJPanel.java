package gui.menu;

import gui.Linox;
import net.miginfocom.swing.MigLayout;
import org.opencv.core.Mat;


import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;

public class ImageJPanel extends JPanel {
    JScrollPane imageScrollPane;
    JLabel imageView = new JLabel();
    Mat image;
    String title;
    JButton button;
    MouseMotionAdapter mouseMotionAdapter;
    MouseAdapter mouseAdapter;
    private boolean mousePressed = false;

    ImageJPanel(String _title, Mat _image) {
        this.setLayout(new MigLayout());
        title = _title;
        image = _image;

        imageView.setIcon(new ImageIcon(matToBufferedImage(image)));

        mouseMotionAdapter =  new MouseMotionAdapter() {
            @Override
            public void mouseMoved(MouseEvent e) {
                if(e.getX() < image.width() && e.getX() > -1 && e.getY() < image.height() && e.getY() > -1) {
                    double[] px;
                    px = image.get(e.getY(), e.getX());
                    double r,g,b;
                    int lum;
                    if(px.length == 1) {
                        r = g = b = px[0];
                        lum = (int)px[0];
                    } else {
                        r = px[0];//(pixel & 0xff0000) >> 16;
                        g = px[1];//(pixel & 0x00ff00) >> 8;
                        b = px[2];//(pixel & 0x0000ff);
                        lum = (int) (0.299 * r + 0.587 * g + 0.114 * b);
                    }
                    int id = e.getX() + e.getY() * image.width();
                    Linox.getInstance().getStatusBar().setStatus("(" + e.getX() + ", " + e.getY() + ") id = " + id + " RGB(" + r + ", " + g + ", " + b + ") lum(" + lum + ")");
                }
            }

        };
        imageView.addMouseMotionListener( mouseMotionAdapter);

        imageScrollPane = new JScrollPane(imageView, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        imageScrollPane.getVerticalScrollBar().setPreferredSize(new Dimension(10, 0));
        imageScrollPane.getHorizontalScrollBar().setPreferredSize(new Dimension(0, 10));

        this.add(imageScrollPane);
    }

    ImageJPanel() {
        this.setLayout(new MigLayout());
        imageView.setText("No image");
        title = "Empty";
        image = null;
        final JScrollPane imageScrollPane = new JScrollPane(imageView);
        // imageScrollPane.setPreferredSize(new Dimension(90, 30));
        add(imageScrollPane);
    }

    void setButton(Action action, boolean visible) {
        if(button != null) {
            this.remove(button);
        }
        button = new JButton(action);
        button.setVisible(visible);
        this.add(button);
    }

    void removeButton() {
        if(button != null) {
            this.remove(button);
        }
    }

    void setMouseListener(MouseAdapter adapter) {
        if(mouseAdapter != null) {
            imageView.removeMouseListener(mouseAdapter);
            imageView.removeMouseMotionListener(mouseAdapter);
        } else {
            imageView.removeMouseMotionListener(mouseMotionAdapter);
        }

        mouseAdapter = adapter;
        imageView.addMouseListener(mouseAdapter);
        imageView.addMouseMotionListener(mouseAdapter);

    }

    void resetMouseMotionListener() {
        imageView.removeMouseListener(mouseAdapter);
        imageView.removeMouseMotionListener(mouseAdapter);
        mouseAdapter = null;

        imageView.addMouseMotionListener(mouseMotionAdapter);
    }

    public Mat getImage() {
        return image;
    }

    public String getTitle() {
        return title;
    }

    public void setMousePressed(boolean value) {
        mousePressed = value;
    }

    public boolean getMousePressed() {
        return mousePressed;
    }

    /**
     * Converts/writes a Mat into a BufferedImage.
     *
     * @param matrix Mat of type CV_8UC3 or CV_8UC1
     * @return BufferedImage of type TYPE_3BYTE_BGR or TYPE_BYTE_GRAY
     */
    public BufferedImage matToBufferedImage(Mat matrix) {
        int cols = matrix.cols();
        int rows = matrix.rows();
        int elemSize = (int)matrix.elemSize();
        byte[] data = new byte[cols * rows * elemSize];
        int type;

        matrix.get(0, 0, data);

        switch (matrix.channels()) {
            case 1:
                type = BufferedImage.TYPE_BYTE_GRAY;
                break;

            case 3:
                type = BufferedImage.TYPE_3BYTE_BGR;

                // bgr to rgb
                byte b;
                for(int i=0; i<data.length; i=i+3) {
                    b = data[i];
                    data[i] = data[i+2];
                    data[i+2] = b;
                }
                break;

            default:
                return null;
        }

        BufferedImage image = new BufferedImage(cols, rows, type);
        image.getRaster().setDataElements(0, 0, cols, rows, data);

        return image;
    }

}
