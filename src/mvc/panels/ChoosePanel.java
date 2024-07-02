package mvc.panels;

import mvc.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class ChoosePanel extends JPanel {
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private View view;
    private JPanel panelRadio;
    private JPanel panelPlacement;
    private JRadioButton oneDeck;
    private JRadioButton twoDeck;
    private JRadioButton threeDeck;
    private JRadioButton fourDeck;
    private JRadioButton vertical;
    private JRadioButton horizontal;
    private JButton clearField;
    private ButtonGroup groupDeck;
    private ButtonGroup groupPlacement;

    public ChoosePanel(View view) {
        this.view = view;
        setLayout(null);
        this.setPreferredSize(new Dimension(255, 400));
        panelRadio = new JPanel();
        panelRadio.setLayout(new BoxLayout(panelRadio, BoxLayout.Y_AXIS));
        panelRadio.setBounds(13, 190, 230, 130);
        panelPlacement = new JPanel();
        panelPlacement.setLayout(new BoxLayout(panelPlacement, BoxLayout.Y_AXIS));
        panelPlacement.setBounds(13, 330, 230, 80);
        clearField = new JButton("Remove all ships");
        clearField.setBounds(13, 410, 230, 30);
        clearField.addActionListener(new ActionClearField());
        panelRadio.setBorder(BorderFactory.createTitledBorder("Deck"));
        panelPlacement.setBorder(BorderFactory.createTitledBorder("Ship orientation"));
        oneDeck = new JRadioButton();
        setNameOneDeck(4);
        twoDeck = new JRadioButton();
        setNameTwoDeck(3);
        threeDeck = new JRadioButton();
        setNameThreeDeck(2);
        fourDeck = new JRadioButton();
        setNameFourDeck(1);
        vertical = new JRadioButton("Vertical");
        horizontal = new JRadioButton("Horizontal");
        groupDeck = new ButtonGroup();
        groupPlacement = new ButtonGroup();
        panelRadio.add(oneDeck);
        panelRadio.add(twoDeck);
        panelRadio.add(threeDeck);
        panelRadio.add(fourDeck);
        panelPlacement.add(vertical);
        panelPlacement.add(horizontal);
        add(panelRadio);
        add(panelPlacement);
        add(clearField);
        groupDeck.add(oneDeck);
        groupDeck.add(twoDeck);
        groupDeck.add(threeDeck);
        groupDeck.add(fourDeck);
        groupPlacement.add(vertical);
        groupPlacement.add(horizontal);
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.drawImage(Picture.getImage(Picture.info.name()), 2, 0, this);
    }

    //установка имени радиобаттанов палубности
    public void setNameOneDeck(int count) {
        String text = "Single-deck, left - " + count;
        oneDeck.setText(text);
    }

    public void setNameTwoDeck(int count) {
        String text = "Double-deck, left - " + count;
        twoDeck.setText(text);
    }

    public void setNameThreeDeck(int count) {
        String text = "Three-deck, left -" + count;
        threeDeck.setText(text);
    }

    public void setNameFourDeck(int count) {
        String text = "Four-deck, left -" + count;
        fourDeck.setText(text);
    }

    //returns the number of decks depending on which radioButton is selected
    public int getCountDeck() {
        if (oneDeck.isSelected()) return 1;
        else if (twoDeck.isSelected()) return 2;
        else if (threeDeck.isSelected()) return 3;
        else if (fourDeck.isSelected()) return 4;
        else return 0;
    }

    //returns a number indicating which ship orientation is selected
    public int getPlacement() {
        if (vertical.isSelected()) return 1;
        else if (horizontal.isSelected()) return 2;
        else return 0;
    }

    //listener class that loads an empty field when the "Remove all ships" button is clicked
    private class ActionClearField implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            view.loadEmptyMyField();
        }
    }
}