package metaStrategies;

import models.*;
import strategies.Strategy;

import java.util.List;

public class CardCountHintsMeta implements Strategy<Knowledge.Meta> {
    private final Knowledge.Meta meta;
    private final int knowledgeCount;

    public CardCountHintsMeta(int knowledgeCount, Knowledge.Meta meta)
    {
        this.meta = meta;
        this.knowledgeCount = knowledgeCount;
    }

    public boolean isApplicable(TableState tableState) {
        Hand hand = Hands.getHand().apply(tableState);
        List<Card> cards = Hand.typeAppearsGivenNumberOfTimes()
                .andThen(Hand.getCardsMatchingKnowledge(hand))
                .apply(knowledgeCount, hand);
        return cards != null;
    }

    public Knowledge.Meta runStrategy(TableState tableState) {
        return meta;
    }
}
