package strategies.hints;

import models.*;
import strategies.Strategy;

public class HintKnowledgeWithCount implements Strategy {
    private final int minHints;
    private final int knowledgeCount;

    public HintKnowledgeWithCount(int minHints, int knowledgeCount)
    {
        this.minHints = minHints;
        this.knowledgeCount = knowledgeCount;
    }

    public boolean isApplicable(TableState tableState) {
        return tableState.hintCount() > minHints &&
            Hand.knowledgeAppearsGivenNumberOfTimes().apply(
                knowledgeCount, Hands.getHand().apply(tableState)
            ) != null;
    }

    public TableState runStrategy(TableState tableState) {
        Knowledge knowledge = Hand.knowledgeAppearsGivenNumberOfTimes().apply(
            knowledgeCount, Hands.getHand().apply(tableState)
        );
        return TableState.giveHint(knowledge).apply(tableState);
    }
}
