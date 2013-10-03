package gui.menu;

import entities.DataCollector;
import gui.Linox;
import org.opencv.core.Mat;
import plugins.IPluginFilter;

import javax.swing.*;
import java.awt.*;

public class PluginRunner implements IPluginRunner {
    private volatile boolean shutdown = false;
    private IPluginFilter plugin;

    @Override
    public void setPlugin( IPluginFilter plugin ) {
        this.plugin = plugin;
    }

    @Override
    public void run() {
        try {
            shutdown = false;
            Linox.getInstance().getImageStore().setCursor( new Cursor( Cursor.WAIT_CURSOR ) );
            ( ( LinoxMenuStore ) Linox.getInstance().getMenuStore() ).setEnableEditToolsItems( false );
            // DataCollector.INSTANCE.clearHistory();
            ImageJPanel jPanel = Linox.getInstance().getImageStore().getSelectedTab();
            DataCollector.INSTANCE.setImageOriginal( jPanel.getTitle(), jPanel.getImage() );
            plugin.initImage( DataCollector.INSTANCE.getImageOriginal().clone() );
            plugin.addRunListener( this );
            plugin.run();
            while ( !shutdown ) {
                Thread.sleep( 10 );
            }
        } catch ( InterruptedException e ) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        } finally {
            ( ( LinoxMenuStore ) Linox.getInstance().getMenuStore() ).setEnableEditToolsItems( true );
            Linox.getInstance().getImageStore().setCursor( new Cursor( Cursor.DEFAULT_CURSOR ) );
        }
    }

    @Override
    public void stopPlugin() {
        JOptionPane.showMessageDialog( Linox.getInstance(), "plugin " + plugin.getTitle() + " stopped. Because: " + plugin.getErrMessage() );
        shutdown = true;
    }

    @Override
    public void finishPlugin() {
        shutdown = true;
    }

    @Override
    public void addImageTab() {
        DataCollector.INSTANCE.setImageResult( plugin.getTitle(), plugin.getResult( true ) );
        ( Linox.getInstance().getImageStore() ).addImageTab( DataCollector.INSTANCE.getImageResultTitle(), DataCollector.INSTANCE.getImageResult() );
    }

    @Override
    public void addImageTab( String title, Mat image ) {
        ( Linox.getInstance().getImageStore() ).addImageTab( title, image );
    }

    @Override
    public void replaceImageTab() {
        DataCollector.INSTANCE.setImageResult( plugin.getTitle(), plugin.getResult( true ) );
        ( Linox.getInstance().getImageStore() ).replaceImageTab( DataCollector.INSTANCE.getImageResultTitle(), DataCollector.INSTANCE.getImageResult() );
    }

    @Override
    public void replaceImageTab( String title, Mat image ) {
        ( Linox.getInstance().getImageStore() ).replaceImageTab( title, image );
    }


}
