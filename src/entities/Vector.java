package entities;

public class Vector {
    public int x;
    public int y;
    public double theta;

    public Vector( int x, int y ) {
        this.x = x;
        this.y = y;
        theta = Math.atan2( y, x );
    }

}
