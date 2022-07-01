package models;

import java.util.*;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.IntStream;

public record CardStacks(Map<Knowledge.Color, Stack<Card>> cardStacks) {
    public static CardStacks initializeCardStacks(){
        Map<Knowledge.Color, Stack<Card>> cardStacks = new HashMap<>();
        for (Knowledge.Color color : Knowledge.Color.values()){
            cardStacks.put(color, new Stack<>());
        }
        return new CardStacks(cardStacks);
    }

    public Function<Knowledge.Color, Integer> topOfCardStack() {
        return color -> cardStacks.get(color).size();
    }

    public Predicate<Knowledge.Color> stackIsFull() {
        return color -> topOfCardStack().apply(color) == 5;
    }
    public static Predicate<CardStacks> stacksAreFull() {
        return cardStacks -> Arrays.stream(Knowledge.Color.values()).allMatch(cardStacks.stackIsFull());
    }

    public Predicate<Card> isLegalPlay() {
        return card -> topOfCardStack().apply(card.color()) == card.getNumber() - 1;
    }

    public static BiFunction<Card, CardStacks, CardStacks> addCardToStack(){
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

    public static Function<TableState, Integer> getScore() {
        return tableState -> IntStream.range(0,5).reduce(
                0,
                (subtotal, ordinal) ->
                    subtotal + tableState.cardStacks().topOfCardStack().apply(Knowledge.Color.values()[ordinal])
        );
    }
}
