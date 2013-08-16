package gui.menu;

import plugins.IPluginFilter;

public interface IPluginRunner extends Runnable {
    void addImageTab();

    void replaceImageTab();

    void setPlugin( IPluginFilter plugin );

    void stopPlugin();

    void finishPlugin();
}
