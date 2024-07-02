package mvc;

import mvc.panels.ChoosePanel;
import mvc.panels.EnemyField;
import mvc.panels.MyField;
import mvc.panels.PanelButtons;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseListener;
import java.io.IOException;


public class View extends JFrame {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Controller controller;
    private Model model;
    private MyField myField; //panel of our playing field
    private EnemyField enemyField; //opponent's playing field panel
    private ChoosePanel choosePanel; //panel for selecting settings when adding a ship
    private PanelButtons panelButtons; //button bar

    public View() {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }
        setTitle("Battle War");
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);
        setIconImage(Picture.getImage("icon"));
    }

    public void setController(Controller controller) {
        this.controller = controller;
    }

    public void setModel(Model model) {
        this.model = model;
    }

    //GUI initialization
    public void init() {
        if (enemyField != null) {
            remove(enemyField);
            remove(myField);
            remove(panelButtons);
        }
        controller.loadEmptyMyField();
        add(choosePanel = new ChoosePanel(this), BorderLayout.EAST);
        add(myField = new MyField(this), BorderLayout.WEST);
        add(panelButtons = new PanelButtons(this), BorderLayout.SOUTH);
        myField.setChoosePanel(choosePanel);
        pack();
        revalidate();
        setVisible(true);
    }

    //method for calling an information dialog box with the given text
    public static void callInformationWindow(String message) {
        JOptionPane.showMessageDialog(
                null, message,
                "Attention!", JOptionPane.ERROR_MESSAGE
        );
    }

    //method to load our empty game board
    public void loadEmptyMyField() {
        controller.loadEmptyMyField();
        myField.repaint();            //redrawing our playing field
        //setting the name of the radioButtons on the panel for selecting the settings for adding a ship
        choosePanel.setNameOneDeck(4);
        choosePanel.setNameTwoDeck(3);
        choosePanel.setNameThreeDeck(2);
        choosePanel.setNameFourDeck(1);
    }

    //adding a ship
    public void addShip(Ship ship) {
        controller.addShip(ship);
    }

    //removal of the ship from our field by coordinates
    public Ship removeShip(int x, int y) {
        return controller.removeShip(x, y);
    }

    //a method that changes the name of the radioButtons when deleting / adding ships by the number of decks parameter
    public void changeCountShipOnChoosePanel(int countDeck) {
        switch (countDeck) {
            case 1: {
                //parameter - the number of ships left to add (the maximum number of ships of this type -
                //number of ships already added to the corresponding list in model
                choosePanel.setNameOneDeck(4 - model.getShipsOneDeck().size());
                break;
            }
            case 2: {
                choosePanel.setNameTwoDeck(3 - model.getShipsTwoDeck().size());
                break;
            }
            case 3: {
                choosePanel.setNameThreeDeck(2 - model.getShipsThreeDeck().size());
                break;
            }
            case 4: {
                choosePanel.setNameFourDeck(1 - model.getShipsFourDeck().size());
                break;
            }
        }
        choosePanel.revalidate();
    }

    //the method redraws our playing field
    public void repaintMyField(Graphics g) {
        Box[][] matrix = model.getMyField(); //we get the matrix of our field
        for (int i = 0; i < matrix.length; i++) {
            for (int j = 0; j < matrix[i].length; j++) {
                Box box = matrix[i][j]; //assign the value of the matrix element to the box
                if (box == null) continue;
                //uploading the image to the panel of our playing field
                g.drawImage(Picture.getImage(box.getPicture().name()), box.getX(), box.getY(), myField);
            }
        }
    }

    //the method redraws the opponent's playing field
    public void repaintEnemyField(Graphics g) {
        Box[][] matrix = model.getEnemyField(); //get the opponent's field matrix
        for (int i = 0; i < matrix.length; i++) {
            for (int j = 0; j < matrix[i].length; j++) {
                Box box = matrix[i][j];
                if (box == null) continue;
                //if the value of the picture = an empty cell or a cell with a ship then...
                if ((box.getPicture() == Picture.empty || box.getPicture() == Picture.ship)) {
                    //if the box is open and the picture = an empty cell, then draw this cell with the picture "empty cell with a dot"
                    if (box.isOpen() && box.getPicture() == Picture.empty) {
                        g.drawImage(Picture.getImage(Picture.point.name()), box.getX(), box.getY(), enemyField);
                    }
                    //otherwise, if the box is open and the picture = a cell with a ship, then draw this cell with the picture "cell with a crossed out ship"
                    else if ((box.isOpen() && box.getPicture() == Picture.ship)) {
                        g.drawImage(Picture.getImage(Picture.destroy_ship.name()), box.getX(), box.getY(), enemyField);
                    }
                    //in other cases, we draw the cell with the picture "closed cell"
                    else g.drawImage(Picture.getImage(Picture.closed.name()), box.getX(), box.getY(), enemyField);
                }
                //otherwise, we draw with the picture that is stored in the matrix - for cells numbering columns and rows
                else g.drawImage(Picture.getImage(box.getPicture().name()), box.getX(), box.getY(), enemyField);
            }
        }
    }

    //a method that is triggered after pressing the Start Game button
    public void startGame() {
        if (controller.checkFullSetShips()) { // checking that the player has added a complete set of ships
            //call the window where you need to enter the number of the game room, and also click the "create room" or "connect to the room" buttons
            String[] options = {"Create room","Join room"};
            JPanel panel = new JPanel();
            JLabel label1 = new JLabel("Create a room by entering a 4 digit room number,");
            JLabel label2 = new JLabel("or connect to an already created one:");
            JTextField field = new JTextField(25);
            panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
            panel.add(label1);
            panel.add(label2);
            panel.add(field);

            int selectedOption = JOptionPane.showOptionDialog(null, panel, "Room creation:",
                    JOptionPane.OK_OPTION, JOptionPane.QUESTION_MESSAGE, null, options, options[0]);
            try {
                if (selectedOption == 0) { //if the "create room" button is pressed
                    int port = Integer.parseInt(field.getText().trim());
                    controller.createGameRoom(port); //game room is created (server starts with socket connections)
                    panelButtons.setTextInfo("WE ARE WAITING AN OPPONENT");
                    panelButtons.revalidate();
                    View.callInformationWindow("Waiting for an opponent: after an opponent connects to the room, a notification will appear. Then the game will begin. Your move is first.");
                    controller.connectToRoom(port); //client to server connection
                    View.callInformationWindow("The second player has joined! You can start the battle.");
                    refreshGuiAfterConnect(); //updating the client interface after connecting the second player
                    panelButtons.setTextInfo("NOW YOUR MOVE");
                    panelButtons.getExitButton().setEnabled(true); //exit button activation
                    enemyField.addListener(); //add a listener to the opponent's playfield panel object
                } else if (selectedOption == 1) { //if the button "connect to the room" is pressed
                    int port = Integer.parseInt(field.getText().trim());
                    controller.connectToRoom(port); //client to server connection
                    View.callInformationWindow("You have successfully connected to the room. Your opponent goes first.");
                    refreshGuiAfterConnect(); //updating the client interface after connection
                    panelButtons.setTextInfo("NOW OPPONENT'S TURN");
                    new ReceiveThread().start(); //starting a thread that is waiting for a message from the server
                }
            } catch (Exception e) {
                View.callInformationWindow("An error occurred while creating the room, or an incorrect room number, please try again.");
                e.printStackTrace();
            }
        } else View.callInformationWindow("You have not added all the ships on your field!");
    }

    //method to disconnect the client from the server
    public void disconnectGameRoom() {
        try {
            controller.disconnectGameRoom();
            View.callInformationWindow("You have left the room. The game is over. You have suffered a technical defeat.");
            enemyField.removeListener(); //remove the listener from the opponent's playing field panel
        } catch (Exception e) {
            View.callInformationWindow("An error occurred while disconnecting from the room.");
        }
    }

    //updates the client interface after both players connect
    public void refreshGuiAfterConnect() {
        MouseListener[] listeners = myField.getMouseListeners();
        for (MouseListener lis : listeners) {
            myField.removeMouseListener(lis); //removing the listener from the panel of our playing field
        }
        choosePanel.setVisible(false);
        remove(choosePanel);          //deleting the settings panel for adding a ship
        add(enemyField = new EnemyField(this), BorderLayout.EAST); //adding an opponent's playing field panel
        enemyField.repaint(); //rendering of the opponent's field
        pack();  //repack form
        panelButtons.getStartGameButton().setEnabled(false); //deactivation of the button "Start the game"
        revalidate();
    }

    //sending a message to the server with the coordinates of the shot cell
    public void sendShot(int x, int y) {
        try {
            boolean isSendShot = controller.sendMessage(x, y); //sending a message directly through the controller
            if (isSendShot) { //if the message is sent, then
                enemyField.repaint(); //redrawing of the opponent's field
                enemyField.removeListener(); //removal of a listener from the opponent's field panel
                panelButtons.setTextInfo("NOW OPPONENT'S TURN");
                panelButtons.getExitButton().setEnabled(false); //exit button deactivation
                new ReceiveThread().start(); //starting a thread that is waiting for a message from the server
            }
        } catch (Exception e) {
            View.callInformationWindow("An error occurred while sending the shot.");
            e.printStackTrace();
        }
    }

    //the class-thread that is waiting for a message from the server
    private class ReceiveThread extends Thread {
        @Override
        public void run() {
            try {
                boolean continueGame = controller.receiveMessage(); //the controller received the message
                myField.repaint();
                if (continueGame) { //if true is returned, then...
                    panelButtons.setTextInfo("NOW YOUR MOVE");
                    panelButtons.getExitButton().setEnabled(true); //exit button activation
                    enemyField.addListener();  //adding a listener to the opponent's field
                } else { //if it returns false then the game is over
                    panelButtons.setTextInfo("THE GAME IS OVER");
                    panelButtons.getExitButton().setEnabled(false);
                    enemyField.removeListener();
                    panelButtons.getRestartGameButton().setEnabled(true);
                }

            } catch (IOException | ClassNotFoundException e) {
                View.callInformationWindow("An error occurred while receiving a message from the server");
                e.printStackTrace();
            }
        }
    }
}