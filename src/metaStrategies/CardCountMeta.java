package metaStrategies;

import models.*;

import java.util.function.Function;
import java.util.function.Predicate;

import static metaStrategies.MetaUtil.applyMetaToCardsThatMatchKnowledge;
import static models.Hand.getCardsMatchingKnowledge;
import static models.Hands.getHand;

public class CardCountMeta extends MetaStrategy {
    private final int knowledgeCount;

    public CardCountMeta(int knowledgeCount, Knowledge.Meta meta)
    {
        super(meta);
        this.knowledgeCount = knowledgeCount;
    }

    @Override
    public Predicate<TableState> isApplicable(Knowledge knowledge) {
        return tableState -> getCardsMatchingKnowledge(getHand(knowledge.playerIndex()).apply(tableState))
            .apply(knowledge).size() == knowledgeCount;
    }

    @Override
    public Function<TableState, TableState> applyMeta(Knowledge knowledge) {
        return applyMetaToCardsThatMatchKnowledge(knowledge, meta);
    }
}
