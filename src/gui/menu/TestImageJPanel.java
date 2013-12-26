package gui.menu;

import gui.Linox;
import gui.dialog.ParameterButton;
import gui.dialog.ParameterSlider;
import net.miginfocom.swing.MigLayout;
import org.opencv.core.Mat;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.image.BufferedImage;

public class TestImageJPanel extends JPanel {
    JScrollPane imageScrollPane1;
    JScrollPane imageScrollPane2;
    public JLabel imageView1, imageView2;
    public Mat image1, image2;
    String title;
    JButton button;
    MouseMotionAdapter mouseMotionAdapter1;
    MouseMotionAdapter mouseMotionAdapter2;
    MouseAdapter mouseAdapter;
    public ParameterButton pButton;
    public ParameterSlider pSlider;
    private boolean mousePressed = false;

    TestImageJPanel( String _title, Mat _image1, Mat _image2 ) {
        this.setLayout( new MigLayout() );
        title = _title;
        image1 = _image1;
        image2 = _image2;
        imageView1 = new JLabel( new ImageIcon( matToBufferedImage( image1 ) ) );
        imageView2 = new JLabel( new ImageIcon( matToBufferedImage( image2 ) ) );

        mouseMotionAdapter1 = new MouseMotionAdapter() {
            @Override
            public void mouseMoved( MouseEvent e ) {
                if ( e.getX() < image1.width() && e.getX() > -1 && e.getY() < image1.height() && e.getY() > -1 ) {
                    double[] px;
                    px = image1.get( e.getY(), e.getX() );
                    double r, g, b;
                    int lum;
                    if ( px.length == 1 ) {
                        r = g = b = px[0];
                        lum = ( int ) px[0];
                    } else {
                        r = px[0];//(pixel & 0xff0000) >> 16;
                        g = px[1];//(pixel & 0x00ff00) >> 8;
                        b = px[2];//(pixel & 0x0000ff);
                        lum = ( int ) ( 0.299 * r + 0.587 * g + 0.114 * b );
                    }
                    int id = e.getX() + e.getY() * image1.width();
                    Linox.getInstance().getStatusBar().setStatus( "(" + e.getX() + ", " + e.getY() + ") id = " + id + " RGB(" + r + ", " + g + ", " + b + ") lum(" + lum + ")" );
                }
            }

        };
        imageView1.addMouseMotionListener( mouseMotionAdapter1 );

        mouseMotionAdapter2 = new MouseMotionAdapter() {
            @Override
            public void mouseMoved( MouseEvent e ) {
                if ( e.getX() < image2.width() && e.getX() > -1 && e.getY() < image2.height() && e.getY() > -1 ) {
                    double[] px;
                    px = image2.get( e.getY(), e.getX() );
                    double r, g, b;
                    int lum;
                    if ( px.length == 1 ) {
                        r = g = b = px[0];
                        lum = ( int ) px[0];
                    } else {
                        r = px[0];//(pixel & 0xff0000) >> 16;
                        g = px[1];//(pixel & 0x00ff00) >> 8;
                        b = px[2];//(pixel & 0x0000ff);
                        lum = ( int ) ( 0.299 * r + 0.587 * g + 0.114 * b );
                    }
                    int id = e.getX() + e.getY() * image2.width();
                    Linox.getInstance().getStatusBar().setStatus( "(" + e.getX() + ", " + e.getY() + ") id = " + id + " RGB(" + r + ", " + g + ", " + b + ") lum(" + lum + ")" );
                }
            }

        };
        imageView2.addMouseMotionListener( mouseMotionAdapter2 );

        imageScrollPane1 = new JScrollPane( imageView1, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED );
        imageScrollPane1.getVerticalScrollBar().setPreferredSize( new Dimension( 10, 0 ) );
        imageScrollPane1.getHorizontalScrollBar().setPreferredSize( new Dimension( 0, 10 ) );
        imageScrollPane2 = new JScrollPane( imageView2, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED );
        imageScrollPane2.getVerticalScrollBar().setPreferredSize( new Dimension( 10, 0 ) );
        imageScrollPane2.getHorizontalScrollBar().setPreferredSize( new Dimension( 0, 10 ) );


        this.add( imageScrollPane1 );
        this.add( imageScrollPane2 );
    }

    TestImageJPanel() {
        this.setLayout( new MigLayout() );
        imageView1 = new JLabel( "No image" );
        imageView2 = new JLabel( "No image" );
        title = "Empty";
        image1 = null;
        image2 = null;
        final JScrollPane imageScrollPane1 = new JScrollPane( imageView1 );
        final JScrollPane imageScrollPane2 = new JScrollPane( imageView2 );
        // imageScrollPane.setPreferredSize(new Dimension(90, 30));
        add( imageScrollPane1 );
        add( imageScrollPane2 );
    }

    public void setImage1( Mat _image ) {
        image1 = _image;
        imageView1.setIcon( new ImageIcon( matToBufferedImage( image1 ) ) );
    }

    public void setImage2( Mat _image ) {
        image2 = _image;
        imageView2.setIcon( new ImageIcon( matToBufferedImage( image2 ) ) );
    }

    public void setButton( Action action, boolean visible ) {
        if ( button != null ) {
            this.remove( button );
        }
        button = new JButton( action );
        button.setVisible( visible );
        this.add( button );
    }

    public void removeButton() {
        if ( button != null ) {
            this.remove( button );
        }
    }

    public void setMouseListener1( MouseAdapter adapter ) {
        if ( mouseAdapter != null ) {
            imageView1.removeMouseListener( mouseAdapter );
            imageView1.removeMouseMotionListener( mouseAdapter );
        } else {
            imageView1.removeMouseMotionListener( mouseMotionAdapter1 );
        }

        mouseAdapter = adapter;
        imageView1.addMouseListener( mouseAdapter );
        imageView1.addMouseMotionListener( mouseAdapter );

    }

    public void resetMouseMotionListener1() {
        imageView1.removeMouseListener( mouseAdapter );
        imageView1.removeMouseMotionListener( mouseAdapter );
        mouseAdapter = null;

        imageView1.addMouseMotionListener( mouseMotionAdapter1 );
    }

    public Mat getImage1() {
        return image1;
    }

    public String getTitle() {
        return title;
    }

    public void setMousePressed( boolean value ) {
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
    public BufferedImage matToBufferedImage( Mat matrix ) {
        int cols = matrix.cols();
        int rows = matrix.rows();
        int elemSize = ( int ) matrix.elemSize();
        byte[] data = new byte[cols * rows * elemSize];
        int type;

        matrix.get( 0, 0, data );

        switch ( matrix.channels() ) {
            case 1:
                type = BufferedImage.TYPE_BYTE_GRAY;
                break;

            case 3:
                type = BufferedImage.TYPE_3BYTE_BGR;

                // bgr to rgb
                byte b;
                for ( int i = 0; i < data.length; i = i + 3 ) {
                    b = data[i];
                    data[i] = data[i + 2];
                    data[i + 2] = b;
                }
                break;

            default:
                return null;
        }

        BufferedImage image = new BufferedImage( cols, rows, type );
        image.getRaster().setDataElements( 0, 0, cols, rows, data );

        return image;
    }

}
