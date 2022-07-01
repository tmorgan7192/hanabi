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

    public static Function<Hand, Card> getCard(int cardIndex) {
        return hand -> hand.hand().get(cardIndex);
    }

    public static Function<Card, Integer> getCardIndex(Hand hand) {
        return card -> hand.hand().indexOf(card);
    }

    public static Function<TableState, Hand> updateHand(int cardIndex, Card newCard) {

        return tableState -> {
            Hand hand = Hands.getHand().apply(tableState);
            return new Hand(
                    IntStream.range(0, hand.hand().size())
                            .mapToObj(index-> (index == cardIndex) ? newCard : Hand.getCard(index).apply(hand))
                            .collect(Collectors.toList())
            );
        };
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

    public static Function<Hand, Hand> updateKnowledge(Knowledge knowledge, Knowledge.Meta meta) {
        return hand -> {
            List<Card> newHand = new ArrayList<>();
            for (int index = 0; index < hand.hand().size(); ++index) {
                newHand.add(Card.updateKnowledge(knowledge, meta).apply(Hand.getCard(index).apply(hand)));
            }
            return new Hand(newHand);
        };
    }

    public static BiFunction<Integer, Hand, Knowledge> typeAppearsGivenNumberOfTimes() {
        return (count, hand) -> {
            Optional<Knowledge.Color> colorWithCorrectCount = Arrays.stream(Knowledge.Color.values())
                    .filter(color -> Objects.equals(getColorCount().apply(color, hand), count))
                    .findFirst();
            if (colorWithCorrectCount.isPresent()) {
                return new Knowledge(Knowledge.KnowledgeType.COLOR, colorWithCorrectCount.get().toString());
            }
            Optional<Knowledge.CardNumber> numberWithCorrectCount = Arrays.stream(Knowledge.CardNumber.values())
                    .filter(number -> Objects.equals(getNumberCount().apply(number, hand), count))
                    .findFirst();
            return numberWithCorrectCount.map(
                    cardNumber -> new Knowledge(Knowledge.KnowledgeType.NUMBER, cardNumber.toString())
                ).orElse(null);
        };
    }

    public static Function<Knowledge, List<Card>> getCardsMatchingKnowledge(Hand hand) {
        return (knowledge) -> {
            if (knowledge == null) {
                return null;
            }
            return hand.hand().stream().filter(Card.matchesKnowledge(knowledge)).collect(Collectors.toList());
        };
    }

}
