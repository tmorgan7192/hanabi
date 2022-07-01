package models;

import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public record Hands(List<Hand> hands) {

    public static Function<TableState, Hand> getHand() {
        return tableState -> tableState.hands().hands().get(tableState.activePlayerIndex());
    }

    public static BiFunction<Integer, TableState, Card> getCard() {
        return (cardIndex, tableState) -> Hands.getHand().apply(tableState).hand().get(cardIndex);
    }

    public static Function<Hands, Hands> updateHand(int playerIndex, Hand newHand) {
        return hands -> new Hands(
            IntStream.range(0, hands.hands().size())
                .mapToObj(index-> (index == playerIndex) ? newHand : hands.hands().get(index))
                .collect(Collectors.toList())
        );
    }

    public static Function<TableState, Hands> addCardToHand(Card card) {
        return map(Hand.addCardToHand(card));
    }

    public static Function<TableState, Hands> removeCardFromHand(int cardIndex) {
        return map(Hand.removeCardFromHand(cardIndex));
    }

    public static Function<TableState, Hands> updateKnowledge(Knowledge knowledge) {
        return map(Hand.updateKnowledge(knowledge));
    }


    public static Function<TableState, Hands> map(Function<Hand, Hand> function) {
        return tableState ->
            updateHand(tableState.activePlayerIndex(), function.apply(getHand().apply(tableState))).apply(tableState.hands());
    }
}
