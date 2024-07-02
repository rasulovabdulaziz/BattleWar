package mvc;

import java.util.ArrayList;
import java.util.List;

public class Model {
    private Box[][] myField = new Box[Picture.COLUMNS][Picture.ROWS]; //matrix with boxes of our playing field
    private Box[][] enemyField = new Box[Picture.COLUMNS][Picture.ROWS];  //matrix with boxes of the opponent's playing field
    private List<Ship> shipsOneDeck = new ArrayList<>(); //list of all our single deck ships
    private List<Ship> shipsTwoDeck = new ArrayList<>(); //list of all our double deck ships
    private List<Ship> shipsThreeDeck = new ArrayList<>(); //list of all our three-deck ships
    private List<Ship> shipsFourDeck = new ArrayList<>(); //list of all our four-deck ships
    private List<Ship> allShipsOfEnemy = new ArrayList<>(); //list of all oponent ships


    public void setAllShipsOfEnemy(List<Ship> allShipsOfEnemy) {
        this.allShipsOfEnemy = allShipsOfEnemy;
    }

    //a method that returns a list of all our oracles
    public List<Ship> getAllShips() {
        List<Ship> allBoxesOfShips = new ArrayList<>();
        allBoxesOfShips.addAll(shipsFourDeck);
        allBoxesOfShips.addAll(shipsThreeDeck);
        allBoxesOfShips.addAll(shipsTwoDeck);
        allBoxesOfShips.addAll(shipsOneDeck);
        return allBoxesOfShips;
    }

    //a method that returns ALL boxes from ALL of our ships
    public List<Box> getAllBoxesOfShips() {
        List<Box> allBoxes = new ArrayList<>();
        List<Ship> allShips = getAllShips();
        for (Ship ship : allShips) {
            allBoxes.addAll(ship.getBoxesOfShip());
        }
        return allBoxes;
    }

    public List<Ship> getShipsOneDeck() {
        return shipsOneDeck;
    }

    public List<Ship> getShipsTwoDeck() {
        return shipsTwoDeck;
    }

    public List<Ship> getShipsThreeDeck() {
        return shipsThreeDeck;
    }

    public List<Ship> getShipsFourDeck() {
        return shipsFourDeck;
    }

    public Box[][] getMyField() {
        return myField;
    }

    public void setMyField(Box[][] myField) {
        this.myField = myField;
    }

    public Box[][] getEnemyField() {
        return enemyField;
    }

    public void setEnemyField(Box[][] enemyField) {
        this.enemyField = enemyField;
    }

    //a method that sets the value of the specified box to the specified matrix (playfield)
    public void addBoxInField(Box[][] fieldBox, Box box) {
        //by the box coordinates we calculate the indices of the corresponding place in the matrix
        int i = box.getX() / Picture.IMAGE_SIZE;
        int j = box.getY() / Picture.IMAGE_SIZE;
        fieldBox[i][j] = box;
    }

    //a method that returns a box from the specified matrix (playfield) by the coordinates of the playfield rendering panel
    public Box getBox(Box[][] field, int x, int y) {
        int i = x / Picture.IMAGE_SIZE;
        int j = y / Picture.IMAGE_SIZE;
        int lenght = field.length - 1;
        //if the coordinates point to an element whose index is greater than the dimension of the matrix, then return null
        if (!(i > lenght || j > lenght)) {
            return field[i][j];
        }
        return null;
    }

    //a method that sets the value of isOpen to true (opens boxes after hitting the ship) of boxes adjacent to the box of the ship specified by the input coordinates
    public void openBoxAroundBoxOfShipEnemy(int x, int y, boolean isHorizontalPlacement) {
        //for a horizontally oriented ship
        if (isHorizontalPlacement) {
            Box boxUp = getBox(enemyField, x, y - Picture.IMAGE_SIZE);
            if (boxUp != null) boxUp.setOpen(true);
            Box boxDown = getBox(enemyField, x, y + Picture.IMAGE_SIZE);
            if (boxDown != null) boxDown.setOpen(true);
        }
        //for a vertically oriented ship
        else {
            Box boxLeft = getBox(enemyField, x - Picture.IMAGE_SIZE, y);
            if (boxLeft != null) boxLeft.setOpen(true);
            Box boxRight = getBox(enemyField, x + Picture.IMAGE_SIZE, y);
            if (boxRight != null) boxRight.setOpen(true);
        }
    }

    //opens all empty boxes around the perimeter of the ship, used when all decks of the ship are damaged
    public void openAllBoxesAroundShip(Ship ship) {
        //we open all the boxes around the first and last boxes of the ship in cycles, because there can be no more than 4 decks,
        // then just open everything around the first deck and the last
        Box startBox = ship.getBoxesOfShip().get(0);
        Box endBox = ship.getBoxesOfShip().get(ship.getCountDeck() - 1);
        for (int i = startBox.getX() - Picture.IMAGE_SIZE; i <= startBox.getX() + Picture.IMAGE_SIZE; i += Picture.IMAGE_SIZE) {
            for (int j = startBox.getY() - Picture.IMAGE_SIZE; j <= startBox.getY() + Picture.IMAGE_SIZE; j += Picture.IMAGE_SIZE) {
                Box box = getBox(enemyField, i, j);
                if (box != null) box.setOpen(true);
            }
        }
        for (int i = endBox.getX() - Picture.IMAGE_SIZE; i <= endBox.getX() + Picture.IMAGE_SIZE; i += Picture.IMAGE_SIZE) {
            for (int j = endBox.getY() - Picture.IMAGE_SIZE; j <= endBox.getY() + Picture.IMAGE_SIZE; j += Picture.IMAGE_SIZE) {
                Box box = getBox(enemyField, i, j);
                if (box != null) box.setOpen(true);
            }
        }
    }

    //adds the given ship to the corresponding list of ships depending on the number of decks
    public void addShip(Ship ship) {
        int countDeck = ship.getCountDeck();
        switch (countDeck) {
            case 1: {
                //check - if the list already contains the maximum number of ships of this type (number of decks)
                //then the corresponding information window is called, otherwise we add the ship to the required list
                if (shipsOneDeck.size() < 4) {
                    shipsOneDeck.add(ship);
                    for (Box box : ship.getBoxesOfShip()) {
                        addBoxInField(myField, box);
                    }
                } else View.callInformationWindow("Enumeration of single-deck. The maximum possible is 4.");
                break;
            }
            case 2: {
                if (shipsTwoDeck.size() < 3) {
                    shipsTwoDeck.add(ship);
                    for (Box box : ship.getBoxesOfShip()) {
                        addBoxInField(myField, box);
                    }
                } else View.callInformationWindow("Enumeration of double-deck. The maximum possible is 3.");
                break;
            }
            case 3: {
                if (shipsThreeDeck.size() < 2) {
                    shipsThreeDeck.add(ship);
                    for (Box box : ship.getBoxesOfShip()) {
                        addBoxInField(myField, box);
                    }
                } else View.callInformationWindow("Enumeration of three-deck. The maximum possible is 2.");
                break;
            }
            case 4: {
                if (shipsFourDeck.size() < 1) {
                    shipsFourDeck.add(ship);
                    for (Box box : ship.getBoxesOfShip()) {
                        addBoxInField(myField, box);
                    }
                } else View.callInformationWindow("A four-deck has already been added. The maximum possible is 1.");
                break;
            }
        }
    }


    //returns the enemy ship in the box that was fired at, if the coordinates of the boxShot are equal to the coordinates
    //boxing one of the enemy ship, otherwise return null
    public Ship getShipOfEnemy(Box boxShot) {
        for (Ship ship : allShipsOfEnemy) {
            for (Box box : ship.getBoxesOfShip()) {
                if (boxShot.getX() == box.getX() && boxShot.getY() == box.getY()) {
                    return ship;
                }
            }
        }
        return null;
    }

    //removes the ship from the corresponding list - used in the deployment process
    // and removing ships on your playing field
    public void removeShip(Ship ship) {
    //if the ship is contained in one of the lists, then iterate over all the boxes of the list,
    // change the value of their picture to EMPTY and add this box to the matrix of our playing field
        if (shipsOneDeck.contains(ship)) {
            for (Box box : ship.getBoxesOfShip()) {
                box.setPicture(Picture.empty);
                addBoxInField(myField, box);
                shipsOneDeck.remove(ship);
            }
        } else if (shipsTwoDeck.contains(ship)) {
            for (Box box : ship.getBoxesOfShip()) {
                box.setPicture(Picture.empty);
                addBoxInField(myField, box);
                shipsTwoDeck.remove(ship);
            }
        } else if (shipsThreeDeck.contains(ship)) {
            for (Box box : ship.getBoxesOfShip()) {
                box.setPicture(Picture.empty);
                addBoxInField(myField, box);
                shipsThreeDeck.remove(ship);
            }
        } else if (shipsFourDeck.contains(ship)) {
            for (Box box : ship.getBoxesOfShip()) {
                box.setPicture(Picture.empty);
                addBoxInField(myField, box);
                shipsFourDeck.remove(ship);
            }
        }
    }
}