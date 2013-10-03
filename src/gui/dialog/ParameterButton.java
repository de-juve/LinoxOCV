package gui.dialog;

import entities.Point;
import net.miginfocom.swing.MigLayout;
import org.opencv.core.Mat;
import org.opencv.core.Scalar;

import javax.swing.*;
import java.util.ArrayList;

public class ParameterButton extends JPanel {
    private JButton button;
    private ArrayList<Point> points;
    private boolean mousePressed = false;

    public ParameterButton( Action action ) {
        this.setLayout( new MigLayout() );
        points = new ArrayList<>();
        button = new JButton( action );
        button.setVisible( true );
        this.add( button );
    }

    public void addPoint( Point point, int w, Scalar color, Mat img ) {
        for ( int row = ( int ) point.y - w / 2; row <= ( int ) point.y + w / 2; row++ ) {
            for ( int col = ( int ) point.x - w / 2; col <= ( int ) point.x + w / 2; col++ ) {
                Scalar colr = new Scalar( img.get( row, col )[0], img.get( row, col )[1], img.get( row, col )[2] );
                if ( colr.equals( color ) ) {
                    points.add( new Point( col, row ) );
                }
            }
        }
    }

    public void setMousePressed( boolean value ) {
        mousePressed = value;
    }

    public boolean getMousePressed() {
        return mousePressed;
    }

    public ArrayList<Point> getValue() {
        return points;
    }
}
