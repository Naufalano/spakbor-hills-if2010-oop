package com.spakbor.action;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Scanner;
import com.spakbor.cls.core.Player;

enum Suit {
    HEARTS("H"), DIAMONDS("D"), CLUBS("C"), SPADES("S");
    private String symbol;
    Suit(String symbol) { this.symbol = symbol; }
    @Override
    public String toString() { return symbol; }
}

enum Rank {
    TWO("2", 2), THREE("3", 3), FOUR("4", 4), FIVE("5", 5), SIX("6", 6),
    SEVEN("7", 7), EIGHT("8", 8), NINE("9", 9), TEN("10", 10),
    JACK("J", 10), QUEEN("Q", 10), KING("K", 10), ACE("A", 11); 

    private String displayName;
    private int value;

    Rank(String displayName, int value) {
        this.displayName = displayName;
        this.value = value;
    }

    public int getValue() { return value; }
    @Override
    public String toString() { return displayName; }
}

class Card {
    private final Suit suit;
    private final Rank rank;

    public Card(Suit suit, Rank rank) {
        this.suit = suit;
        this.rank = rank;
    }

    public Suit getSuit() {
        return suit;
    }

    public Rank getRank() {
        return rank;
    }

    public int getValue() {
        return rank.getValue();
    }

    @Override
    public String toString() {
        return rank.toString() + suit.toString();
    }
}

class Deck {
    private List<Card> cards;

    public Deck() {
        cards = new ArrayList<>();
        for (Suit suit : Suit.values()) {
            for (Rank rank : Rank.values()) {
                cards.add(new Card(suit, rank));
            }
        }
    }

    public void shuffle() {
        Collections.shuffle(cards);
    }

    public Card dealCard() {
        if (cards.isEmpty()) {
            
            System.out.println("Dek kosong! Shuffling dek baru...");
            cards = new ArrayList<>();
            for (Suit suit : Suit.values()) {
                for (Rank rank : Rank.values()) {
                    cards.add(new Card(suit, rank));
                }
            }
            shuffle();
            if (cards.isEmpty()) return null;
        }
        return cards.remove(0);
    }

    public int cardsRemaining() {
        return cards.size();
    }
}

class Hand {
    private List<Card> cards;
    private String ownerName;

    public Hand(String ownerName) {
        this.ownerName = ownerName;
        this.cards = new ArrayList<>();
    }

    public void addCard(Card card) {
        if (card != null) {
            cards.add(card);
        }
    }

    public int getValue() {
        int value = 0;
        int aceCount = 0;
        for (Card card : cards) {
            value += card.getValue();
            if (card.getRank() == Rank.ACE) {
                aceCount++;
            }
        }
        while (value > 21 && aceCount > 0) {
            value -= 10;
            aceCount--;
        }
        return value;
    }

    public void clear() {
        cards.clear();
    }

    public List<Card> getCards() {
        return cards;
    }

    public boolean isBust() {
        return getValue() > 21;
    }

    public boolean hasBlackjack() {
        return cards.size() == 2 && getValue() == 21;
    }

    public String toString(boolean hideFirstCard) {
        StringBuilder sb = new StringBuilder();
        sb.append(ownerName).append("'s hand: ");
        if (cards.isEmpty()) {
            sb.append("[KOSONG]");
            return sb.toString();
        }

        for (int i = 0; i < cards.size(); i++) {
            if (i == 0 && hideFirstCard) {
                sb.append("[KARTU TERTUTUP]");
            } else {
                sb.append(cards.get(i).toString());
            }
            if (i < cards.size() - 1) {
                sb.append(", ");
            }
        }
        if (!hideFirstCard || cards.size() == 1) {
            sb.append(" (Nilai: ").append(getValue()).append(")");
        } else if (cards.size() > 1) {
            sb.append(" (Nilai kartu terbuka: ").append(cards.get(1).getValue()).append(")");
        }
        return sb.toString();
    }
}

public class Blackjack {
    private Scanner scanner;
    private Player player;
    private Deck deck;
    private Hand playerHand;
    private Hand dealerHand;
    private int currentBet;

    public Blackjack(Player player) {
        this.player = player;
        deck = new Deck();
        playerHand = new Hand(player.getName());
        dealerHand = new Hand("Dealer");
        scanner = new Scanner(System.in);
    }


    public void playRound() {
        deck.shuffle();
        playerHand.clear();
        dealerHand.clear();
        currentBet = 0;

        System.out.println("\n--- LET'S PLAY BLACKJACK ---");
        System.out.print("\nMau main berapa mazeh? > ");
        String bet = scanner.nextLine().trim();
        int money = Integer.parseInt(bet);
        if (player.getGold() >= money) {
            this.currentBet = money;
            System.out.println("Taruhan: " + currentBet + "g.");
        } else {
            System.out.println("\nJangan judi kalau miskin mas. Mending gawe.");
            return;
        }
        System.out.println("\nSaldo Anda: " + player.getGold() + "g.");

        playerHand.addCard(deck.dealCard());
        playerHand.addCard(deck.dealCard());
        dealerHand.addCard(deck.dealCard());
        dealerHand.addCard(deck.dealCard());

        System.out.println(dealerHand.toString(true));
        System.out.println(playerHand.toString(false));

        if (playerHand.hasBlackjack()) {
            System.out.println("BLACKJACK! Win ga nih bos?");
            if (dealerHand.hasBlackjack()) {
                System.out.println("Dealer juga Blackjack! Seri cik.");
            } else {
                System.out.println("Beuh menang ternyata.");
                player.setGold(player.getGold() + (int) (currentBet * 1.5));
                System.out.println("Menang " + (int) (currentBet * 1.5) + "g cik!");
            }
            return;
        }
        if (dealerHand.hasBlackjack()) {
             System.out.println(dealerHand.toString(false));
             System.out.println("Dealer Blackjack! Anda rungkad.");
             player.spendGold(currentBet);
             return;
        }

        while (true) {
            System.out.print("Hit atau Stand? (h/s): ");
            String choice = scanner.nextLine().trim().toLowerCase();
            if (choice.equals("h")) {
                playerHand.addCard(deck.dealCard());
                System.out.println(playerHand.toString(false));
                if (playerHand.isBust()) {
                    System.out.println("BUST! Anda melebihi 21. Anda kalah.");
                    player.spendGold(currentBet);
                    return;
                }
            } else if (choice.equals("s")) {
                System.out.println("Anda memilih Stand dengan nilai " + playerHand.getValue());
                break;
            } else {
                System.out.println("Pilihan tidak valid.");
            }
        }

        System.out.println("\n--- Giliran Dealer ---");
        System.out.println(dealerHand.toString(false));

        while (dealerHand.getValue() < 17) {
            System.out.println("Dealer Hit...");
            try { Thread.sleep(1000); } catch (InterruptedException e) { Thread.currentThread().interrupt(); }
            dealerHand.addCard(deck.dealCard());
            System.out.println(dealerHand.toString(false));
            if (dealerHand.isBust()) {
                System.out.println("Dealer BUST! Kamu menang cik!");
                player.setGold(player.getGold() + currentBet);
                return;
            }
        }
        if (dealerHand.getValue() >= 17 && !dealerHand.isBust()) {
             System.out.println("Dealer Stand dengan nilai " + dealerHand.getValue());
        }

        System.out.println("\n--- Hasil Akhir ---");
        System.out.println(playerHand.toString(false));
        System.out.println(dealerHand.toString(false));

        int playerValue = playerHand.getValue();
        int dealerValue = dealerHand.getValue();

        if (playerValue > dealerValue) {
            System.out.println("Kamu menang!");
            player.setGold(player.getGold() + currentBet);
        } else if (dealerValue > playerValue) {
            System.out.println("Dealer menang. Anda rungkad.");
            player.spendGold(currentBet);
        } else {
            System.out.println("Seri cik. Fyuh.");
        }
    }
}
