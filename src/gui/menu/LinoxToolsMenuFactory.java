package gui.menu;

import gui.Linox;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.ArrayList;

public class LinoxToolsMenuFactory {
    private final ArrayList<JMenuItem> items = new ArrayList<>();

    public ArrayList<JMenuItem> getItems() {
        return items;
    }

    public LinoxToolsMenuFactory() {
        final Action show = new AbstractAction("Show history") {
            @Override
            public void actionPerformed(ActionEvent e) {
                Linox.getInstance().setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
                try {
                    ((LinoxMenuStore)Linox.getInstance().getMenuStore()).showHistory();
                } finally {
                    Linox.getInstance().setCursor(Cursor.getDefaultCursor());
                }
            }
        };

        final Action clear = new AbstractAction("Clear history") {
            @Override
            public void actionPerformed(ActionEvent e) {
                Linox.getInstance().setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
                try {
                    ((LinoxMenuStore)Linox.getInstance().getMenuStore()).clearHistory();
                } finally {
                    Linox.getInstance().setCursor(Cursor.getDefaultCursor());
                }
            }
        };

        items.add(new JMenuItem(show));
        items.add(new JMenuItem(clear));
    }
}
