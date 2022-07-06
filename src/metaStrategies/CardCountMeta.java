package metaStrategies;

import models.*;

import java.util.function.Predicate;

public class CardCountMeta extends MetaStrategy {
    private final int knowledgeCount;

    public CardCountMeta(int knowledgeCount, Knowledge.Meta meta)
    {
        super(meta);
        this.knowledgeCount = knowledgeCount;
    }

    @Override
    public Predicate<Hand> isApplicable(Knowledge knowledge) {
        return hand -> Hand.getCardsMatchingKnowledge(hand).apply(knowledge).size() == knowledgeCount;
    }
}
