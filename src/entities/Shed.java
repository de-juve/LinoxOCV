package entities;

import java.awt.*;
import java.util.ArrayList;
import java.util.Random;

public class Shed {
    private int label;
    private Color color;
    private Point canonical;
    public ArrayList<Point> points;

    public Shed(int _label, Color _color) {
        label = _label;
        color = _color;
        points = new ArrayList<>();
        canonical = new Point(-1, -1);
    }

    public Shed(int _label) {
        label = _label;
        color = new Color(new Random().nextFloat(), new Random().nextFloat(), new Random().nextFloat());
        points = new ArrayList<>();
    }

    public void addPoint(Point point) {
        points.add(point);
    }

    public int getLabel() {
        return label;
    }

    public Color getColor() {
        return color;
    }

    public Point getCanonical() {
        return canonical;
    }

    public void setCanonical(Point canonical) {
        this.canonical = canonical;
    }

    public int size() {
        return points.size();
    }
}
