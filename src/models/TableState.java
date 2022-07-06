package models;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

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

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder("\nDeck size: ")
                .append(this.deck.size())
                .append("\nDiscard size: ")
                .append(this.discardPile.size())
                .append("\nHints: ")
                .append(this.hintCount)
                .append("\nTokens: ")
                .append(this.tokenCount)
                .append("\n\nStacks: ");
        for (String stack: this.cardStacks.cardStacks().entrySet().stream()
                .map(entry -> entry.getKey().toString().substring(0, 1) + this.cardStacks.topOfCardStack().apply(entry.getKey()))
                .collect(Collectors.toList())
        ) {
            builder.append(stack).append(" ");
        }
        builder.append("\n");

        for (String hand :
                this.hands.hands().stream()
                .map(hand -> hand.toString(hand.index() == this.activePlayerIndex))
                .collect(Collectors.toList())
        ){
            builder.append("\n").append(hand);
        }
        return builder.append("\n").toString();
    }
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
            hands.add(new Hand(hand, playerIndex));
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

    public static Function<TableState, TableState> drawCard() {
        return tableState -> new TableState(
                Deck.removeCardFromDeck().apply(tableState.deck()),
                tableState.discardPile(),
                Hands.addCardToHand(Deck.getTopCardFromDeck(tableState.deck())).apply(tableState),
                tableState.cardStacks(),
                tableState.tokenCount(),
                tableState.hintCount(),
                tableState.otCount,
            (tableState.activePlayerIndex + 1) % numPlayers
        );
    }

    public static Function<TableState, TableState> andThenDraw(Function<TableState, TableState> function) {
        return tableState -> !tableState.deck.deck().isEmpty()
                ? function.andThen(drawCard()).apply(tableState)
                : function.apply(tableState);
    }

    public static Function<TableState, TableState> discardCard(int cardIndex) {
        return andThenDraw(doDiscardCard(cardIndex));
    }

    public static Function<TableState, TableState> doDiscardCard(int cardIndex) {
        return tableState ->
                new TableState(
                        tableState.deck(),
                        Deck.addCardToDeck(
                            Hand.getCardFromActivePlayer().apply(cardIndex, tableState)
                        ).apply(tableState.discardPile()),
                        Hands.removeCardFromHand(cardIndex).apply(tableState),
                        tableState.cardStacks(),
                        tableState.tokenCount(),
                        Math.min(tableState.hintCount() + 1, MAX_HINT_COUNT),
                        decrementOtCount(tableState.deck).apply(tableState.otCount),
                        tableState.activePlayerIndex
                );
    }

    public static Function<TableState, TableState> playCard(int cardIndex) {
        return andThenDraw(doPlayCard(cardIndex));
    }

    public static Function<TableState, TableState> doPlayCard(int cardIndex) {
        return tableState -> {
            Card card = Hand.getCardFromActivePlayer().apply(cardIndex, tableState);
            boolean isLegalPlay = tableState.cardStacks().isLegalPlay().test(card);

            return new TableState(
                    tableState.deck(),
                    Deck.addCardToDeck(card).apply(tableState.discardPile()),
                    Hands.removeCardFromHand(cardIndex).apply(tableState),
                    isLegalPlay ? CardStacks.addCardToStack().apply(card, tableState.cardStacks()) : tableState.cardStacks(),
                    isLegalPlay ? tableState.tokenCount() : tableState.tokenCount() - 1,
                    tableState.hintCount(),
                    decrementOtCount(tableState.deck).apply(tableState.otCount),
                    tableState.activePlayerIndex()
            );
        };
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

    public static Function<Integer, Integer> decrementOtCount(Deck deck) {
        return otCount -> deck.deck().isEmpty() ? otCount - 1 : otCount;
    }
}
