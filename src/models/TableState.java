package models;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

import static game.Game.numPlayers;
import static game.Util.getNumCards;
import static models.CardStacks.addCardToStack;
import static models.CardStacks.initializeCardStacks;
import static models.Deck.*;
import static models.Hand.getCardFromActivePlayer;
import static models.Hands.*;

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

    @Contract("_, _ -> new")
    public static @NotNull TableState initializeTable(int numPlayers, Deck deck) throws IllegalArgumentException {
        int numCards = getNumCards(numPlayers);

        List<Hand> hands = new ArrayList<>();
        for (int playerIndex = 0; playerIndex < numPlayers; ++playerIndex) {
            List<Card> hand = new ArrayList<>();
            for (int cardIndex = 0; cardIndex < numCards; ++cardIndex) {
                Card card = getTopCardFromDeck(deck);
                deck = doRemoveCardFromDeck().apply(deck);
                hand.add(card);
            }
            hands.add(new Hand(hand, playerIndex));
        }

        return new TableState(
            deck,
            initializeDiscardPile(),
            new Hands(hands),
            initializeCardStacks(),
            MAX_TOKEN_COUNT,
            MAX_HINT_COUNT,
            numPlayers,
                0
        );
    }

    @Override
    @Contract(pure = true)
    public @NotNull String toString() {
        StringBuilder builder = new StringBuilder("\nDeck size: ")
                .append(this.deck.size())
                .append("\nDiscard size: ")
                .append(this.discardPile.size())
                .append("\nHints: ")
                .append(this.hintCount)
                .append("\nTokens: ")
                .append(this.tokenCount)
                .append("\n\nStacks: ");
        for (String stack: this.cardStacks.cardStacks().keySet().stream()
                .map(cards -> cards.toString().substring(0, 1) + this.cardStacks.topOfCardStack().apply(cards))
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

    @Contract(pure = true)
    public static @NotNull Function<TableState, TableState> updateKnowledge(Knowledge knowledge) {
        return mapWithIndex(Hand.updateKnowledge(knowledge), knowledge.playerIndex());
    }

    @Contract(pure = true)
    public static @NotNull Function<TableState, TableState> drawCard() {
        return addCardToHand(getTopCardFromDeck()).andThen(removeCardFromDeck());
    }

    @Contract(pure = true)
    public static @NotNull Function<TableState, TableState> doDiscardCard(int cardIndex) {
        return addCardToDiscardPile(cardIndex).andThen(removeCardFromHand(cardIndex));
    }

    @Contract(pure = true)
    private static @NotNull Function<TableState, TableState> removeCardFromDeck() {
        return tableState -> new TableState(
                doRemoveCardFromDeck().apply(tableState.deck()),
                tableState.discardPile(),
                tableState.hands(),
                tableState.cardStacks(),
                tableState.tokenCount(),
                tableState.hintCount(),
                tableState.otCount(),
                (tableState.activePlayerIndex())
        );
    }

    @Contract(pure = true)
    public static @NotNull Function<TableState, TableState> addCardToDiscardPile(int cardIndex) {
        return tableState ->
                new TableState(
                        tableState.deck(),
                        addCardToDeck(getCardFromActivePlayer(cardIndex).apply(tableState)).apply(tableState.discardPile()),
                        tableState.hands(),
                        tableState.cardStacks(),
                        tableState.tokenCount(),
                        tableState.hintCount(),
                        tableState.otCount(),
                        tableState.activePlayerIndex()
                );
    }

    @Contract(pure = true)
    public static @NotNull Function<TableState, TableState> updateHand(Hand newHand, int playerIndex) {
        return tableState ->
                new TableState(
                        tableState.deck(),
                        tableState.discardPile(),
                        new Hands(tableState.hands().hands().stream()
                                .map(hand -> hand.index() == playerIndex ? newHand : hand)
                                .toList()
                        ),
                        tableState.cardStacks(),
                        tableState.tokenCount(),
                        tableState.hintCount(),
                        tableState.otCount(),
                        (tableState.activePlayerIndex())
                );
    }

    @Contract(pure = true)
    public static @NotNull Function<TableState, TableState> attemptPlayCard(int cardIndex) {
        return tableState -> {
            Card card = getCardFromActivePlayer(cardIndex).apply(tableState);
            boolean isLegalPlay = tableState.cardStacks().isLegalPlay().test(card);
            Function<TableState, TableState> moveCardFromHand = isLegalPlay
                    ? updateCardStacks(addCardToStack().apply(card, tableState.cardStacks()))
                    : addCardToDiscardPile(cardIndex).andThen(decrementTokenCount());
            return moveCardFromHand.andThen(removeCardFromHand(cardIndex)).apply(tableState);
        };
    }

    @Contract(pure = true)
    public static @NotNull Function<TableState, TableState> updateCardStacks(CardStacks newCardStacks) {
        return tableState ->
                new TableState(
                        tableState.deck(),
                        tableState.discardPile(),
                        tableState.hands(),
                        newCardStacks,
                        tableState.tokenCount(),
                        tableState.hintCount(),
                        tableState.otCount(),
                        tableState.activePlayerIndex()
                );
    }

    @Contract(pure = true)
    public static @NotNull Function<TableState, TableState> decrementTokenCount() {
        return tableState ->
                new TableState(
                        tableState.deck(),
                        tableState.discardPile(),
                        tableState.hands(),
                        tableState.cardStacks(),
                        tableState.tokenCount() - 1,
                        tableState.hintCount(),
                        tableState.otCount(),
                        tableState.activePlayerIndex()
                );
    }

    @Contract(pure = true)
    public static @NotNull Function<TableState, TableState> updateHintCount(int delta) {
        return tableState -> {
            int newHintCount = Math.min(tableState.hintCount() + delta, MAX_HINT_COUNT);
            if (newHintCount < 0) {
                throw new IllegalStateException("Can only give hint if there are hints available");
            }
            return new TableState(
                    tableState.deck(),
                    tableState.discardPile(),
                    tableState.hands(),
                    tableState.cardStacks(),
                    tableState.tokenCount(),
                    newHintCount,
                    tableState.otCount(),
                    tableState.activePlayerIndex()
            );
        };
    }

    @Contract(pure = true)
    private static @NotNull Function<TableState, TableState> updateOtCount() {
        return tableState ->
                new TableState(
                        tableState.deck(),
                        tableState.discardPile(),
                        tableState.hands(),
                        tableState.cardStacks(),
                        tableState.tokenCount(),
                        tableState.hintCount(),
                        tableState.deck().deck().isEmpty() ? tableState.otCount() - 1 : tableState.otCount(),
                        tableState.activePlayerIndex()
                );
    }

    @Contract(pure = true)
    private static @NotNull Function<TableState, TableState> incrementActivePlayerIndex() {
        return tableState ->
                new TableState(
                        tableState.deck(),
                        tableState.discardPile(),
                        tableState.hands(),
                        tableState.cardStacks(),
                        tableState.tokenCount(),
                        tableState.hintCount(),
                        tableState.otCount(),
                        (tableState.activePlayerIndex() + 1) % numPlayers
                );
    }

    @Contract(pure = true)
    private static @NotNull Function<TableState, TableState> bumpTurnsInHand() {
        return map(Hand.bumpTurnsInHand());
    }

    @Contract(pure = true)
    public static @NotNull Function<TableState, TableState> endTurn() {
        return bumpTurnsInHand().andThen(updateOtCount()).andThen(incrementActivePlayerIndex());
    }
}
