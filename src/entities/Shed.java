package entities;

import java.util.ArrayList;

public class Shed {
    private int label;
    public ArrayList<Point> points;

    public Shed(int _label) {
        label = _label;
        points = new ArrayList<>();
    }

    public void addPoint(Point point) {
        points.add(point);
    }

    public int getLabel() {
        return label;
    }
}
