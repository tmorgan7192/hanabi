package strategies.hints;

import models.*;

import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;

public class HintKnowledgeWithMeta extends HintStrategy {
    private final Knowledge.Meta meta;
    private final int knowledgeCount;

    public HintKnowledgeWithMeta(int minHints, int knowledgeCount, Knowledge.KnowledgeType defaultType, Knowledge.Meta meta)
    {
        super(minHints, defaultType);
        this.meta = meta;
        this.knowledgeCount = knowledgeCount;
    }

    @Override
    public Predicate<TableState> shouldGiveHint() {
        return tableState -> {
            List<Hand> hands = Hands.getNonActiveHands().apply(tableState);
            for (Hand hand : hands) {
                List<Card> cards = Hand.knowledgeAppearsGivenNumberOfTimes(defaultType)
                    .andThen(Hand.getCardsMatchingKnowledge(hand))
                    .apply(knowledgeCount, hand);
                if (cards != null
                    && Knowledge.metaHoldsForAll(meta, tableState).test(cards)
                ) {
                    return true;
                }
            }
            return false;
        };
    }

    @Override
    public Function<TableState, Knowledge> getHintKnowledge() {
        return tableState -> {
            List<Hand> hands = Hands.getNonActiveHands().apply(tableState);
            for (Hand hand : hands) {
                Knowledge knowledge = Hand.knowledgeAppearsGivenNumberOfTimes(defaultType).apply(knowledgeCount, hand);
                if (
                    Knowledge.metaHoldsForAll(meta, tableState)
                    .test(Hand.getCardsMatchingKnowledge(hand).apply(knowledge))
                )
                    return knowledge;
            }
            return null;
        };
    }
}
