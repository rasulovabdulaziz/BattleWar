package mvc;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Ship implements Serializable {
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private final int countDeck;  //number of decks
    private List<Box> boxesOfShip; //list of all bosses of this ship
    private final boolean isHorizontalPlacement; //ship orientation

    public List<Box> getBoxesOfShip() {
        return boxesOfShip;
    }

    public boolean isHorizontalPlacement() {
        return isHorizontalPlacement;
    }

    public int getCountDeck() {
        return countDeck;
    }

    public Ship(int countDeck, boolean isHorizontalPlacement) {
        this.countDeck = countDeck;
        this.isHorizontalPlacement = isHorizontalPlacement;
        boxesOfShip = new ArrayList<>(countDeck);
    }

    //method for creating a ship of horizontal orientation by given coordinates x and y
    public void createHorizontalShip(int x, int y) {
        //in order for the ship not to be drawn outside the playing field, we calculate the maximum possible X coordinate for drawing, taking into account the number of decks
        int pointLimitValueForPaint = (Picture.COLUMNS - countDeck) * Picture.IMAGE_SIZE;
        for (int i = 0; i < countDeck; i++) {
            Box newBox;
            //if X is greater than the maximum allowable drawing point, then the X coordinate for boxing = pointLimitValueForPaint + i * Picture.IMAGE_SIZE
            if (x > pointLimitValueForPaint) {
                newBox = new Box(Picture.ship, pointLimitValueForPaint + i * Picture.IMAGE_SIZE, y);
                boxesOfShip.add(newBox);
            } else {
                newBox = new Box(Picture.ship, (x + i * Picture.IMAGE_SIZE), y);
                boxesOfShip.add(newBox);
            }
        }
    }

    //a similar method for creating a ship with a horizontal orientation
    public void createVerticalShip(int x, int y) {
        int pointStartPaint = (Picture.ROWS - countDeck) * Picture.IMAGE_SIZE;
        for (int i = 0; i < countDeck; i++) {
            Box newBox;
            if (pointStartPaint < y) {
                newBox = new Box(Picture.ship, x, pointStartPaint + i * Picture.IMAGE_SIZE);
                boxesOfShip.add(newBox);
            } else {
                newBox = new Box(Picture.ship, x, (y + i * Picture.IMAGE_SIZE));
                boxesOfShip.add(newBox);
            }
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || o.getClass() != this.getClass()) return false;
        Ship ship = (Ship) o;
        return countDeck == ship.getCountDeck() && (boxesOfShip != null && ship.getBoxesOfShip() != null &&
                boxesOfShip.hashCode() == ship.getBoxesOfShip().hashCode());
    }
}