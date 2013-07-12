package entities;

import java.util.TreeMap;

public enum ShedCollector {
    INSTANCE;

    private TreeMap<Integer, Shed> sheds;

    public void addShed(Shed shed) {
        if(sheds == null) {
            sheds = new TreeMap<>();
        }
        sheds.put(shed.getLabel(),shed);
    }

    public int size() {
        return sheds.size();
    }

    public void clear() {
        if(sheds == null) {
            sheds = new TreeMap<>();
        } else {
            sheds.clear();
        }
    }

    public void addElementToShed(Integer label, Point p) {
        sheds.get(label).addPoint(p);
    }
}
