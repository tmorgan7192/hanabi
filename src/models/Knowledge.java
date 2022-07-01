package models;

import java.util.function.Predicate;

public record Knowledge(KnowledgeType type, String value) {

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

    public enum Meta {
        PLAY,
        DISCARD
    }

    public enum KnowledgeType {
        COLOR,
        NUMBER,
        META
    }

    public static Predicate<Card> checkMeta(Meta meta, TableState tableState) {
        return card -> (meta.equals(Meta.PLAY) && Card.cardIsPlayable(tableState).test(card)) || (meta.equals(Meta.DISCARD) && Card.cardIsDiscardable(tableState).test(card));
    }
}

