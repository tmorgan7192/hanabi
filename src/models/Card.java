package models;

import java.util.*;
import java.util.function.Function;
import java.util.function.Predicate;

public record Card(
    Knowledge.Color color,
    Knowledge.CardNumber number,
    Map<Knowledge.KnowledgeType, Map<String, Boolean>> knowledgeMap
) {
    public static Card createCard(Knowledge.Color color, Knowledge.CardNumber number) {
        return new Card(color, number, initializeKnowledgeMap());
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
        knowledgeMap.put(Knowledge.KnowledgeType.META, metaMap);
        return knowledgeMap;
    }

    public static List<String> getKeys(Knowledge.KnowledgeType type) {
        return switch (type) {
            case COLOR -> Arrays.stream(Knowledge.Color.values()).map(Enum::toString).toList();
            case NUMBER -> Arrays.stream(Knowledge.CardNumber.values()).map(Enum::toString).toList();
            case META -> Arrays.stream(Knowledge.Meta.values()).map(Enum::toString).toList();
        };
    }

    public int getNumber() {
        return this.number.ordinal() + 1;
    }

    public Map<String, Boolean> getKnowledgeMap(Knowledge.KnowledgeType type) {
        return this.knowledgeMap.get(type);
    }

    public static Function<Card, Card> updateKnowledgeMap(
            Knowledge.KnowledgeType type, Map<String, Boolean> knowledgeMap
    ) {
        return card -> {
            Map<Knowledge.KnowledgeType, Map<String, Boolean>> newKnowledgeMap = new HashMap<>();
            for(Knowledge.KnowledgeType key: Knowledge.KnowledgeType.values()){
                newKnowledgeMap.put(key, type.equals(key) ? knowledgeMap : card.getKnowledgeMap(key));
            }
            return new Card(card.color(), card.number(), newKnowledgeMap);
        };
    }

    public static Function<Card, Card> updateKnowledge(Knowledge knowledge) {
        return card -> {
            Map<String, Boolean> newKnowledgeMap = new HashMap<>();
            for (String key: Card.getKeys(knowledge.type())){
                newKnowledgeMap.put(key, key.equals(knowledge.value()));
            }
            return Card.updateKnowledgeMap(knowledge.type(), newKnowledgeMap).apply(card);
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


