package mvc.connect;

import mvc.Box;
import mvc.Ship;

import java.io.Serializable;
import java.util.List;

public class Message implements Serializable {
    private int x;
    private int y;
    private MessageType messageType;
    private Box[][]  gameField;
    private List<Ship> listOfAllShips;

    public Message(MessageType messageType, Box[][] gameField, List<Ship> allShipsOfEnemy) {
        this.messageType = messageType;
        this.gameField = gameField;
        this.listOfAllShips = allShipsOfEnemy;
    }

    public Message(MessageType messageType) {
        this.messageType = messageType;
    }

    public Message(MessageType messageType,int x, int y) {
        this.x = x;
        this.y = y;
        this.messageType = messageType;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public MessageType getMessageType() {
        return messageType;
    }

    public Box[][] getGameField() {
        return gameField;
    }

    public List<Ship> getListOfAllShips() {
        return listOfAllShips;
    }
}