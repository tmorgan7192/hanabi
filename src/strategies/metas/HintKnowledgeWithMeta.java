package strategies.metas;

import models.*;
import strategies.Strategy;

import java.util.List;

public class HintKnowledgeWithMeta implements Strategy<TableState> {
    private final int minHints;
    private final Knowledge.Meta meta;
    private final int knowledgeCount;

    public HintKnowledgeWithMeta(int minHints, int knowledgeCount, Knowledge.Meta meta)
    {
        this.minHints = minHints;
        this.meta = meta;
        this.knowledgeCount = knowledgeCount;
    }

    public boolean isApplicable(TableState tableState) {
        if (tableState.hintCount() < minHints) {
            return false;
        }
        Hand hand = Hands.getHand().apply(tableState);
        List<Card> cards = Hand.typeAppearsGivenNumberOfTimes()
            .andThen(Hand.getCardsMatchingKnowledge(hand))
            .apply(knowledgeCount, hand);
        return Knowledge.metaHoldsForAll(meta, tableState).test(cards);
    }

    public TableState runStrategy(TableState tableState) {
        Knowledge knowledge = Hand.typeAppearsGivenNumberOfTimes().apply(
            knowledgeCount, Hands.getHand().apply(tableState)
        );
        return TableState.giveHint(knowledge).apply(tableState);
    }
}
