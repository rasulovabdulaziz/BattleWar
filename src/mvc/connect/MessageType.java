package mvc.connect;

public enum MessageType {
    FIELD, //when sending receiving a playing field matrix
    SHOT, //when sending a receiving shot
    DEFEAT, //when sending receiving the defeat of one of the players
    ACCEPTED, //when the socket is accepted by the server
    DISCONNECT, //on client shutdown
    MY_DISCONNECT;
}