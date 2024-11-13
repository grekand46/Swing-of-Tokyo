package ui;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.util.function.*;

import javax.swing.JComponent;

public class WindowResizeHandler extends MouseAdapter {
    private App root;
    private Cursor cursor;
    private Function<App, Point> anchorFunc;
    private Date lastTime;
    private Point offset;
    private Dimension minSize = new Dimension(400, 300);
    private int xDir;
    private int yDir;

    public WindowResizeHandler(App root, Cursor cursor, Function<App, Point> anchorFunc, int xDir, int yDir) {
        this.root = root;
        this.cursor = cursor;
        this.anchorFunc = anchorFunc;
        this.xDir = xDir;
        this.yDir = yDir;
    }

    @Override
    public void mouseEntered(MouseEvent e) {
        if(root.getMouseCapture() != null || root.isMaximized()) return;
        root.setCursor(cursor);
    }
    @Override
    public void mouseExited(MouseEvent e) {
        if(root.getMouseCapture() != null || root.isMaximized()) return;
        root.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
        root.setMouseCapture(null);
    }

    private Point lastPoint;
    private Point anchor;
    @Override
    public void mousePressed(MouseEvent e) {
        if(e.getButton() != MouseEvent.BUTTON1 || root.isMaximized()) return;
        lastPoint = e.getLocationOnScreen();
        anchor = anchorFunc.apply(root);
        lastTime = new Date();
        root.setMouseCapture(e.getSource());
        Point componentLocation = ((JComponent) e.getSource()).getLocationOnScreen();
        offset = new Point(lastPoint.x - componentLocation.x, lastPoint.y - componentLocation.y);
    } 
    @Override
    public void mouseReleased(MouseEvent e) {
        if(e.getButton() != MouseEvent.BUTTON1 || root.isMaximized()) return;
        lastPoint = null;
        lastTime = null;
        anchor = null;
        root.setMouseCapture(null);
    }
    @Override
    public void mouseDragged(MouseEvent e) {
        if(lastPoint == null || lastTime == null || root.isMaximized()) return;
        Date currentTime = new Date();
        if(currentTime.getTime() - lastTime.getTime() < 1000 / 60) return;
        Point current = e.getLocationOnScreen();
        Point effectiveLocation = new Point(current.x + xDir * offset.x, current.y + yDir * offset.y);
        resize(anchor, effectiveLocation);
        root.onResize();
        lastTime = currentTime;
    } 

    private void resize(Point anchor, Point cursor) {
        int x = 0;
        int y = 0;
        int width = 0;
        int height = 0;
        if(xDir == 1) {
            x = root.getX();
            width = Math.max((int) minSize.getWidth(), cursor.x - anchor.x);
        }
        else if(xDir == -1) {
            int minWidth = (int) minSize.getWidth();
            x = Math.min(cursor.x, root.getX() + root.getWidth() - minWidth);
            width = Math.max(minWidth, anchor.x - cursor.x);
        }
        else {
            x = root.getX();
            width = root.getWidth();
        }

        if(yDir == 1) {
            y = root.getY();
            height = Math.max((int) minSize.getHeight(), cursor.y - anchor.y);
        }
        else if(yDir == -1) {
            int minHeight = (int) minSize.getHeight();
            y = Math.min(cursor.y, root.getY() + root.getHeight() - minHeight);
            height = Math.max(minHeight, anchor.y - cursor.y);
        }
        else {
            y = root.getY();
            height = root.getHeight();
        }
        root.setBounds(x, y, width, height);
    }
}
