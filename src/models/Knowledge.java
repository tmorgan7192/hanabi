package models;

import game.Game;
import strategies.Strategy;

import java.util.List;
import java.util.function.Function;
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

    public enum KnowledgeType {
        COLOR,
        NUMBER
    }

    public enum Meta {
        PLAY,
        DISCARD
    }

    public static Function<TableState, Meta> checkMetaImplications(){
        return tableState -> {
            for (Strategy<Meta> strategy: Game.metaStrategies) {
                if (strategy.isApplicable(tableState)) {
                    return strategy.runStrategy(tableState);
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
        return cards -> cards.stream().allMatch(metaHolds(meta, tableState));
    }

    public static Predicate<List<Card>> metaHoldsForAny(Meta meta, TableState tableState) {
        return cards -> cards.stream().anyMatch(metaHolds(meta, tableState));
    }

}

