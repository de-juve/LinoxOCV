package gui.menu;

import gui.Linox;
import org.opencv.core.Mat;
import plugins.DataCollector;
import sun.awt.image.ImageAccessException;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.util.ArrayList;

public class LinoxImageStore extends JTabbedPane {
    private TabCloseUI closeUI = new TabCloseUI(this);
    private ArrayList<String> titles;

    private LinoxImageFactory imageFactory;

    public LinoxImageStore() {
        imageFactory = new LinoxImageFactory();
        titles = new ArrayList<>();
    }

    public void addImageTab(String title, Mat image) {
        this.addTab(title+"   ", imageFactory.addImage(title, image));
        titles.add(title);
    }

    public void removeSelectedImageTab() {
        titles.remove(this.getTitleAt(this.getSelectedIndex()).trim());
        closeUI.removeSelectedTab();
    }

    public boolean tabAboutToClose(int tabIndex) {
        return true;
    }

    public ImageJPanel getSelectedTab() {
        return (ImageJPanel) this.getSelectedComponent();
    }

    public void setCursorType(int cursorType) {
        closeUI.cursorType = cursorType;
    }

    public ArrayList<String> getTitles() {
        return titles;
    }

    @Override
    public void paint(Graphics g) {
        super.paint(g);
        closeUI.paint(g);
    }

    public Mat getImage(String title) throws ImageAccessException {
        for(int i = 0; i < this.getTabCount(); i++) {
            if(this.getTitleAt(i).trim().equals(title)) {
                return ((ImageJPanel) this.getComponentAt(i)).getImage();
            }
        }
        throw new ImageAccessException("Not found image with this title " + title);
    }

    private boolean hasTabs() {
        return imageFactory.getItems().size() > 0;
    }

    private class TabCloseUI implements MouseListener, MouseMotionListener {
        private LinoxImageStore  tabbedPane;
        private int closeX = 0 ,closeY = 0, meX = 0, meY = 0;
        private int selectedTab;
        private final int  width = 8, height = 8;
        private Rectangle rectangle = new Rectangle(0,0,width, height);
        private int cursorType = Cursor.DEFAULT_CURSOR;

        private TabCloseUI(){}

        public TabCloseUI(LinoxImageStore pane) {
            tabbedPane = pane;
            tabbedPane.addMouseMotionListener(this);
            tabbedPane.addMouseListener(this);
        }

        public void removeSelectedTab() {
            if(tabbedPane.hasTabs()) {
                boolean isToCloseTab = tabAboutToClose(selectedTab);
                if (isToCloseTab && selectedTab > -1){
                    tabbedPane.getTitles().remove(tabbedPane.getTitleAt(selectedTab).trim());
                    tabbedPane.removeTabAt(selectedTab);
                }
            }
        }

        public String getTitle() {
            return tabbedPane.getTitleAt(selectedTab);
        }

        @Override
        public void mouseClicked(MouseEvent e) {
            try {
                ImageJPanel imageJPanel = (ImageJPanel) tabbedPane.getSelectedComponent();
                DataCollector.INSTANCE.setImageOriginal(imageJPanel.getTitle(),imageJPanel.getImage());

                Linox.getInstance().getStatusBar().setStatus(DataCollector.INSTANCE.getImageOriginalTitle() + ": width = " + DataCollector.INSTANCE.getWidth() + " height = " + DataCollector.INSTANCE.getHeight());
            } catch( NullPointerException ex) {}
        }

        @Override
        public void mousePressed(MouseEvent e) {
        }

        @Override
        public void mouseEntered(MouseEvent e) {
        }

        @Override
        public void mouseExited(MouseEvent e) {
        }

        @Override
        public void mouseDragged(MouseEvent e) {
        }

        @Override
        public void mouseReleased(MouseEvent me) {
            if(closeUnderMouse(me.getX(), me.getY())){
                removeSelectedTab();
                selectedTab = tabbedPane.getSelectedIndex();
            }
        }

        @Override
        public void mouseMoved(MouseEvent me) {
            meX = me.getX();
            meY = me.getY();
            if(mouseOverTab(meX, meY)){
                controlCursor();
                tabbedPane.repaint();
            }
        }

        private void controlCursor() {
            if(tabbedPane.getTabCount()>0)
                if(closeUnderMouse(meX, meY)){
                    tabbedPane.setCursor(new Cursor(Cursor.HAND_CURSOR));
                    if(selectedTab > -1)
                        tabbedPane.setToolTipTextAt(selectedTab, "Close " +tabbedPane.getTitleAt(selectedTab));
                }
                else{
                    tabbedPane.setCursor(new Cursor(cursorType));
                    if(selectedTab > -1)
                        tabbedPane.setToolTipTextAt(selectedTab,"");
                }
        }

        private boolean closeUnderMouse(int x, int y) {
            rectangle.x = closeX;
            rectangle.y = closeY;
            return rectangle.contains(x,y);
        }

        public void paint(Graphics g) {

            int tabCount = tabbedPane.getTabCount();
            for(int j = 0; j < tabCount; j++)
                if(tabbedPane.getComponent(j).isShowing()){
                    int x = tabbedPane.getBoundsAt(j).x + tabbedPane.getBoundsAt(j).width -width-5;
                    int y = tabbedPane.getBoundsAt(j).y +5;
                    drawClose(g,x,y);
                    break;
                }
            if(mouseOverTab(meX, meY)){
                drawClose(g,closeX,closeY);
            }
        }

        private void drawClose(Graphics g, int x, int y) {
            if(tabbedPane != null && tabbedPane.getTabCount() > 0){
                Graphics2D g2 = (Graphics2D)g;
                drawColored(g2, isUnderMouse(x,y)? Color.RED : Color.WHITE, x, y);
            }
        }

        private void drawColored(Graphics2D g2, Color color, int x, int y) {
            g2.setStroke(new BasicStroke(5,BasicStroke.JOIN_ROUND,BasicStroke.CAP_ROUND));
            g2.setColor(Color.BLACK);
            g2.drawLine(x, y, x + width, y + height);
            g2.drawLine(x + width, y, x, y + height);
            g2.setColor(color);
            g2.setStroke(new BasicStroke(3, BasicStroke.JOIN_ROUND, BasicStroke.CAP_ROUND));
            g2.drawLine(x, y, x + width, y + height);
            g2.drawLine(x + width, y, x, y + height);

        }

        private boolean isUnderMouse(int x, int y) {
            if(Math.abs(x-meX)<width && Math.abs(y-meY)<height )
                return  true;
            return  false;
        }

        private boolean mouseOverTab(int x, int y) {
            int tabCount = tabbedPane.getTabCount();
            for(int j = 0; j < tabCount; j++)
                if(tabbedPane.getBoundsAt(j).contains(meX, meY)){
                    selectedTab = j;
                    closeX = tabbedPane.getBoundsAt(j).x + tabbedPane.getBoundsAt(j).width -width-5;
                    closeY = tabbedPane.getBoundsAt(j).y +5;
                    return true;
                }
            return false;
        }

    }

}
