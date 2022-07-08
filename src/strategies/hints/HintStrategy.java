package strategies.hints;

import models.*;
import strategies.Strategy;

import java.util.function.Function;
import java.util.function.Predicate;

import static game.Game.printLogs;
import static game.Turn.giveHint;

public abstract class HintStrategy implements Strategy {
    protected final int minHints;
    protected final Knowledge.KnowledgeType defaultType;

    protected HintStrategy(int minHints, Knowledge.KnowledgeType defaultType) {
        this.minHints = minHints;
        this.defaultType = defaultType;
    }

    public final Predicate<TableState> isApplicable() {
        return checkHints().and(isNewHint()).and(shouldGiveHint());
    }

    private Predicate<TableState> isNewHint() {
        return tableState -> {
            Knowledge knowledge = getHintKnowledge().apply(tableState);
            if (knowledge == null) {
                return false;
            }
            Hand hand = Hands.getHandByIndex(knowledge.playerIndex()).apply(tableState);
            return !Hand.knowledgeIsKnown(knowledge).test(hand);
        };
    }

    private Predicate<TableState> checkHints() {
        return tableState -> tableState.hintCount() > minHints;
    }

    public final Function<TableState, TableState> runStrategy() {
        return tableState -> {
            Knowledge knowledge = getHintKnowledge().apply(tableState);
            if (printLogs) {
                System.out.println("Giving hint " + knowledge.value() + " to player with index " + knowledge.playerIndex());
            }
            return giveHint(knowledge).apply(tableState);
        };
    }

    public abstract Predicate<TableState> shouldGiveHint();

    public abstract Function<TableState, Knowledge> getHintKnowledge();
}

