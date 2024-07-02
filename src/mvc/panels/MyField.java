package mvc.panels;

import mvc.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class MyField extends JPanel {
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private View view;
    private ChoosePanel choosePanel;

    public void setChoosePanel(ChoosePanel choosePanel) {
        this.choosePanel = choosePanel;
    }

    public MyField(View view) {
        this.view = view;
        this.setPreferredSize(new Dimension(Picture.COLUMNS * Picture.IMAGE_SIZE, Picture.ROWS * Picture.IMAGE_SIZE));
        this.addMouseListener(new ActionMouse());
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        view.repaintMyField(g);
    }

    //class listener on mouse button click on the panel
    private class ActionMouse extends MouseAdapter {
        @Override
        public void mousePressed(MouseEvent e) {
            //get coordinates and round
            int x = (e.getX() / Picture.IMAGE_SIZE) * Picture.IMAGE_SIZE;
            int y = (e.getY() / Picture.IMAGE_SIZE) * Picture.IMAGE_SIZE;
            //we get the number of decks and the orientation of the ship
            int countDeck = choosePanel.getCountDeck();
            int placement = choosePanel.getPlacement();
            Ship ship;
            Ship removedShip;
            //if LMB was pressed then add the ship
            if (e.getButton() == MouseEvent.BUTTON1 && (x >= Picture.IMAGE_SIZE && y >= Picture.IMAGE_SIZE)) {
                if (countDeck > 0 && countDeck <= 4) {
                    switch (placement) {
                        case 1: {
                            ship = new Ship(countDeck, false);
                            ship.createVerticalShip(x, y);
                            view.addShip(ship);
                            break;
                        }
                        case 2: {
                            ship = new Ship(countDeck, true);
                            ship.createHorizontalShip(x, y);
                            view.addShip(ship);
                            break;
                        }
                        default:
                            View.callInformationWindow("No nesting orientation selected.");
                    }
                } else {
                    View.callInformationWindow("The number of decks has not been selected.");
                    return;
                }
                //if RMB then delete the ship
            } else if (e.getButton() == MouseEvent.BUTTON3 && (x >= Picture.IMAGE_SIZE && y >= Picture.IMAGE_SIZE) &&
                    (removedShip = view.removeShip(x, y)) != null) {
                //change the number of remaining ships in the name of the radioButton due to the removal of the ship
                view.changeCountShipOnChoosePanel(removedShip.getCountDeck());
            }
            repaint(); //redraw the panel
            //change the number of remaining ships in the name of the radio button in connection with the addition of the ship
            view.changeCountShipOnChoosePanel(countDeck);
        }
    }
}