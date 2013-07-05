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
}
