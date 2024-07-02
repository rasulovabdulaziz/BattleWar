package mvc;

import mvc.connect.Connection;
import mvc.connect.Message;
import mvc.connect.MessageType;
import mvc.connect.Server;

import java.io.IOException;
import java.net.Socket;
import java.util.List;

public class Controller {
    private Model model;
    private Connection connection;

    public Controller(View view, Model model) {
        this.model = model;
    }

    //loads our empty playing field
    public void loadEmptyMyField() {
        //clearing lists of all types of ships
        model.getShipsOneDeck().clear();
        model.getShipsTwoDeck().clear();
        model.getShipsThreeDeck().clear();
        model.getShipsFourDeck().clear();
        model.setMyField(new Box[Picture.COLUMNS][Picture.ROWS]);
        //assigning values to the elements of the matrix of our playing field
        for (int i = 0; i < Picture.ROWS; i++) {
            for (int j = 0; j < Picture.COLUMNS; j++) {
                if (i == 0 && j == 0) continue;
                else if (i == 0 && j != 0) { //if this is the first column, then assign the value of pictures with letters
                    model.addBoxInField(model.getMyField(), new Box(Picture.valueOf("SYM" + j), Picture.IMAGE_SIZE * i, Picture.IMAGE_SIZE * j));
                }
                else if (i != 0 && j == 0) { //if this is the first line, assign the value of pictures with numbers
                    model.addBoxInField(model.getMyField(), new Box(Picture.valueOf("NUM" + i), Picture.IMAGE_SIZE * i, Picture.IMAGE_SIZE * j));
                }
                else { //in other cases, knowing the picture with an empty cell
                    model.addBoxInField(model.getMyField(), new Box(Picture.empty, Picture.IMAGE_SIZE * i, Picture.IMAGE_SIZE * j));
                }
            }
        }
    }

    //adds a ship before checking whether the new ship intersects with the already added ship
    public void addShip(Ship ship) {
        List<Box> boxesOfShip = ship.getBoxesOfShip();
        for (Box boxShip : boxesOfShip) {
            if (checkAround(boxShip, boxesOfShip)) {
                boxesOfShip.clear();
                return;
            }
        }
        if (boxesOfShip.size() != 0) model.addShip(ship);
    }

    //deletes the ship at the given coordinates
    public Ship removeShip(int x, int y) {
        List<Ship> allShips = model.getAllShips(); //get a list of all added ships
        for (Ship ship : allShips) {
            for (Box box : ship.getBoxesOfShip()) {
                if (x == box.getX() && y == box.getY()) { //we sort out the ships, then their boxes,
                    // if the box coordinates match the given ones, delete the ship
                    model.removeShip(ship);
                    return ship;
                }
            }
        }
        return null;
    }

    //method of checking for crossings with other ships
    private boolean checkAround(Box box, List<Box> boxesOfShip) {
        int myX = box.getX();
        int myY = box.getY();
        for (int i = myX - Picture.IMAGE_SIZE; i <= myX + Picture.IMAGE_SIZE; i += Picture.IMAGE_SIZE) {
            for (int j = myY - Picture.IMAGE_SIZE; j <= myY + Picture.IMAGE_SIZE; j += Picture.IMAGE_SIZE) {
                Box boxFromMatrix = model.getBox(model.getMyField(), i, j);
                if (boxFromMatrix != null && boxFromMatrix.getPicture() == Picture.ship && !boxesOfShip.contains(boxFromMatrix)) {
                    View.callInformationWindow("You can't add a ship here - crossing with another one");
                    boxesOfShip.clear();
                    return true;
                }
            }
        }
        return false;
    }

    //a method that opens empty cells around a wrecked ship on the enemy field
    public void openBoxesAround(Box boxShot) {
        Ship ship = model.getShipOfEnemy(boxShot); //on boxing in which they shot, we get a ship
        if (ship != null) {
            //if the number of decks == the number of padded (open) boxes of the ship, then open all the empty cells around
            if (ship.getCountDeck() == getCountOpenBoxOfShip(ship)) model.openAllBoxesAroundShip(ship);
                //otherwise, if only one deck is hit (box is open), we do nothing
            else if (getCountOpenBoxOfShip(ship) == 1) return;
                //in other cases, open empty cells around the padded decks
            else {
                for (Box box : ship.getBoxesOfShip()) {
                    if (box.isOpen())
                        model.openBoxAroundBoxOfShipEnemy(box.getX(), box.getY(), ship.isHorizontalPlacement());
                }
            }
        }
    }

    //the method returns the number of open boxes (padded decks) of the ship
    public int getCountOpenBoxOfShip(Ship ship) {
        int count = 0;
        for (Box box : ship.getBoxesOfShip()) {
            if (box.isOpen()) count++;
        }
        return count;
    }

    //game over check
    private boolean checkEndGame() {
        List<Box> allBoxesOfShip = model.getAllBoxesOfShips(); //get a list of all our ships
        for (Box box : allBoxesOfShip) {
            //check if at least one ship has a SHIP picture value then the game is not over
            if (box.getPicture() == Picture.ship) return false;
        }
        return true;
    }

    //checking for a complete set of added ships before the start of the game
    public boolean checkFullSetShips() {
        //just check the number of ship in the corresponding lists for each type of ship
        if (model.getShipsOneDeck().size() == 4 &&
                model.getShipsTwoDeck().size() == 3 &&
                model.getShipsThreeDeck().size() == 2 &&
                model.getShipsFourDeck().size() == 1) return true;
        else return false;
    }

    //FURTHER ARE METHODS FOR CONNECTING TO THE SERVER AND EXCHANGE SHOT BETWEEN PLAYERS

    //creates a playroom
    public void createGameRoom(int port) throws IOException {
        Server server = new Server(port); //create an object of the server class and start the thread of execution
        server.start();
    }


    //client to server connection method
    public void connectToRoom(int port) throws IOException, ClassNotFoundException {
        connection = new Connection(new Socket("localhost", port));
        Message message = connection.receive(); //receive a message from the server
        //if the message type is ACCEPTED, then we send our field with ships and a list of all ships to the server
        if (message.getMessageType() == MessageType.ACCEPTED) {
            connection.send(new Message(MessageType.FIELD, model.getMyField(), model.getAllShips()));
            Message messageField = connection.receive(); //waiting for a response from the server with the field and the list of enemy ships
            if (messageField.getMessageType() == MessageType.FIELD) {
                model.setEnemyField(messageField.getGameField());  //save the model in the field and the list of enemy ships
                model.setAllShipsOfEnemy(messageField.getListOfAllShips());
            }
        }
    }

    //method of disconnecting from the server (leaving the game room)
    public void disconnectGameRoom() throws IOException {
        connection.send(new Message(MessageType.DISCONNECT));
    }

    //sending a message to the server with the coordinates of the shot
    public boolean sendMessage(int x, int y) throws IOException {
        Box box = model.getBox(model.getEnemyField(), x, y);
        if (!box.isOpen()) {
            box.setOpen(true); //open the shot box
            openBoxesAround(box); //we open neighboring empty cells (boxes)
            connection.send(new Message(MessageType.SHOT, x, y)); //send the coordinates of the shot to the server
            return true;
        } else return false;
    }

    //method that receives messages from the server
    public boolean receiveMessage() throws IOException, ClassNotFoundException {
        Message message = connection.receive(); //receive a message
        if (message.getMessageType() == MessageType.SHOT) { //if it's a shot...
            int x = message.getX();
            int y = message.getY();
            Box box = model.getBox(model.getMyField(), x, y); //we get a box from our field by coordinates
            //if the box with the picture is "empty cell", then set the value to "empty cell with a dot"
            if (box.getPicture() == Picture.empty) {
                box.setPicture(Picture.point);
                //if the box with the picture is "ship" then set the value to "destroyed ship"
            } else if (box.getPicture() == Picture.ship) {
                box.setPicture(Picture.destroy_ship);
            }
            model.addBoxInField(model.getMyField(), box); //set the modified box to our field matrix
            if (checkEndGame()) { //check for the end of the game, if yes, send the appropriate message
                connection.send(new Message(MessageType.DEFEAT));
                View.callInformationWindow("You lost! All your ships have been destroyed.");
                return false;
            }
            return true;
            //if the message type DISCONNECT is sent, that we also disconnect
        } else if (message.getMessageType() == MessageType.DISCONNECT) {
            connection.send(new Message(MessageType.MY_DISCONNECT));
            View.callInformationWindow("Your opponent has left the game. You have won a technical victory!");
            return false;
            //if the message type is DEFEAT we are sending that we are also disconnected
        } else if (message.getMessageType() == MessageType.DEFEAT) {
            connection.send(new Message(MessageType.MY_DISCONNECT));
            View.callInformationWindow("All enemy ships have been destroyed. You have won!");
            return false;
        } else return false;
    }
}