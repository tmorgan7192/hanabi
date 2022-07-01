package models;

import java.util.*;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public record Hand(List<Card> hand) {

    private static BiFunction<Knowledge.Color, Hand, Integer> getColorCount() {
        return (color, hand) -> (int) hand.hand().stream().filter(card -> card.color() == color).count();
    }

    private static BiFunction<Knowledge.CardNumber, Hand, Integer> getNumberCount() {
        return (number, hand) -> (int) hand.hand().stream().filter(card -> card.number() == number).count();
    }

    public static Function<Integer, Card> getCard(Hand hand) {
        return cardIndex -> hand.hand().get(cardIndex);
    }

    public static Function<Card, Integer> getCardIndex(Hand hand) {
        return card -> hand.hand().indexOf(card);
    }

    public static Function<Hand, Hand> updateHand(int cardIndex, Card newCard) {
        return hand -> new Hand(
            IntStream.range(0, hand.hand().size())
                .mapToObj(index-> (index == cardIndex) ? newCard : hand.hand().get(index))
                .collect(Collectors.toList())
        );
    }

    public static Function<Hand, Hand> addCardToHand(Card card) {
        return hand -> {
            List<Card> newHand = hand.hand();
            newHand.add(card);
            return new Hand(newHand);
        };
    }

    public static Function<Hand, Hand> removeCardFromHand(int cardIndex) {
        return hand -> {
            List<Card> newHand = hand.hand();
            newHand.remove(cardIndex);
            return new Hand(newHand);
        };
    }

    public static Function<Hand, Hand> updateKnowledge(Knowledge knowledge) {
        return hand -> {
            List<Card> newHand = new ArrayList<>();
            for (int index = 0; index < hand.hand().size(); ++index) {
                newHand.add(Card.updateKnowledge(knowledge).apply(Hand.getCard(hand).apply(index)));
            }
            return new Hand(newHand);
        };
    }

    public static BiFunction<Integer, Hand, Knowledge> knowledgeAppearsGivenNumberOfTimes() {
        return (count, hand) -> {
            Optional<Knowledge.Color> colorWithCorrectCount = Arrays.stream(Knowledge.Color.values())
                    .filter(color -> Objects.equals(getColorCount().apply(color, hand), count))
                    .findFirst();
            if (colorWithCorrectCount.isPresent()) {
                Optional<Card> card = hand.hand().stream()
                    .filter(c -> c.color() == colorWithCorrectCount.get()).findFirst();
                if (card.isPresent()) {
                    return new Knowledge(Knowledge.KnowledgeType.COLOR, colorWithCorrectCount.get().toString());
                }
                throw new IllegalStateException("Card with color " + colorWithCorrectCount.get() + " should be present");
            }
            Optional<Knowledge.CardNumber> numberWithCorrectCount = Arrays.stream(Knowledge.CardNumber.values())
                    .filter(number -> Objects.equals(getNumberCount().apply(number, hand), count))
                    .findFirst();
            if (numberWithCorrectCount.isPresent()) {
                Optional<Card> card = hand.hand().stream()
                    .filter(c -> c.color() == colorWithCorrectCount.get()).findFirst();
                if (card.isPresent()) {
                    return new Knowledge(Knowledge.KnowledgeType.NUMBER, numberWithCorrectCount.get().toString());
                }
                throw new IllegalStateException("Card with color " + colorWithCorrectCount.get() + " should be present");
            }
            return null;
        };
    }

}
