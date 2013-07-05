package gui.dialog;

import net.miginfocom.swing.MigLayout;
import plugins.PluginFilter;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

public class ParameterJPanel extends JPanel {
    private ArrayList<PluginFilter> parameterListeners;

    private JButton buttonOK, buttonCancel, buttonFinish;
    JScrollPane scrollPane;
    JPanel parameterPanel;
    private ArrayList<ParameterSlider> parameterSliders;
    private ArrayList<ParameterComboBox> parameterComboBoxes;

    public ParameterJPanel(String title, PluginFilter listener) {
        this.setLayout(new MigLayout());
        this.setName(title);

        parameterListeners = new ArrayList<>();
        parameterSliders = new ArrayList<>();
        parameterComboBoxes = new ArrayList<>();

        parameterListeners.add(listener);

        parameterPanel = new JPanel(new MigLayout());
        scrollPane = new JScrollPane(parameterPanel, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        scrollPane.getVerticalScrollBar().setPreferredSize(new Dimension(10, 0));
        scrollPane.getHorizontalScrollBar().setPreferredSize(new Dimension(0, 10));

        buttonOK = new JButton("OK");
        buttonCancel = new JButton("CANCEL");
        buttonFinish = new JButton("FINISH");

        buttonOK.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onOK();
            }
        });

        buttonCancel.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onCancel();
            }
        });

        buttonFinish.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onFinish();
            }
        });

        this.add(scrollPane, "span");
        this.add(buttonOK);
        this.add(buttonFinish);
        this.add(buttonCancel, "wrap");
        this.setSize(new Dimension(20, 300));
    }

    public void addParameterSlider(ParameterSlider slider) {
        parameterSliders.add(slider);
        this.parameterPanel.add(slider, "wrap");
    }

    public int getValueSlider(ParameterSlider slider) {
        return parameterSliders.get(parameterSliders.lastIndexOf(slider)).getValue();
    }

    public void addParameterComboBox(ParameterComboBox comboBox) {
        parameterComboBoxes.add(comboBox);
        this.parameterPanel.add(comboBox, "wrap");
    }

    public String getValueComboBox(ParameterComboBox comboBox) {
        return parameterComboBoxes.get(parameterComboBoxes.lastIndexOf(comboBox)).getValue();
    }

    private void onOK() {
       for(PluginFilter listener : parameterListeners) {
           listener.getParams(this);
       }
    }

    private void onCancel() {
        for(PluginFilter listener : parameterListeners) {
            listener.cancel();
        }
    }

    private void onFinish() {
        for(PluginFilter listener : parameterListeners) {
            listener.finish();
        }
    }
}
