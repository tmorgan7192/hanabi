package models;

import java.util.*;
import java.util.function.Function;
import java.util.function.Predicate;

public record Card(
    Knowledge.Color color,
    Knowledge.CardNumber number,
    Map<Knowledge.KnowledgeType, Map<String, Boolean>> knowledgeMap,
    Knowledge.Meta meta
) {
    public static Card createCard(Knowledge.Color color, Knowledge.CardNumber number) {
        return new Card(color, number, initializeKnowledgeMap(), null);
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

    public static Function<Card, Card> updateKnowledge(Knowledge knowledge, Knowledge.Meta meta) {
        return card -> {
            Map<String, Boolean> typedKnowledgeMap = new HashMap<>();
            for (String key: Card.getKeys(knowledge.type())){
                typedKnowledgeMap.put(key, key.equals(knowledge.value()));
            }
            Map<Knowledge.KnowledgeType, Map<String, Boolean>> newKnowledgeMap = new HashMap<>();
            for(Knowledge.KnowledgeType key: Knowledge.KnowledgeType.values()){
                newKnowledgeMap.put(key, knowledge.type().equals(key) ? typedKnowledgeMap : card.getKnowledgeMap(key));
            }
            return new Card(card.color(), card.number(), newKnowledgeMap, card.meta() == null ? meta : card.meta());
        };

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
}


