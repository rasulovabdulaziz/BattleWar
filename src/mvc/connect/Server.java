package mvc.connect;

import mvc.Box;
import mvc.Ship;
import mvc.View;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class Server extends Thread {
    private ServerSocket serverSocket;
    private Box[][] fieldPlayer1; //player 1 playing field matrix
    private Box[][] fieldPlayer2; //player 2 playing field matrix
    private List<Ship> allShipsPlayer1; //list of player 1 ships
    private List<Ship> allShipsPlayer2; //list of player 2 ships
    private volatile boolean allPlayersConnected = false; //flag whether all players are connected
    private List<Connection> listConnection = new ArrayList<>(); //list of connections of all players

    public Server(int port) throws IOException {
        serverSocket = new ServerSocket(port);
    }

    @Override
    public void run() {
        startServer();
    }

    //starts the server
    private void startServer() {
        try {
            while (!allPlayersConnected) { //not all players connected?
                Socket socket = serverSocket.accept(); //accept client connection
                if (listConnection.size() == 0) { //if there are no connections in the list yet, then...
                    Connection connection = new Connection(socket);
                    listConnection.add(connection); //add connection to list
                    connection.send(new Message(MessageType.ACCEPTED)); //send to customer about acceptance
                    Message message = connection.receive();  //we are waiting for a matrix of its field and a list of ships from the client
                    if (message.getMessageType() == MessageType.FIELD) {
                        //set the field and ships to the appropriate fields
                        fieldPlayer1 = message.getGameField();
                        allShipsPlayer1 = message.getListOfAllShips();
                    }
                    //we start the thread of the main loop of communication between the client and the server
                    new ThreadConnection(connection).start();
                }
                //similarly for player 2, only after he sends the field and ships, we send the opponent's fields and ships to the clients
                else if (listConnection.size() == 1) {
                    Connection connection = new Connection(socket);
                    listConnection.add(connection);
                    connection.send(new Message(MessageType.ACCEPTED));
                    Message message = connection.receive();
                    if (message.getMessageType() == MessageType.FIELD) {
                        fieldPlayer2 = message.getGameField();
                        allShipsPlayer2 = message.getListOfAllShips();
                        connection.send(new Message(MessageType.FIELD, fieldPlayer1, allShipsPlayer1));
                        listConnection.get(0).send(new Message(MessageType.FIELD, fieldPlayer2, allShipsPlayer2));
                    }
                    new ThreadConnection(connection).start();
                    allPlayersConnected = true;
                }
            }
            serverSocket.close();
        } catch (Exception e) {
            View.callInformationWindow("An error occurred while starting the game room server.");
        }
    }

    //class-thread of execution for the main loop of communication between the client and the server
    private class ThreadConnection extends Thread {
        private Connection connection;
        private volatile boolean stopCicle = false; //loop interrupt flag


        public ThreadConnection(Connection connection) {
            this.connection = connection;
        }

        private void mainCicle(Connection connection) {
            try {
                while (!stopCicle) {
                    Message message = connection.receive(); //receive a message from the client
                    //if disconnect, then redirect the message to the enemy client and stop the loop
                    if (message.getMessageType() == MessageType.DISCONNECT || message.getMessageType() == MessageType.DEFEAT) {
                        sendMessageEnemy(message);
                        stopCicle = true;
                        //if the message type is MY_DISCONNECT then just stop the loop
                    } else if (message.getMessageType() == MessageType.MY_DISCONNECT) {
                        stopCicle = true;
                        //in any other case, we redirect the message to the enemy
                    } else sendMessageEnemy(message);
                }
                connection.close();
            } catch (Exception e) {
                View.callInformationWindow("Error when exchanging shots. Communication lost");
            }
        }

        //sends a message to the enemy
        private void sendMessageEnemy(Message message) throws IOException {
            for (Connection con : listConnection) {
                if (!connection.equals(con)) {
                    con.send(message);
                }
            }
        }

        @Override
        public void run() {
            mainCicle(connection);
        }
    }
}