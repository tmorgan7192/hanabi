package models;

import java.util.Comparator;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static game.Game.numPlayers;

public record Hands(List<Hand> hands) {
    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        for (Hand hand: hands){
            builder.append("\n").append(hand.toString());
        }
        return builder.toString();
    }

    public static Function<TableState, Hand> getHand() {
        return tableState -> getHandByIndex(tableState.activePlayerIndex()).apply(tableState);
    }

    public static Function<TableState, Hand> getHandByIndex(Integer index) {
        return tableState -> tableState.hands().hands().get(index);
    }

    public static Function<TableState, List<Hand>> getNonActiveHands() {
        return tableState -> tableState.hands().hands().stream()
            .filter(hand -> hand.index() != tableState.activePlayerIndex())
            .sorted(Comparator.comparingInt(hand -> (hand.index() - tableState.activePlayerIndex() + numPlayers) % numPlayers))
            .collect(Collectors.toList());
    }

    public static Function<TableState, Hands> updateHand(Hand newHand, int playerIndex) {
        return tableState -> new Hands(
            tableState.hands().hands().stream()
                .map(hand -> hand.index() == playerIndex ? newHand : hand)
                .toList()
        );
    }

    public static Function<TableState, Hands> addCardToHand(Card card) {
        return map(Hand.addCardToHand(card));
    }

    public static Function<TableState, Hands> removeCardFromHand(int cardIndex) {
        return map(Hand.removeCardFromHand(cardIndex));
    }

    public static Function<TableState, Hands> updateKnowledge(Knowledge knowledge) {
        return tableState -> updateHand(
                Hand.updateKnowledge(knowledge).apply(Hands.getHandByIndex(knowledge.playerIndex()).apply(tableState)),
                knowledge.playerIndex()
            ).apply(tableState);
    }

    public static Function<TableState, Hands> map(Function<Hand, Hand> function) {
        return tableState -> mapWithIndex(function, tableState.activePlayerIndex()).apply(tableState);
    }

    public static Function<TableState, Hands> mapWithIndex(Function<Hand, Hand> function, int playerIndex) {
        return tableState -> updateHand(
            function.apply(Hands.getHandByIndex(playerIndex).apply(tableState)), playerIndex
        ).apply(tableState);
    }
}
