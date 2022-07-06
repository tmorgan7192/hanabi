package strategies.hints;

import models.*;

import java.util.Optional;
import java.util.function.Function;
import java.util.function.Predicate;

import static models.Hand.anyNonActiveMatch;

public class HintFirstDiscardable extends HintStrategy {
    public HintFirstDiscardable(int minHints, Knowledge.KnowledgeType defaultKnowledgeType)
    {
        super(minHints, defaultKnowledgeType);
    }

    @Override
    public Predicate<TableState> shouldGiveHint() {
        return tableState -> anyNonActiveMatch(Card.cardIsDiscardable(tableState).and(Card.cardIsKnown().negate()))
            .test(tableState);
    }

    @Override
    public Function<TableState, Knowledge> getHintKnowledge() {
        return tableState -> {
            for (Hand hand: Hands.getNonActiveHands().apply(tableState)) {
                Optional<Card> firstDiscardableCard = hand.hand().stream()
                    .filter(Card.cardIsDiscardable(tableState))
                    .findFirst();
                if (firstDiscardableCard.isPresent()) {
                    return Knowledge.getUnknownKnowledge(defaultType, hand.index()).apply(firstDiscardableCard.get());
                }
            }
            return null;
        };
    }
}
