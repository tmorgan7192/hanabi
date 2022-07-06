package models;

import java.util.Random;
import java.util.Stack;
import java.util.function.Function;

import static game.Util.getNumCards;

public record Deck(Stack<Card> deck) {
    public int size() {
        return deck.size();
    }
    public static Deck initializeDeck() {
        Stack<Card> deck = new Stack<>();
        Random random = new Random();
        int index = 0;
        for (Knowledge.CardNumber number: Knowledge.CardNumber.values()) {
            int numCards = getNumCards(number);
            for (Knowledge.Color color : Knowledge.Color.values()) {
                for (int i=0; i<numCards; ++i){
                    deck.add(index, Card.createCard(color, number));
                    index = random.nextInt(deck.size());
                }
            }
        }
        return new Deck(deck);
    }


    public static Deck initializeDiscardPile() {
        return new Deck(new Stack<>());
    }

    public static Card getTopCardFromDeck(Deck deck) {
        if (deck.deck().isEmpty()) {
            throw new IllegalStateException("Should not call getTopCardFromDeck for empty deck");
        }
        return deck.deck().peek();
    }

    public static Function<Deck, Deck> removeCardFromDeck() {
        return deck -> {
            if (deck.deck().isEmpty()) {
                throw new IllegalStateException("Should not call getTopCardFromDeck for empty deck");
            }
            Stack<Card> newDeck = (Stack<Card>) deck.deck().clone();
            newDeck.pop();
            return new Deck(newDeck);
        };
    }

    public static Function<Deck, Deck> addCardToDeck(Card card) {
        return deck -> {
            Stack<Card> newDeck = deck.deck();
            newDeck.add(card);
            return new Deck(newDeck);
        };

    }

}
