package sample;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

public class Cards {

    protected final ArrayList<ImageView> cardImages;
    private boolean[] cardsAvailable = new boolean[54];
    private final Random random = new Random();
    private final String[] cardsInformation = { "A", "2", "3", "4", "5", "6", "7", "8", "9", "10", "J", "Q", "K" };


    public Cards() { // Getting the card images when instantiated
        cardImages = getCardImages();

        shuffleCards();
    }

    public ArrayList<ImageView> getCardImages() { // Getting the images from the CardImages folder
        ArrayList<ImageView> cards = new ArrayList<>();
        String folder = "sample\\CardImages\\";
        String extension = ".png";

        for (int i = 1; i < 55; i++) {
            cards.add(new ImageView(new Image(folder + i + extension)));
            cardsAvailable[i - 1] = false; // Setting all cards to false (not being used), just in case
        }

        return cards;
    }

    public void dealCards(Player player) { // This is for dealing the card when the game first starts
        int iterations;

        if (player.getName().equalsIgnoreCase("Dealer")) { iterations = 1; } else { iterations = 2; } // If dealer, deal one card and then give them a card facing the back

        while (player.getHand().size() < iterations) {
            giveCard(player); // Call this method to give the players their needed cards
        }

        if (iterations == 1) { player.addToHand(cardImages.get(53)); } // Since it is the dealer, give him a back facing card

        System.out.println("Total value of " + player.getName() + ": " + player.getHandValue());
    }

    public void giveCard(Player player) { // Give one card to the player, used for hits or dealing initially but with a loop
        int randomCard; randomCard = random.nextInt(51);

        int cleanCardNumber = randomCard;

        // Helps with giving each card a letter from the array
        if (randomCard >= 13 && randomCard < 26) {
            cleanCardNumber -= 13;
        } else if (randomCard >= 26 && randomCard < 39) {
            cleanCardNumber -= 26;
        } else if (randomCard >= 39) {
            cleanCardNumber -= 39;
        }

        if (cardsAvailable[randomCard]) {
            cardsAvailable[randomCard] = false;
            player.addToHand(cardImages.get(randomCard));
            player.addHandData(cardsInformation[cleanCardNumber]);
        } else {
            System.out.println("Card already exists...choosing a new one");
            giveCard(player);
        }
    }

    public void shuffleCards() { // Set all cards back to false on the shuffle so they're ready to be used
        Arrays.fill(cardsAvailable, Boolean.TRUE); // Set all cards to 'available'/true
    }
}
