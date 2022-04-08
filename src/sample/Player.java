package sample;

import javafx.scene.image.ImageView;
import java.util.ArrayList;

public class Player {

    private String name;
    private int handValue = 0;
    private ArrayList<ImageView> hand = new ArrayList<>();
    private ArrayList<String> handData = new ArrayList<>();

    public Player(String name) {
        this.name = name;
    }

     // Checks the hand and adds the values appropriately
    public void checkHandData() {
        handValue = 0; // Set it to 0 so it does the math everytime and Aces can be taken into account

        for (String d : handData) {
            if (d.equalsIgnoreCase("A")) {
                handValue += 11;
            } else if (d.equalsIgnoreCase("K") || d.equalsIgnoreCase("Q") || d.equalsIgnoreCase("J")) {
                handValue += 10;
            } else {
                handValue += Integer.parseInt(d);
            }
        }

        if (handValue > 21) {
            checkAces();
        }
    }

    // Checks for aces in deck and makes changes accordingly
    public void checkAces() {
        if (handData.contains("A")) {
            int aceCount = 0;

            for(String ace : handData) {
                if (ace.equalsIgnoreCase("A")) {
                    aceCount++;
                }
            }

            handValue -= aceCount * 10;
        }
    }

    public ArrayList<ImageView> getHand() {
        return hand;
    }

    public void addToHand(ImageView image) { // Adding an image from another class, specifically the deck
        this.hand.add(image);
    }

    public void clearHand() { // Clear all of the player hand data. From value, card images, and card letter/data
        handData.clear();
        hand.clear();
        handValue = 0;
    }

    public String getName() {
        return name;
    }

    public int getHandValue() {
        return handValue;
    }

    public void addHandData(String handData) {
        this.handData.add(handData);
        checkHandData();
    }
}
