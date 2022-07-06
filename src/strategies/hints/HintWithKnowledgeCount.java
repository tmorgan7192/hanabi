package strategies.hints;

import models.*;

import java.util.function.Function;
import java.util.function.Predicate;

public class HintWithKnowledgeCount extends HintStrategy {
    private final int knowledgeCount;

    public HintWithKnowledgeCount(int minHints, int knowledgeCount, Knowledge.KnowledgeType defaultType)
    {
        super(minHints, defaultType);
        this.knowledgeCount = knowledgeCount;
    }

    @Override
    public Predicate<TableState> shouldGiveHint() {
        return tableState -> {
            for (Hand hand : Hands.getNonActiveHands().apply(tableState)) {
                if (Hand.knowledgeAppearsGivenNumberOfTimes(defaultType).apply(knowledgeCount, hand) != null) {
                    return true;
                }
            }
            return false;
        };
    }

    @Override
    public Function<TableState, Knowledge> getHintKnowledge() {
        return tableState -> {
            for (Hand hand : Hands.getNonActiveHands().apply(tableState)) {
                Knowledge knowledge = Hand.knowledgeAppearsGivenNumberOfTimes(defaultType).apply(knowledgeCount, hand);
                if (knowledge != null)
                    return knowledge;
                }
            throw new IllegalStateException("Card with knowledge count should be present");
            };
        }
}
