package gui.menu;

import gui.Linox;
import plugins.PluginFilter;
import plugins.DataCollector;

import javax.swing.*;
import java.awt.*;

public class PluginRunner implements Runnable {
    private volatile boolean shutdown = false;
    private PluginFilter plugin;

    public void setPlugin(PluginFilter plugin) {
        this.plugin = plugin;
    }

    @Override
    public void run() {
        try {
            shutdown = false;
            Linox.getInstance().getImageStore().setCursor(new Cursor(Cursor.WAIT_CURSOR));
            Linox.getInstance().getImageStore().setCursorType(Cursor.WAIT_CURSOR);
            ((LinoxMenuStore) Linox.getInstance().getMenuStore()).setEnableEditToolsItems(false);
            // DataCollector.INSTANCE.clearHistory();
            plugin.initProcessor(DataCollector.INSTANCE.getImageOriginal().clone());
            plugin.addRunListener(this);
            plugin.run();
            while(!shutdown) {
                Thread.sleep(10);
            }
        } catch (InterruptedException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        } finally {
            ((LinoxMenuStore) Linox.getInstance().getMenuStore()).setEnableEditToolsItems(true);
            Linox.getInstance().getImageStore().setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
            Linox.getInstance().getImageStore().setCursorType(Cursor.DEFAULT_CURSOR);
        }
    }

    public void stopPlugin() {
        JOptionPane.showMessageDialog(Linox.getInstance(), "plugin " + plugin.getTitle() + " stopped. Because: " + plugin.getErrMessage());
        shutdown = true;
    }

    public void finishPlugin() {
        DataCollector.INSTANCE.setImageResult(plugin.getTitle() + " of ", plugin.getResult(true));
        DataCollector.INSTANCE.setMaxLuminance(255);

        (Linox.getInstance().getImageStore()).addImageTab(DataCollector.INSTANCE.getImageResultTitle(), DataCollector.INSTANCE.getImageResult());
        Linox.getInstance().setPreferredSize(new Dimension(Math.min(640, plugin.getResult(false).width() + 3), Math.min(480, plugin.getResult(false).height() + 3)));
        shutdown = true;
    }

    public void addImageTab() {
        DataCollector.INSTANCE.setImageResult(plugin.getTitle() + " of ", plugin.getResult(true));
        (Linox.getInstance().getImageStore()).addImageTab(DataCollector.INSTANCE.getImageResultTitle(), DataCollector.INSTANCE.getImageResult());
    }

}
