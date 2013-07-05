package gui.menu;

import gui.Linox;
import entities.DataCollector;
import org.opencv.core.Mat;
import org.opencv.highgui.Highgui;

import javax.swing.*;
import javax.swing.filechooser.FileFilter;

import java.util.Map;
import java.util.TreeMap;

import static javax.swing.JOptionPane.ERROR_MESSAGE;
import static javax.swing.JOptionPane.showMessageDialog;

public class LinoxMenuStore extends JMenuBar {
    private JMenu filemenu, editmenu, toolsmenu;
    private LinoxFileMenuFactory fileFactory;
    private LinoxEditMenuFactory editFactory;
    private LinoxToolsMenuFactory toolsFactory;

    public LinoxMenuStore() {
        fileFactory = new LinoxFileMenuFactory();
        editFactory = new LinoxEditMenuFactory();
        toolsFactory = new LinoxToolsMenuFactory();

        filemenu = new JMenu("File");
        filemenu.add(new JSeparator());

        for(JMenuItem item : fileFactory.getItems()) {
            filemenu.add(item);
        }

        editmenu = new JMenu("Edit");

        for(JMenuItem item : editFactory.getItems()) {
            editmenu.add(item);
        }

        toolsmenu = new JMenu("Tools");
        toolsmenu.add(new JSeparator());

        for(JMenuItem item : toolsFactory.getItems()) {
            toolsmenu.add(item);
        }

        //editmenu.setEnabled(false);
        toolsmenu.setEnabled(false);

        this.add(filemenu);
        this.add(editmenu);
        this.add(toolsmenu);
    }

    public void setEnableEditToolsItems(boolean option) {
        editmenu.setEnabled(option);
        for(int i = 0; i < editmenu.getItemCount(); i++) {
            editmenu.getItem(i).setEnabled(option);
        }
        toolsmenu.setEnabled(option);
    }

    public Mat openImage() {
        JFileChooser fileChooser = new JFileChooser(System.getProperty("user.dir") + "/resource");

        FileFilter filter1 = new ExtensionFileFilter("JPG and JPEG", new String[]{"JPG", "JPEG"});
        fileChooser.setFileFilter(filter1);
        FileFilter filter2 = new ExtensionFileFilter("PNG and BMP", new String[]{"PNG", "BMP"});
        fileChooser.setFileFilter(filter2);
        FileFilter filter3 = new ExtensionFileFilter("ALL", new String[]{"JPG", "JPEG", "PNG", "BMP", "TIFF", "GIF"});
        fileChooser.setFileFilter(filter3);
        // Ask user for the location of the image file
        if (fileChooser.showOpenDialog(null) != JFileChooser.APPROVE_OPTION) {
            return null;
        }
        String path = fileChooser.getSelectedFile().getAbsolutePath();
        Mat newImage = Highgui.imread(path, Highgui.CV_LOAD_IMAGE_COLOR);

        // opencv_core.IplImage newImage = cvLoadImage(path, CV_LOAD_IMAGE_GRAYSCALE);

        if (newImage != null) {
            (Linox.getInstance().getImageStore()).addImageTab(fileChooser.getSelectedFile().getName(), newImage);
            DataCollector.INSTANCE.setImageOriginal(fileChooser.getSelectedFile().getName(), newImage);
            setEnableEditToolsItems(true);
            return  newImage;
        } else {
            showMessageDialog(Linox.getInstance(), "Cannot open image file: " + path, Linox.getInstance().getTitle(), ERROR_MESSAGE);
            return null;
        }
    }

    public boolean saveImage(String path, Mat image) {
        Highgui.imwrite(path, image);
        return true;
    }

    public boolean saveImage(Mat image) {
        JFileChooser fileChooser = new JFileChooser(System.getProperty("user.dir") + "/resource");
        FileFilter filter1 = new ExtensionFileFilter("ALL", new String[]{"JPG", "JPEG", "PNG", "BMP", "TIFF", "GIF"});
        fileChooser.setFileFilter(filter1);

        // Ask user for the location of the image file
        if (fileChooser.showSaveDialog(null) != JFileChooser.APPROVE_OPTION) {
            return false;
        }
        String path = fileChooser.getSelectedFile().getAbsolutePath();
        Highgui.imwrite(path, image);

        return true;
    }

    public void closeImage() {
        (Linox.getInstance().getImageStore()).removeSelectedImageTab();
        DataCollector.INSTANCE.setImageOriginal("empty", null);
    }

    public void exit() {
        System.exit(0);
    }

    void showHistory() {
        TreeMap<String, Mat> stack =  DataCollector.INSTANCE.getHistoryStack();

        for (Map.Entry<String, Mat> entry : stack.entrySet())
        {
            Linox.getInstance().getImageStore().addImageTab(entry.getKey(), entry.getValue());
        }
    }

    void clearHistory() {
        DataCollector.INSTANCE.clearHistory();
    }

}
