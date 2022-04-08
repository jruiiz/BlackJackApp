package sample;

import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import java.io.*;
import java.util.ArrayList;
import java.util.Scanner;

public class Main extends Application {

    private final VBox vBox = new VBox();
    private static final HBox dealerBox = new HBox();
    private final HBox optionsBox = new HBox();
    private final HBox betBox = new HBox();
    private final HBox informationBox = new HBox();
    private final static HBox playerBox = new HBox();
    private final BorderPane borderPane = new BorderPane();
    private final Button hitBtn = new Button("Hit");
    private final Button standBtn = new Button("Stand");
    private final Button doubleBtn = new Button("Double");
    private final Button newGameBtn = new Button("NEW GAME");
    private final Button betFive = new Button("$5");
    private final Button betTen = new Button("$10");
    private final Button betTwentyFive = new Button("$25");
    private final Button betFifty = new Button("$50");
    private final Button betAll = new Button("ALL");
    private final Label dealerLabel = new Label("↑ DEALER ↑     ");
    private final Label playerLabel = new Label("     ↓ PLAYER ↓");
    private Label moneyAvailable = new Label("Balance: $" + currentBalance);
    private Label playerCurrentPlay = new Label("Current hand: " + player.getHandValue());
    private Label dealerCurrentPlay = new Label("Dealer hand: " + dealer.getHandValue());
    private Label betAmountLabel = new Label("C U R R E N T  B E T  A M O U N T: $5");

    static Cards deck = new Cards(); // Creating the deck of cards
    static Player player = new Player("Player"); // Creating self player
    static Player dealer = new Player("Dealer"); // Creating dealer
    static int betAmount = 5;
    static int currentBalance = 100;
    static boolean dealerHasGone = false;
    static boolean playerLost = false;
    static boolean doubleDown = false;

    static File file = new File("recordedBlackJackData.txt");

    public static void main(String[] args) {
        initializeBalance(file.exists());

        // ===== GIVING THE INITIAL CARDS TO THE PLAYERS =====
        deck.dealCards(player);
        deck.dealCards(dealer);

        // ==== FOR ADDING CARDS ONTO THE SCREEN =====
        addCard(playerBox, player.getHand());
        addCard(dealerBox, dealer.getHand());

        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        betBox.getChildren().addAll(betFive, betTen, betTwentyFive, betFifty, betAll);
        betBox.setAlignment(Pos.CENTER);
        betBox.setSpacing(3);

        betFifty.setMaxWidth(50);

        dealerBox.setSpacing(2);
        playerBox.setSpacing(2);

        optionsBox.getChildren().addAll(dealerLabel, hitBtn, standBtn, doubleBtn, newGameBtn, playerLabel);
        optionsBox.setSpacing(5);

        informationBox.getChildren().addAll(moneyAvailable, playerCurrentPlay, dealerCurrentPlay);
        informationBox.setSpacing(20);

        vBox.getChildren().addAll(betAmountLabel, betBox, dealerBox, optionsBox, playerBox, informationBox);
        vBox.setSpacing(8);

        vBox.setAlignment(Pos.CENTER);
        borderPane.setCenter(vBox);

        dealerBox.setAlignment(Pos.CENTER);
        optionsBox.setAlignment(Pos.CENTER);
        informationBox.setAlignment(Pos.CENTER);
        playerBox.setAlignment(Pos.CENTER);
        newGameBtn.setDisable(true);

        hitBtn.setOnAction(e -> hitPlayer());

        standBtn.setOnAction(e -> playerStand(1));

        doubleBtn.setOnAction(e -> doubleDown());

        newGameBtn.setOnAction(e -> newGame());

        betFive.setOnAction(e -> bet(5));

        betTen.setOnAction(e -> bet(10));

        betTwentyFive.setOnAction(e -> bet(25));

        betFifty.setOnAction(e -> bet(50));

        betAll.setOnAction(e -> bet(currentBalance));

        primaryStage.setTitle("Black Jack - Jose Ruiz");
        primaryStage.setScene(new Scene(borderPane, 600, 333));
        primaryStage.setResizable(false);
        primaryStage.show();
    }

    public void newGame() { // Resets all nodes and variables on each instance (dealer/player)
        resetBalance();
        player.clearHand();
        dealer.clearHand();

        dealerBox.getChildren().removeAll(dealerBox.getChildren());
        playerBox.getChildren().removeAll(playerBox.getChildren());

        updateLabels("     ↓ PLAYER ↓", "↑ DEALER ↑     ");

        dealerHasGone = false;
        enableButtons();

        deck.shuffleCards();

        deck.dealCards(player);
        deck.dealCards(dealer);

        addCard(playerBox, player.getHand());
        addCard(dealerBox, dealer.getHand());

        playerCurrentPlay.setText("Current hand: " + player.getHandValue());
        dealerCurrentPlay.setText("Dealer hand: " + dealer.getHandValue());

        playerLost = false;
        doubleDown = false;
    }

    public static void initializeBalance(boolean fileExists) {  // If the file exists, then read from it or create one if missing
        if (!fileExists) {
            try (FileWriter fileWriter = new FileWriter(file); Scanner scanner = new Scanner(file)) {
                fileWriter.write("100");
                fileWriter.flush();

                currentBalance = Integer.parseInt(scanner.next());
            } catch (IOException e) {
                System.out.println(e.getMessage());
            }
        } else {
            try (Scanner scanner = new Scanner(file)) {
                currentBalance = Integer.parseInt(scanner.next());
            } catch (IOException e) {
                System.out.println(e.getMessage());
            }
        }

    }

    public void updateBalance(int newBalance) { // Update the balance after each play. Also, record new balance into file
        try (FileWriter fileWriter = new FileWriter(file); Scanner scanner = new Scanner(file)) {
            fileWriter.write(Integer.toString(newBalance));
            fileWriter.flush();

            currentBalance = Integer.parseInt(scanner.next());
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

    public void resetBalance() { // If player reaches <= 0, then reset to 100
        if (currentBalance <= 0) {
            System.out.println("\n BALANCE IS LESS THAN 100 \n");
            currentBalance = 100;
        }
    }

    public void hitPlayer() { // Give player a card
        doubleBtn.setDisable(true);
        deck.giveCard(player);
        addCard(playerBox, player.getHand());
        playerCurrentPlay.setText("Current hand: " + player.getHandValue());

        bustCheck();

        if (playerLost && !doubleDown) {
            placeBet(betAmount, 1);
        }
    }

    public void hitDealer() { // Give dealer a card
        deck.giveCard(dealer);
        addCard(dealerBox, dealer.getHand());
        dealerCurrentPlay.setText("Dealer hand: " + dealer.getHandValue());
    }

    public void playerStand(int betMultiplier) { // Delete the back facing card and then give the dealer a real card to "flip" over
        System.out.println(betMultiplier);
        placeBet(betAmount, betMultiplier);
        dealer.getHand().remove(1);
        standBtn.setDisable(true);
        dealerBox.getChildren().remove(1);
        hitBtn.setDisable(true);
        hitDealer();

        dealerPlay(player, dealer);
    }

    public void doubleDown() { // Double down, hit and double your bet
        doubleDown = true;
        hitPlayer();

        if (player.getHandValue() > 21) { // If the player loses on double down, then just remove money
            placeBet(betAmount, 2);
        } else { // If not, let the game continue
            playerStand(2);
        }

        disableButtons();
    }

    public void updateLabels(String playerLabel, String dealerLabel) {
        this.playerLabel.setText(playerLabel);
        this.dealerLabel.setText((dealerLabel));
    }

    public void disableButtons() {
        hitBtn.setDisable(true);
        standBtn.setDisable(true);
        doubleBtn.setDisable(true);

        betFive.setDisable(true);
        betTen.setDisable(true);
        betTwentyFive.setDisable(true);
        betFifty.setDisable(true);
        betAll.setDisable(true);
        newGameBtn.setDisable(false);
    }

    public void enableButtons() {
        hitBtn.setDisable(false);
        standBtn.setDisable(false);
        doubleBtn.setDisable(false);

        betFive.setDisable(false);
        betTen.setDisable(false);
        betTwentyFive.setDisable(false);
        betFifty.setDisable(false);
        betAll.setDisable(false);
        newGameBtn.setDisable(true);
    }

    public void dealerPlay(Player player, Player dealer) {
        while (dealer.getHandValue() < player.getHandValue() && dealer.getHandValue() <= 21) {
            hitDealer();
        }

        dealerHasGone = true;
        bustCheck();
    }

    public void bet(int betAmount) {
        Main.betAmount = betAmount;
        betAmountLabel.setText("C U R R E N T  B E T  A M O U N T: $" + Main.betAmount);
    }

    public void placeBet(int entryBet, int multiplier) { // Remove player money when standing
        System.out.println("Current balance bet: " + currentBalance);
        entryBet *= multiplier;
        currentBalance -= entryBet;
        moneyAvailable.setText("Balance: $" + currentBalance);
    }

    public void winMoney(int betAmount, int multiplier) {
        currentBalance += betAmount * multiplier;
        moneyAvailable.setText("Balance: $" + currentBalance);
    }

    public void bustCheck() { // Check all possibilities of a bust
        if ((player.getHandValue() > 21 && player.getHandValue() <= 21 && dealerHasGone) || (player.getHandValue() <= 21 && dealerHasGone && (dealer.getHandValue() < player.getHandValue()) || dealer.getHandValue() > 21)) {
            System.out.println("Player wins\n");

            if (!ifBlackJack()) {
                if (doubleDown) {
                    winMoney(betAmount * 2, 2);
                    System.out.println("Double down win");
                } else {
                    winMoney(betAmount, 2);
                }

                updateLabels("== PLAYER WINS ==", "== PLAYER WINS ==");
            } else {
                winMoney(betAmount, 3);
                updateLabels("== BLACK JACK WIN ==", "== BLACK JACK WIN ==");
            }

            disableButtons();
        } else if ((dealer.getHandValue() <= 21 && dealer.getHandValue() > player.getHandValue() && dealerHasGone) || ((player.getHandValue() > 21))) {
            playerLost = true;
            System.out.println("Dealer wins\n");
            updateLabels("== DEALER WINS ==", "== DEALER WINS ==");
            disableButtons();
        } else if (dealer.getHandValue() == player.getHandValue() && dealerHasGone) {
            if (doubleDown) {
                winMoney(betAmount * 2, 1);
            } else {
                winMoney(betAmount, 1);
            }

            updateLabels("== DRAW ==", "== DRAW ==");
            disableButtons();
        }

        updateBalance(currentBalance);
    }

    public boolean ifBlackJack() { // Checking if there was a black jack win
        return player.getHandValue() == 21 && player.getHand().size() == 2 && dealerHasGone;
    }

    public static void addCard(HBox box, ArrayList<ImageView> imgList) { // Checks the current hbox and if it doesn't contain all of the cards, add the missing.
        for (ImageView i : imgList) {
            if (!box.getChildren().contains(i)) {
                box.getChildren().add(i);
            }
        }
    }
}