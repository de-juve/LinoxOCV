package plugins.roadNetwork;

import entities.DataCollector;
import entities.Direction;
import entities.Line;
import entities.Point;
import gui.Linox;
import gui.dialog.ParameterJPanel;
import gui.dialog.ParameterSlider;
import org.opencv.core.Mat;
import plugins.AbstractPlugin;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

public class Builder extends AbstractPlugin {
    ParameterSlider thresSlider;
    private int thres;

    public Builder() {
        title = "Road builder";
    }

    @Override
    public void run() {
        Linox.getInstance().getStatusBar().setProgress( title, 0, 100 );

        showParamsPanel( "Choose params" );
        if ( exit ) {
            return;
        }
    }

    public Mat build( Mat image, ArrayList<Line> lines ) {
        Mat result;
        Line line = lines.get( 0 );
        Iterator<Point> iterator = line.points.iterator();

        while ( iterator.hasNext() ) {
            Point p = iterator.next();
            HashMap<Direction, Double> map = new HashMap<>();

            for ( Direction d : Direction.values() ) {
                double w = letRay( p, d );
                map.put( d, w );
            }

            Direction bestDirection = Direction.NORTH;
            for ( Direction d : Direction.values() ) {
                double w1 = map.get( d );
                double w2 = map.get( d.opposite() );
                if ( Math.abs( w1 - w2 ) < thres ) {
                    if ( map.get( bestDirection ) > Math.min( map.get( d ), map.get( d.opposite() ) ) ) {
                        bestDirection = d;
                    }
                }
            }
            p.direction = bestDirection;


        }
        return image;
    }

    private double letRay( Point p, Direction direction ) {
        double weight = 0;
        boolean doing = true;
        while ( doing ) {
            Point n = direction.getNeighboure( p );
            if ( n.y < 0 || n.y > image.height() || n.x < 0 || n.x > image.width() ) {
                doing = false;
            } else {
                if ( Math.abs( image.get( n.y, n.x )[0] - image.get( p.y, p.x )[0] ) <= thres ) {
                    weight += direction.weight();
                    p = n;
                } else {
                    doing = false;
                }
            }
        }
        return weight;
    }

    @Override
    public void getParams( ParameterJPanel panel ) {
        ArrayList<Line> lines = DataCollector.INSTANCE.getLines();
        if ( lines.isEmpty() ) {
            errMessage = "Empty lines!";
            return;
        }
        thres = panel.getValueSlider( thresSlider );
        result = build( image, lines );

        if ( tabs == 0 ) {
            pluginListener.addImageTab();
            tabs++;
        } else {
            pluginListener.replaceImageTab();
        }
    }

    protected void showParamsPanel( String name ) {
        thresSlider = new ParameterSlider( "Thres :", 1, 50, 5 );

        ParameterJPanel panel = new ParameterJPanel( name, this );
        panel.addParameterSlider( thresSlider );

        Linox.getInstance().addParameterJPanel( panel );
    }
}
