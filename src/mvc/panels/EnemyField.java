package mvc.panels;

import mvc.Picture;
import mvc.View;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;


public class EnemyField extends JPanel {
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private View view;

    public EnemyField(View view) {
        this.view = view;
        this.setPreferredSize(new Dimension(Picture.COLUMNS * Picture.IMAGE_SIZE, Picture.ROWS * Picture.IMAGE_SIZE));
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        view.repaintEnemyField(g);
    }

    //adds a listener to the opponent's field panel
    public void addListener() {
        addMouseListener(new ActionMouse());
    }

    //removes the listener from the opponent's field panel
    public void removeListener() {
        MouseListener[] listeners = getMouseListeners();
        for (MouseListener lis : listeners) {
            removeMouseListener(lis);
        }
    }

    //at an event (clicking the mouse button on the panel) receives the coordinates, rounds them, checks not outside the playing field
    //these coordinates and sends the coordinates to the opponent
    private class ActionMouse extends MouseAdapter {
        @Override
        public void mousePressed(MouseEvent e) {
            int x = (e.getX() / Picture.IMAGE_SIZE) * Picture.IMAGE_SIZE;
            int y = (e.getY() / Picture.IMAGE_SIZE) * Picture.IMAGE_SIZE;
            if (x >= Picture.IMAGE_SIZE && y >= Picture.IMAGE_SIZE) {
                view.sendShot(x, y);
            }
        }
    }
}