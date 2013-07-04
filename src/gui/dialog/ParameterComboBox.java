package gui.dialog;

import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class ParameterComboBox extends JPanel implements ActionListener {
    private JLabel label;
    private JComboBox<String> comboBox;
    private String value;

    public ParameterComboBox(String name, String[] values) {
        this.setLayout(new MigLayout());

        label = new JLabel(name);
        comboBox = new JComboBox<>(values);
        comboBox.setSelectedIndex(0);
        value = (String) comboBox.getSelectedItem();
        comboBox.addActionListener(this);

        this.add(label);
        this.add(comboBox);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        value = (String)((JComboBox<String>) e.getSource()).getSelectedItem();
    }

    public String getValue() {
        return value;
    }
}
