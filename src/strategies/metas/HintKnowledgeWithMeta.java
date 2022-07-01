package strategies.metas;

import models.*;
import strategies.Strategy;

public class HintKnowledgeWithMeta implements Strategy {
    private final int minHints;
    private final String meta;
    private final int knowledgeCount;

    public HintKnowledgeWithMeta(int minHints, int knowledgeCount, String meta)
    {
        this.minHints = minHints;
        this.meta = meta;
        this.knowledgeCount = knowledgeCount;
    }

    public boolean isApplicable(TableState tableState) {
        Knowledge knowledge = Hand.knowledgeAppearsGivenNumberOfTimes().apply(
                knowledgeCount, Hands.getHand().apply(tableState)
        );
        return tableState.hintCount() > minHints;
    }

    public TableState runStrategy(TableState tableState) {
        Knowledge knowledge = Hand.knowledgeAppearsGivenNumberOfTimes().apply(
            knowledgeCount, Hands.getHand().apply(tableState)
        );
        return TableState.giveHint(knowledge).apply(tableState);
    }
}
