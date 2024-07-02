package mvc;

import java.io.Serializable;

public class Box implements Serializable {
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	//coordinates of the box  with x and y on the field
    private int x;
    private int y;
    private Picture picture;  //meaning of a fair picture for this box
    private boolean isOpen = false;  //flag whether this box is open (whether the opponent has shot in this box)

    public boolean isOpen() {
        return isOpen;
    }

    public void setOpen(boolean open) {
        isOpen = open;
    }

    public Picture getPicture() {
        return picture;
    }

    public void setPicture(Picture picture) {
        this.picture = picture;
    }

    public Box(Picture picture, int x, int y) {
        this.picture = picture;
        this.x = x;
        this.y = y;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }
}