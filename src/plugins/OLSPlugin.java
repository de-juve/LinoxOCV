package plugins;

import entities.DataCollector;
import entities.Line;
import entities.Point;
import gui.Linox;
import gui.dialog.ParameterComboBox;
import gui.dialog.ParameterJPanel;
import gui.dialog.ParameterSlider;
import org.opencv.core.Mat;
import plugins.approximation.Regression;

import java.util.ArrayList;

public class OLSPlugin extends AbstractPlugin {
    ParameterSlider polynomDegree = new ParameterSlider( "Polynomial degree :", 1, 12, 1 );
    ParameterComboBox function = new ParameterComboBox( "Type of function:",
            new String[]{ "Polynomial", "Parabola", "Sin" } );

    public OLSPlugin() {
        title = "Ordinary least squares";
    }

    @Override
    public void run() {
        Linox.getInstance().getStatusBar().setProgress( title, 0, 100 );
        showParamsPanel( "Choose params" );
        if ( exit ) {
            return;
        }
    }

    @Override
    public void getParams( ParameterJPanel panel ) {
        int degree = panel.getValueSlider( polynomDegree );
        String name = panel.getValueComboBox( function );

        result = regress( image, degree, name );

        if ( tabs == 0 ) {
            pluginListener.addImageTab();
            tabs++;
        } else {
            pluginListener.replaceImageTab();
        }
    }

    public static Mat regress( Mat image, int polynomialDegree, String functionName ) {
        Mat result = new Mat( image.size(), image.type() );
        Regression regressionX = new Regression();
        Regression regressionY = new Regression();

        ArrayList<Line> lines = DataCollector.INSTANCE.getLines();
        for ( int i = 0; i < lines.size(); i++ ) {
            Line line = lines.get( i );
            Line x = new Line();
            Line y = new Line();
            for ( int j = 0; j < line.points.size(); j++ ) {
                Point p = line.points.get( j );
                x.add( new Point( i, p.x ) );
                y.add( new Point( i, p.y ) );
            }
            regressionX.calcFitParams( x, functionName, polynomialDegree );
            regressionY.calcFitParams( y, functionName, polynomialDegree );

            int[] xx = new int[line.points.size() + 40];
            int[] yy = new int[line.points.size() + 40];

            for ( int j = 0; j < line.points.size() + 40; j++ ) {
                xx[j] = regressionX.getY( j );
                yy[j] = regressionY.getY( j );
                if ( xx[j] >= image.width() || xx[j] < 0 || yy[j] >= image.height() || yy[j] < 0 ) {
                    break;
                }
                result.put( yy[j], xx[j], 255, 0, 0 );
            }
        }
        return result;
    }

    protected void showParamsPanel( String name ) {

        ParameterJPanel panel = new ParameterJPanel( name, this );
        panel.addParameterSlider( polynomDegree );
        panel.addParameterComboBox( function );

        Linox.getInstance().addParameterJPanel( panel );
    }
}
