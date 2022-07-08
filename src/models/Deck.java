package models;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Random;
import java.util.Stack;
import java.util.function.Function;

import static game.Util.getNumCards;

public record Deck(Stack<Card> deck) {
    @Contract(" -> new")
    public static @NotNull Deck initializeDeck() {
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

    @Contract(" -> new")
    public static @NotNull Deck initializeDiscardPile() {
        return new Deck(new Stack<>());
    }

    @Contract(pure = true)
    public int size() {
        return deck.size();
    }

    @Contract(pure = true)
    public static @Nullable Card getTopCardFromDeck(@NotNull Deck deck) {
        if (deck.deck().isEmpty()) {
            return null;
        }
        return deck.deck().peek();
    }

    @Contract(pure = true)
    public static @NotNull Function<TableState, Card> getTopCardFromDeck() {
        return tableState -> getTopCardFromDeck(tableState.deck());
    }

    @Contract(pure = true)
    public static @NotNull Function<Deck, Deck> doRemoveCardFromDeck() {
        return deck -> {
            if (deck.deck().isEmpty()) {
                return deck;
            }
            Stack<Card> newDeck = (Stack<Card>) deck.deck().clone();
            newDeck.pop();
            return new Deck(newDeck);
        };
    }

    @Contract(pure = true)
    public static @NotNull Function<Deck, Deck> addCardToDeck(Card card) {
        return deck -> {
            Stack<Card> newDeck = deck.deck();
            newDeck.add(card);
            return new Deck(newDeck);
        };

    }
}
