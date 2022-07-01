package models;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

import static game.Game.numPlayers;
import static game.Util.getNumCards;

public record TableState(
    Deck deck, 
    Deck discardPile, 
    Hands hands, 
    CardStacks cardStacks, 
    int tokenCount, 
    int hintCount, 
    int otCount, 
    int activePlayerIndex
) {

    public static final int MAX_TOKEN_COUNT = 3;
    public static final int MAX_HINT_COUNT = 8;

    public static TableState initializeTable(int numPlayers, Deck deck) throws IllegalArgumentException {
        int numCards = getNumCards(numPlayers);

        List<Hand> hands = new ArrayList<>();
        for (int playerIndex = 0; playerIndex < numPlayers; ++playerIndex) {
            List<Card> hand = new ArrayList<>();
            for (int cardIndex = 0; cardIndex < numCards; ++cardIndex) {
                Card card = Deck.getTopCardFromDeck(deck);
                deck = Deck.removeCardFromDeck().apply(deck);
                hand.add(card);
            }
            hands.add(new Hand(hand));
        }

        return new TableState(
            deck,
            Deck.initializeDiscardPile(),
            new Hands(hands),
            CardStacks.initializeCardStacks(),
            MAX_TOKEN_COUNT,
            MAX_HINT_COUNT,
            numPlayers,
                0
        );
    }

    public static Function<TableState, TableState> drawCard(int playerIndex) {
        return tableState -> new TableState(
                Deck.removeCardFromDeck().apply(tableState.deck()),
                tableState.discardPile(),
                Hands.addCardToHand(Deck.getTopCardFromDeck(tableState.deck())).apply(tableState),
                tableState.cardStacks(),
                tableState.tokenCount(),
                tableState.hintCount(),
                decrementOtCount(tableState.deck).apply(tableState.otCount),
            (tableState.activePlayerIndex + 1) % numPlayers
        );
    }

    public static Function<TableState, TableState> discardCard(int cardIndex) {
        return tableState ->
            drawCard(tableState.activePlayerIndex).apply(
                new TableState(
                    tableState.deck(),
                    Deck.addCardToDeck(
                        Hands.getCard().apply(cardIndex, tableState)
                    ).apply(tableState.discardPile()),
                    Hands.removeCardFromHand(cardIndex).apply(tableState),
                    tableState.cardStacks(),
                    tableState.tokenCount(),
                    Math.max(tableState.hintCount() + 1, MAX_HINT_COUNT),
                    decrementOtCount(tableState.deck).apply(tableState.otCount),
                    tableState.activePlayerIndex
                )
            );
    }

    public static Function<TableState, TableState> giveHint(Knowledge knowledge) {
        return tableState -> {
            if (tableState.hintCount() == 0) {
                throw new IllegalStateException("Can only give hint if there are hints available");
            }
            return new TableState(
                tableState.deck(),
                tableState.discardPile(),
                Hands.updateKnowledge(knowledge).apply(tableState),
                tableState.cardStacks(),
                tableState.tokenCount(),
                tableState.hintCount() - 1,
                decrementOtCount(tableState.deck).apply(tableState.otCount),
                (tableState.activePlayerIndex() + 1) % numPlayers
            );
        };
    }

    public static Function<TableState, TableState> playCardOnStack(int cardIndex) {
        return tableState -> {
            Card card = Hands.getCard().apply(cardIndex, tableState);
            boolean isLegalPlay = tableState.cardStacks().isLegalPlay().test(card);

            return drawCard(tableState.activePlayerIndex()).apply(
                new TableState(
                    tableState.deck(),
                    Deck.addCardToDeck(card).apply(tableState.discardPile()),
                    Hands.removeCardFromHand(cardIndex).apply(tableState),
                    isLegalPlay ? CardStacks.addCardToStack().apply(card, tableState.cardStacks()) : tableState.cardStacks(),
                    isLegalPlay ? tableState.tokenCount() : tableState.tokenCount() - 1,
                    tableState.hintCount(),
                    decrementOtCount(tableState.deck).apply(tableState.otCount),
                        tableState.activePlayerIndex()
                )
            );
        };
    }

    public static Function<Integer, Integer> decrementOtCount(Deck deck) {
        return otCount -> deck.deck().isEmpty() ? otCount - 1 : otCount;
    }
}
