package gui;

import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import javax.swing.border.BevelBorder;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.PrintStream;


public class StatusBar extends JPanel implements MouseListener {
    private final JTextArea debugArea;
    private final JLabel statusText;
    private final JProgressBar progressBar;
    private PrintStream standardOut;


    public StatusBar() {

        statusText = new JLabel( getInfoString( false ) );
        statusText.setBorder( new BevelBorder( BevelBorder.LOWERED ) );
        progressBar = new JProgressBar();
        progressBar.setVisible( true );
        debugArea = new JTextArea(5, 50);
        debugArea.setEditable(false);
        PrintStream printStream = new PrintStream(new CustomOutputStream(debugArea));
        // keeps reference of standard output stream
        standardOut = System.out;

        // re-assigns standard output stream and error output stream
      //  System.setOut(printStream);
      //  System.setErr(printStream);

        setLayout(new MigLayout());
        //add( statusText, BorderLayout.PAGE_START );
        //add( progressBar, BorderLayout.PAGE_END );
        add( statusText, "growx, h 20!, wrap" );
        add( new JScrollPane(debugArea), "wrap" );
        add( progressBar, "" );
        statusText.addMouseListener( this );
    }

    public void setStatus( final String message ) {
        final String text;
        if ( message == null || message.isEmpty() ) text = " ";
        else text = message;
        statusText.setText( text );
    }

    public void setProgress( final String message, final int val, final int max ) {
        setStatus( message );
        setProgress( val, max );
    }

    public void setProgress( final int val, final int max ) {
        if ( max < 0 ) {
            progressBar.setVisible( false );
            return;
        }

        if ( val >= 0 && val < max ) {
            progressBar.setValue( val );
            progressBar.setMaximum( max );
            progressBar.setVisible( true );
        } else {
            progressBar.setVisible( false );
        }
    }


    private String getInfoString( final boolean mem ) {
        final String javaVersion = System.getProperty( "java.version" );
        final String osArch = System.getProperty( "os.arch" );
        final long maxMem = Runtime.getRuntime().maxMemory();
        final long totalMem = Runtime.getRuntime().totalMemory();
        final long freeMem = Runtime.getRuntime().freeMemory();
        final long usedMem = totalMem - freeMem;
        final long usedMB = usedMem / 1048576;
        final long maxMB = maxMem / 1048576;
        final StringBuilder sb = new StringBuilder();
        sb.append( "Java " + javaVersion + " [" +
                osArch + "]" );
        if ( mem ) {
            sb.append( "; " + usedMB + "MB of " + maxMB + "MB" );
        }
        return sb.toString();
    }

    public void mouseClicked( MouseEvent mouseEvent ) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    public void mousePressed( MouseEvent mouseEvent ) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    public void mouseReleased( MouseEvent mouseEvent ) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    public void mouseEntered( MouseEvent mouseEvent ) {
        //ImagePlus img = WindowManager.getCurrentImage();
        ///	setProgress(val, max);
        //To change body of implemented methods use File | Settings | File Templates.
    }

    public void mouseExited( MouseEvent mouseEvent ) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    /**
     * Prints log statements for testing in a thread
     */
    private void printLog() {
        /*Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    System.out.println("Time now is " + (new Date()));
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException ex) {
                        ex.printStackTrace();
                    }
                }
            }
        });
        thread.start();*/
    }
}
