package gui.dialog;

import gui.Linox;
import net.miginfocom.swing.MigLayout;
import plugins.IPluginFilter;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

public class ParameterJPanel extends JPanel {
    private ArrayList<IPluginFilter> parameterListeners;

    private JButton buttonOK, buttonCancel, buttonFinish;
    JScrollPane scrollPane;
    JPanel parameterPanel;
    private ArrayList<ParameterSlider> parameterSliders;
    private ArrayList<ParameterComboBox> parameterComboBoxes;
    private ArrayList<ParameterButton> parameterButtons;

    public ParameterJPanel( String title, IPluginFilter listener ) {
        this.setLayout( new MigLayout() );
        this.setName( title );

        parameterListeners = new ArrayList<>();
        parameterSliders = new ArrayList<>();
        parameterComboBoxes = new ArrayList<>();
        parameterButtons = new ArrayList<>();

        parameterListeners.add( listener );

        parameterPanel = new JPanel( new MigLayout() );
        scrollPane = new JScrollPane( parameterPanel, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED );
        scrollPane.getVerticalScrollBar().setPreferredSize( new Dimension( 10, 0 ) );
        scrollPane.getHorizontalScrollBar().setPreferredSize( new Dimension( 0, 10 ) );

        buttonOK = new JButton( "OK" );
        buttonCancel = new JButton( "CANCEL" );
        buttonFinish = new JButton( "FINISH" );

        buttonOK.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                onOK();
            }
        } );

        buttonCancel.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                onCancel();
            }
        } );

        buttonFinish.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                onFinish();
            }
        } );

        this.add( scrollPane, "span" );
        this.add( buttonOK );
        this.add( buttonFinish );
        this.add( buttonCancel, "wrap" );
        this.setSize( new Dimension( 20, 300 ) );
    }

    public void addParameterSlider( ParameterSlider slider ) {
        parameterSliders.add( slider );
        this.parameterPanel.add( slider, "wrap" );
    }

    public int getValueSlider( ParameterSlider slider ) {
        return parameterSliders.get( parameterSliders.lastIndexOf( slider ) ).getValue();
    }

    public void addParameterComboBox( ParameterComboBox comboBox ) {
        parameterComboBoxes.add( comboBox );
        this.parameterPanel.add( comboBox, "wrap" );
    }

    public String getValueComboBox( ParameterComboBox comboBox ) {
        return parameterComboBoxes.get( parameterComboBoxes.lastIndexOf( comboBox ) ).getValue();
    }

    public void addParameterButton( ParameterButton button ) {
        parameterButtons.add( button );
        this.parameterPanel.add( button, "wrap" );
    }

    public ArrayList<entities.Point> getValueButton( ParameterButton button ) {
        return parameterButtons.get( parameterButtons.lastIndexOf( button ) ).getValue();
    }

    private void onOK() {
        final ParameterJPanel jpanel = this;
        Thread t = new Thread( new Runnable() {
            public void run() {
                Linox.getInstance().getImageStore().setCursor( new Cursor( Cursor.WAIT_CURSOR ) );
                for ( IPluginFilter listener : parameterListeners ) {
                    listener.getParams( jpanel );
                }
                Linox.getInstance().getImageStore().setCursor( new Cursor( Cursor.DEFAULT_CURSOR ) );
            }
        } );
        t.start();

    }

    private void onCancel() {
        for ( IPluginFilter listener : parameterListeners ) {
            listener.cancel();
        }
    }

    private void onFinish() {
        for ( IPluginFilter listener : parameterListeners ) {
            listener.finish();
        }
    }
}
