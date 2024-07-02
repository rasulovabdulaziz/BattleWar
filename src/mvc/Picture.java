package mvc;

import javax.swing.*;
import java.awt.*;

public enum Picture {

    closed, //value for closed box picture
    destroy_ship, //for the destroyed ship deck
    empty, //for an empty cell
    NUM1, NUM2, NUM3, NUM4, NUM5, NUM6, NUM7, NUM8, NUM9, NUM10, //for numbering the playing field
    point, //for empty shot box
    ship, //ship deck
    info, //for information on the panel for selecting ship placement settings
    SYM1, SYM2, SYM3, SYM4, SYM5, SYM6, SYM7, SYM8, SYM9, SYM10; //for lettering columns
    public static final int COLUMNS = 11; //number of columns
    public static final int ROWS = 11; //number of lines
    public static final int IMAGE_SIZE = 40; //image side size in pixels

    public static Image getImage(String nameImage) {
        String fileName = "res/img/" + nameImage.toLowerCase() + ".png"; //to load image from resources
        //String fileName = "C:\\BattleWar (3)\\BattleWar\\res\\img\\" + nameImage.toLowerCase() + ".png"; //Here should be address of file with images!If firts command will not work.
        ImageIcon icon = new ImageIcon(fileName);
        return icon.getImage();
    }
}