package models;

import java.util.*;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static models.Card.getKeys;

public record Hand(List<Card> hand, Integer index) {
    public String toString(boolean isActive){
        return isActive ? "-> " + hand.toString() : hand.toString();
    }
    @Override
    public String toString() {
        return hand.toString();
    }

    public int size() {
        return hand.size();
    }

    private static BiFunction<String, Hand, Integer> getTraitCount(Knowledge.KnowledgeType type) {
        return (value, hand) -> (int) hand.hand().stream().filter(card -> card.getTrait(type).equals(value)).count();
    }

    public static BiFunction<Integer, TableState, Card> getCardFromActivePlayer() {
        return (cardIndex, tableState) -> getCard().apply(cardIndex, Hands.getHand().apply(tableState));
    }

    public static BiFunction<Integer, Hand, Card>  getCard() {
        return (cardIndex, hand) -> hand.hand().get(cardIndex);
    }

    public static Function<Card, Integer> getCardIndex(Hand hand) {
        return card -> hand.hand().indexOf(card);
    }

    public static Function<Hand, Hand> addCardToHand(Card card) {
        return hand -> {
            List<Card> newHand = hand.hand();
            newHand.add(card);
            return new Hand(newHand, hand.index());
        };
    }

    public static Function<Hand, Hand> removeCardFromHand(int cardIndex) {
        return hand -> {
            List<Card> newHand = hand.hand();
            newHand.remove(cardIndex);
            return new Hand(newHand, hand.index());
        };
    }

    public static Function<Hand, Hand> bumpTurnsInHand() {
        return hand -> new Hand(
            hand.hand().stream()
            .map(
                card -> new Card(
                    card.color(),
                    card.number(),
                    card.knowledgeMap(),
                    card.meta(),
                    card.numberOfTurnsInHand() + 1
                )
            )
            .collect(Collectors.toList()),
            hand.index
        );
    }

    public static Function<Hand, Hand> updateKnowledge(Knowledge knowledge) {
        return hand -> {
            List<Card> newHand = new ArrayList<>();
            Knowledge.Meta meta = Knowledge.checkMetaImplications(knowledge).apply(hand);
            for (int index = 0; index < hand.size(); ++index) {
                Card oldCard = Hand.getCard().apply(index, hand);
                Card newCard = Card.updateKnowledge(knowledge).apply(oldCard);
                if (Card.matchesKnowledge(knowledge).test(oldCard))
                    newCard = Card.updateMeta(meta).apply(newCard);
                newHand.add(newCard);
            }

            return new Hand(newHand, hand.index());
        };
    }

    public static BiFunction<Integer, Hand, Knowledge> knowledgeAppearsGivenNumberOfTimes(
        Knowledge.KnowledgeType defaultType
    ) {
        return (count, hand) -> {
            Knowledge defaultKnowledge = getKnowledgeWithCount(defaultType).apply(count, hand);
            return defaultKnowledge != null
                ? defaultKnowledge
                : getKnowledgeWithCount(Knowledge.getOtherType(defaultType)).apply(count, hand);
        };
    }

    public static BiFunction<Integer, Hand, Knowledge> getKnowledgeWithCount(Knowledge.KnowledgeType type) {
        return (count, hand) -> {
            Optional<String> traitWithCorrectCount = getKeys(type).stream()
                    .filter(key -> Objects.equals(getTraitCount(type).apply(key, hand), count))
                    .findFirst();
            return traitWithCorrectCount.map(s -> new Knowledge(type, s, hand.index())).orElse(null);
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

    public static Predicate<TableState> someNonActivesMatch(Predicate<Card> predicate, int minMatch) {
        return tableState -> Hands.getNonActiveHands().apply(tableState).stream()
                .map(Hand::hand).flatMap(List::stream).filter(predicate).count() >= minMatch;
    }

    public static Predicate<TableState> anyNonActiveMatch(Predicate<Card> predicate) {
        return someNonActivesMatch(predicate, 1);
    }

    public static Function<TableState, List<Integer>> activeMatches(Predicate<Card> predicate) {
        return tableState -> {
            List<Card> cards = Hands.getHand().apply(tableState).hand();
            return cards.stream().filter(predicate).map(cards::indexOf).collect(Collectors.toList());
        };
    }

    public static Function<TableState, Integer> firstActiveMatch(Predicate<Card> predicate) {
        return tableState -> {
            List<Integer> matches = activeMatches(predicate).apply(tableState);
            if (!matches.isEmpty()){
                return matches.get(0);
            }
            return null;
        };
    }

    public static Predicate<TableState> someActivesMatch(Predicate<Card> predicate, int minMatch) {
        return tableState -> activeMatches(predicate).apply(tableState).size() >= minMatch;
    }
    public static Predicate<TableState> anyActiveMatch(Predicate<Card> predicate) {
        return someActivesMatch(predicate, 1);
    }

    public static Predicate<Hand> knowledgeIsKnown(Knowledge knowledge) {
        return hand -> hand.hand().stream().allMatch(card -> card.getKnowledgeMap(knowledge.type()).get(knowledge.value()) != null);
    }

}
