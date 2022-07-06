package models;

import java.util.*;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static game.Util.getNumCards;

public record Card(
    Knowledge.Color color,
    Knowledge.CardNumber number,
    Map<Knowledge.KnowledgeType, Map<String, Boolean>> knowledgeMap,
    Knowledge.Meta meta,
    int numberOfTurnsInHand
) {
    @Override
    public String toString() {
        return this.color().toString().substring(0, 1)
            + this.getNumber()
            + this.knowledgeToString(Knowledge.KnowledgeType.COLOR)
            + this.knowledgeToString(Knowledge.KnowledgeType.NUMBER)
                + Knowledge.metaToString(this.meta);
    }

    @Override
    public boolean equals(Object card) {
        return card instanceof Card && this.color == ((Card)card).color && this.number == ((Card)card).number;
    }

    public static Card createCard(Knowledge.Color color, Knowledge.CardNumber number) {
        return new Card(color, number, initializeKnowledgeMap(), null, 0);
    }

    public static Map<Knowledge.KnowledgeType, Map<String, Boolean>> initializeKnowledgeMap() {
        Map<Knowledge.KnowledgeType, Map<String, Boolean>> knowledgeMap = new HashMap<>();
        Map<String, Boolean> colorMap = new HashMap<>();
        Map<String, Boolean> numberMap = new HashMap<>();
        Map<String, Boolean> metaMap = new HashMap<>();
        for (Knowledge.Color color : Knowledge.Color.values()) {
            colorMap.put(color.toString(), null);
        }
        for (Knowledge.CardNumber number : Knowledge.CardNumber.values()) {
            numberMap.put(number.toString(), null);
        }
        for (Knowledge.Meta meta : Knowledge.Meta.values()) {
            metaMap.put(meta.toString(), null);
        }
        knowledgeMap.put(Knowledge.KnowledgeType.COLOR, colorMap);
        knowledgeMap.put(Knowledge.KnowledgeType.NUMBER, numberMap);
        return knowledgeMap;
    }

    public String getTrait(Knowledge.KnowledgeType type) {
        return type == Knowledge.KnowledgeType.COLOR ? this.color().toString() : this.number.toString();
    }

    public static List<String> getKeys(Knowledge.KnowledgeType type) {
        return switch (type) {
            case COLOR -> Arrays.stream(Knowledge.Color.values()).map(Enum::toString).toList();
            case NUMBER -> Arrays.stream(Knowledge.CardNumber.values()).map(Enum::toString).toList();
        };
    }

    public int getNumber() {
        return this.number.ordinal() + 1;
    }

    public static Predicate<Card> matchesKnowledge(Knowledge knowledge){
        return card ->
            (knowledge.type().equals(Knowledge.KnowledgeType.COLOR) && card.color().toString().equals(knowledge.value())) ||
            (knowledge.type().equals(Knowledge.KnowledgeType.NUMBER) && card.number().toString().equals(knowledge.value()));

    }
    public Map<String, Boolean> getKnowledgeMap(Knowledge.KnowledgeType type) {
        return this.knowledgeMap.get(type);
    }

    public String knowledgeToString(Knowledge.KnowledgeType type) {
        Map<String, Boolean> typedKnowledgeMap = this.knowledgeMap.get(type);
        if (typedKnowledgeMap.containsValue(true)) {
            List<String> keys = typedKnowledgeMap.entrySet().stream()
                .filter(Map.Entry::getValue)
                    .map(entry -> Knowledge.toString(type).apply(entry.getKey()))
                .collect(Collectors.toList());
            StringBuilder builder = new StringBuilder(" is ");
            for (String key : keys){
                builder.append(key);
            }
            return builder.toString();
        }
        if (typedKnowledgeMap.containsValue(false)) {
            List<String> keys = typedKnowledgeMap.entrySet().stream()
                .filter(entry -> !entry.getValue())
                .map(entry -> Knowledge.toString(type).apply(entry.getKey()))
                .collect(Collectors.toList());
            StringBuilder builder = new StringBuilder(" not ");
            for (String key : keys){
                builder.append(key);
            }
            return builder.toString();
        }
        else
            return "";
    }

    public static Function<Card, Card> updateKnowledge(Knowledge knowledge) {
        return card -> {
            Map<String, Boolean> typedKnowledgeMap = new HashMap<>();
            for (String key: Card.getKeys(knowledge.type())){
                if (key.equals(knowledge.value())) {
                    typedKnowledgeMap.put(key, card.getTrait(knowledge.type()).equals(knowledge.value()));
                }
                else {
                    Boolean oldValue = card.getKnowledgeMap(knowledge.type()).get(key);
                    if (oldValue != null) {
                        typedKnowledgeMap.put(key, oldValue);
                    }
                }
            }
            Map<Knowledge.KnowledgeType, Map<String, Boolean>> newKnowledgeMap = new HashMap<>();
            for(Knowledge.KnowledgeType key: Knowledge.KnowledgeType.values()){
                newKnowledgeMap.put(key, knowledge.type().equals(key) ? typedKnowledgeMap : card.getKnowledgeMap(key));
            }
            return new Card(
                card.color(),
                card.number(),
                newKnowledgeMap,
                null,
                card.numberOfTurnsInHand()
            );
        };
    }

    public static Function<Card, Card> updateMeta(Knowledge.Meta meta) {
        return card -> new Card(
            card.color(),
            card.number(),
            card.knowledgeMap,
            meta,
            card.numberOfTurnsInHand()
        );
    }

    public static Predicate<Card> cardIsKnown(Knowledge.KnowledgeType type) {
        return card -> card.knowledgeMap().get(type).containsValue(true);
    }

    public static Predicate<Card> cardIsKnown() {
        return card -> cardIsKnown(Knowledge.KnowledgeType.NUMBER).test(card) &&
                cardIsKnown(Knowledge.KnowledgeType.COLOR).test(card);
    }
    public static Predicate<Card> cardIsPlayable(TableState tableState){
        return card -> card.getNumber() == tableState.cardStacks().topOfCardStack().apply(card.color()) + 1;
    }

    public static Predicate<Card> cardIsDiscardable(TableState tableState){
        return card -> card.getNumber() <= tableState.cardStacks().topOfCardStack().apply(card.color());
    }

    public static Predicate<Card> cardIsNotSafeToDiscard(TableState tableState) {
        return card -> tableState.discardPile().deck().stream()
            .filter(discardedCard -> discardedCard.equals(card))
            .count() + 1 == getNumCards(card.number);
    }

    public static Predicate<Card> metaIsNotNull() {
        return card -> card.meta != null;
    }

    public static Predicate<Card> metaEquals(Knowledge.Meta meta) {
        return metaMightBe(meta).and(metaIsNotNull());
    }

    public static Predicate<Card> metaMightBe(Knowledge.Meta meta) {
        return card -> card.meta == null || card.meta.equals(meta);
    }
}


