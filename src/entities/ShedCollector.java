package entities;

import java.util.ArrayList;

public enum ShedCollector {
    INSTANCE;

    private ArrayList<Shed> sheds;

    public void addShed(Shed shed) {
        if(sheds == null) {
            sheds = new ArrayList<>();
        }
        sheds.add(shed);
    }

    public int size() {
        return sheds.size();
    }

    public void clear() {
        if(sheds == null) {
            sheds = new ArrayList<>();
        } else {
            sheds.clear();
        }
    }

    public void addElementToShed(int label, Point p) {
        sheds.get(label).addPoint(p);
    }
}
