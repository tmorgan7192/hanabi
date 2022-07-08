package models;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.IntStream;

public record CardStacks(Map<Knowledge.Color, Stack<Card>> cardStacks) {
    @Contract(" -> new")
    public static @NotNull CardStacks initializeCardStacks(){
        Map<Knowledge.Color, Stack<Card>> cardStacks = new HashMap<>();
        for (Knowledge.Color color : Knowledge.Color.values()){
            cardStacks.put(color, new Stack<>());
        }
        return new CardStacks(cardStacks);
    }

    @Contract(pure = true)
    public @NotNull Function<Knowledge.Color, Integer> topOfCardStack() {
        return color -> cardStacks.get(color).size();
    }

    @Contract(pure = true)
    public @NotNull Predicate<Card> isLegalPlay() {
        return card -> topOfCardStack().apply(card.color()) == card.getNumber() - 1;
    }

    @Contract(pure = true)
    public static @NotNull BiFunction<Card, CardStacks, CardStacks> addCardToStack(){
        return (card, cardStacks) -> {
            Map<Knowledge.Color, Stack<Card>> newMap = new HashMap<>();
            for (Knowledge.Color color : Knowledge.Color.values()) {
                Stack<Card> newStack = cardStacks.cardStacks().get(color);
                if (card.color() == color) {
                    newStack.add(card);
                }
                newMap.put(color, newStack);
            }

            return new CardStacks(newMap);
        };
    }

    @Contract(pure = true)
    public static @NotNull Function<TableState, Integer> getScore() {
        return tableState -> IntStream.range(0,5).reduce(
                0,
                (subtotal, ordinal) ->
                    subtotal + tableState.cardStacks().topOfCardStack().apply(Knowledge.Color.values()[ordinal])
        );
    }
}
