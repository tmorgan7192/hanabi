package models;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.Comparator;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static game.Game.numPlayers;
import static models.TableState.updateHand;

public record Hands(List<Hand> hands) {
    @Override
    @Contract(pure = true)
    public @NotNull String toString() {
        StringBuilder builder = new StringBuilder();
        for (Hand hand: hands){
            builder.append("\n").append(hand.toString());
        }
        return builder.toString();
    }

    @Contract(pure = true)
    public Hand get(int playerIndex){
        return this.hands.get(playerIndex);
    }

    @Contract(pure = true)
    public Stream<Hand> stream(){
        return this.hands.stream();
    }

    @Contract(pure = true)
    public static @NotNull Function<TableState, Hand> getHand(Integer playerIndex) {
        return tableState -> tableState.hands().get(playerIndex);
    }
    
    @Contract(pure = true)
    public static @NotNull Function<TableState, Hand> getActivePlayerHand() {
        return tableState -> getHand(tableState.activePlayerIndex()).apply(tableState);
    }
    
    @Contract(pure = true)
    public static @NotNull Function<TableState, List<Hand>> getNonActiveHands() {
        return tableState -> tableState.hands().stream()
            .filter(hand -> hand.index() != tableState.activePlayerIndex())
            .sorted(Comparator.comparingInt(hand -> (hand.index() - tableState.activePlayerIndex() + numPlayers) % numPlayers))
            .collect(Collectors.toList());
    }

    @Contract(pure = true)
    public static @NotNull Function<TableState, TableState> addCardToHand(Function<TableState, Card> getCard) {
        return tableState -> map(Hand.addCardToHand(getCard.apply(tableState))).apply(tableState);
    }

    @Contract(pure = true)
    public static @NotNull Function<TableState, TableState> removeCardFromHand(int cardIndex) {
        return map(Hand.removeCardFromHand(cardIndex));
    }

    @Contract(pure = true)
    public static @NotNull Function<TableState, TableState> map(Function<Hand, Hand> function) {
        return tableState -> mapWithIndex(function, tableState.activePlayerIndex()).apply(tableState);
    }

    @Contract(pure = true)
    public static @NotNull Function<TableState, TableState> mapWithIndex(Function<Hand, Hand> function, int playerIndex) {
        return tableState -> updateHand(
            function.apply(Hands.getHand(playerIndex).apply(tableState)), playerIndex
        ).apply(tableState);
    }
}
