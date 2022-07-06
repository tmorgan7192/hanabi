package models;

import game.Game;
import metaStrategies.MetaStrategy;

import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;

public record Knowledge(KnowledgeType type, String value, int playerIndex) {

    public enum CardNumber{
        ONE,
        TWO,
        THREE,
        FOUR,
        FIVE
    }

    public enum Color {
        GREEN,
        RED,
        WHITE,
        YELLOW,
        BLUE
    }

    public static Function<String,String> toString(KnowledgeType type){
        return key -> type == KnowledgeType.COLOR
            ? key.substring(0, 1)
            : Integer.toString(CardNumber.valueOf(key).ordinal() + 1);
    }

    public enum KnowledgeType {
        COLOR,
        NUMBER
    }

    public enum Meta {
        PLAY,
        DISCARD
    }

    public static String metaToString(Meta meta) {
        if (meta == null)
            return "";
        return switch (meta) {
            case PLAY -> " P";
            case DISCARD -> " D";
        };
    }

    public static KnowledgeType getOtherType(KnowledgeType type) {
        return type.equals(KnowledgeType.COLOR) ? KnowledgeType.NUMBER : KnowledgeType.COLOR;
    }

    public static Function<Hand, Meta> checkMetaImplications(Knowledge knowledge){
        return hand -> {
            for (MetaStrategy strategy: Game.metaStrategies) {
                if (strategy.isApplicable(knowledge).test(hand)) {
                    return strategy.meta;
                }
            }
            return null;
        };
    }

    public static Predicate<Card> metaHolds(Meta meta, TableState tableState) {
        return card -> (meta == Meta.PLAY && Card.cardIsPlayable(tableState).test(card))
            || (meta == Meta.DISCARD && Card.cardIsDiscardable(tableState).test(card));
    }

    public static Predicate<List<Card>> metaHoldsForAll(Meta meta, TableState tableState) {
        return cards -> cards != null && cards.stream().allMatch(metaHolds(meta, tableState));
    }

    public static Predicate<List<Card>> metaHoldsForAny(Meta meta, TableState tableState) {
        return cards -> cards != null && cards.stream().anyMatch(metaHolds(meta, tableState));
    }

    public static Function<Card, Knowledge> getUnknownKnowledge(KnowledgeType defaultType ,int playerIndex) {
        return card -> Card.cardIsKnown(defaultType).test(card) ?
                new Knowledge(Knowledge.KnowledgeType.COLOR, card.getTrait(getOtherType(defaultType)), playerIndex) :
                new Knowledge(defaultType, card.getTrait(defaultType), playerIndex);
    }

}

