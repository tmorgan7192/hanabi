package metaStrategies;

import models.Hand;
import models.Knowledge;

import java.util.function.Predicate;

public class SafeToDiscardMeta extends MetaStrategy {
    private final int knowledgeCount;

    public SafeToDiscardMeta(int knowledgeCount, Knowledge.Meta meta)
    {
        super(meta);
        this.knowledgeCount = knowledgeCount;
    }

    @Override
    public Predicate<Hand> isApplicable(Knowledge knowledge) {
        return hand -> Hand.getCardsMatchingKnowledge(hand).apply(knowledge).size() == knowledgeCount;
    }
}
