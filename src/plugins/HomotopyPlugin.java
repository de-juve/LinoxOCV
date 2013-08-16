package plugins;

import entities.DataCollector;
import gui.Linox;
import gui.dialog.ParameterJPanel;
import gui.dialog.ParameterSlider;
import org.opencv.core.Mat;

import java.util.HashSet;
import java.util.PriorityQueue;

public class HomotopyPlugin extends AbstractPlugin {
    private int areaSizeX, areaSizeY;
    private int deviation;
    private boolean shift;
    private boolean[] visited;
    private HashSet<Integer> visited2;
    private HashSet<Integer> borders;
    private HashSet<Integer> area;
    private PriorityQueue<Integer> queue;
    private int backgroudColor;
    private int foregroundColor;
    private int leftX, upY;
    private boolean finish;
    int[] grayscale;
    Mat gray;

    ParameterSlider xSizeSlider;
    ParameterSlider ySizeSlider;
    ParameterSlider deviationSlider;

    public HomotopyPlugin() {
        title = "Homotopy";
    }


    @Override
    public void run() {
        Linox.getInstance().getStatusBar().setProgress( title, 0, 100 );
        GrayscalePlugin.run( image, true );
        showParamsPanel( "Choose params" );
        if ( exit ) {
            return;
        }
    }

    @Override
    public void getParams( ParameterJPanel panel ) {
        areaSizeX = panel.getValueSlider( xSizeSlider );
        areaSizeY = panel.getValueSlider( ySizeSlider );
        deviation = panel.getValueSlider( deviationSlider );

        homotopy();

        if ( tabs == 0 ) {
            pluginListener.addImageTab();
            tabs++;
        } else {
            pluginListener.replaceImageTab();
        }
    }

    protected void showParamsPanel( String name ) {
        xSizeSlider = new ParameterSlider( "X size of area:", Math.min( 3, image.width() ), image.width(), 50 );
        ySizeSlider = new ParameterSlider( "Y size of area:", Math.min( 3, image.width() ), image.width(), 50 );
        deviationSlider = new ParameterSlider( "Deviation:", 1, 50, 25 );

        ParameterJPanel panel = new ParameterJPanel( name, this );
        panel.addParameterSlider( xSizeSlider );
        panel.addParameterSlider( ySizeSlider );
        panel.addParameterSlider( deviationSlider );

        Linox.getInstance().addParameterJPanel( panel );
    }

    private void homotopy() {
        finish = shift = false;
        gray = DataCollector.INSTANCE.getGrayImg();
        grayscale = new int[( int ) image.total()];
        for ( int i = 0; i < grayscale.length; i++ ) {
            grayscale[i] = ( int ) gray.get( y( i ), x( i ) )[0];
        }
        leftX = upY = 0;

        while ( leftX + areaSizeX <= image.width() && upY + areaSizeY <= image.height() ) {
            deleteArea();
            defineNewBounds();
            if ( finish ) {
                break;
            }
        }

        result = new Mat( image.rows(), image.cols(), image.type() );
        byte[] buff = new byte[( int ) image.total() * image.channels()];

        int j = 0;
        for ( int i = 0; i < grayscale.length; i++ ) {
            for ( int k = 0; k < image.channels(); k++ ) {
                buff[j] = ( byte ) grayscale[i];
                j++;
            }
        }
        result.put( 0, 0, buff );
    }


    private void deleteArea() {
        visited = new boolean[image.width() * image.height()];
        for ( int iy = upY; iy <= upY + areaSizeY; iy++ ) {
            X:
            for ( int ix = leftX; ix <= leftX + areaSizeX; ix++ ) {
                int id = id( ix, iy );
                if ( !visited[id] ) {
                    visited[id] = true;
                    if ( isBorderOfArea( id ) ) {
                        continue;
                    }
                    area = new HashSet<>();
                    borders = new HashSet<>();
                    queue = new PriorityQueue<>();
                    visited2 = new HashSet<>();

                    foregroundColor = ( int ) gray.get( iy, ix )[0];
                    area.add( id );
                    queue.add( id );
                    visited2.add( id );

                    while ( !queue.isEmpty() ) {
                        id = queue.remove();
                        fillArea( id );
                    }
                    if ( !area.isEmpty() ) {
                        backgroudColor = -1;
                        for ( Integer border : borders ) {
                            if ( !isBackgroundPixel( border ) ) {
                                borders.clear();
                                area.clear();
                                continue X;
                            }
                        }
                        for ( Integer i : area ) {
                            grayscale[i] = backgroudColor;
                        }
                    }
                }
            }
        }
    }

    private void fillArea( int id ) {
        int x = x( id );
        int y = y( id );
        for ( int iy = y - 1; iy <= y + 1; iy++ ) {
            for ( int ix = x - 1; ix <= x + 1; ix++ ) {
                if ( iy < upY || iy > upY + areaSizeY || ix < leftX || ix > leftX + areaSizeX ) {
                    continue;
                }
                int neigh = id( ix, iy );
                if ( visited2.contains( neigh ) ) {
                    continue;
                }
                if ( isBorderOfArea( neigh ) && isForegroundPixel( neigh ) ) {
                    visited[neigh] = true;
                    queue.clear();
                    borders.clear();
                    area.clear();
                    visited2.clear();
                    return;
                }

                visited[neigh] = true;
                visited2.add( neigh );
                if ( isForegroundPixel( neigh ) ) {
                    area.add( neigh );
                    queue.add( neigh );
                } else {
                    borders.add( neigh );
                }
            }
        }
    }

    private boolean isBorderOfArea( int id ) {
        int x = x( id );
        int y = y( id );
        return ( x == leftX ) || ( x == leftX + areaSizeX ) || ( y == upY ) || ( y == upY + areaSizeY ) ? true : false;
    }

    private boolean isForegroundPixel( int id ) {
        return ( ( int ) gray.get( y( id ), x( id ) )[0] <= foregroundColor + deviation ) && ( ( int ) gray.get( y( id ), x( id ) )[0] >= foregroundColor - deviation ) ? true : false;
    }

    private boolean isBackgroundPixel( int id ) {
        if ( backgroudColor < 0 ) {
            backgroudColor = ( int ) gray.get( y( id ), x( id ) )[0];
        }
        return ( ( int ) gray.get( y( id ), x( id ) )[0] <= backgroudColor + deviation ) && ( ( int ) gray.get( y( id ), x( id ) )[0] >= backgroudColor - deviation ) ? true : false;
    }

    private void defineNewBounds() {
        if ( leftX + 2 * areaSizeX < image.width() ) {
            leftX += areaSizeX;
        } else if ( upY + 2 * areaSizeY > image.height() ) {
            finish = true;
        } else {
            if ( shift ) {
                leftX = areaSizeX / 2;
            } else {
                leftX = 0;
            }
            upY += areaSizeY / 2;
            shift = !shift;
        }
    }
}
