package strategies.hints;

import models.*;

import java.util.Optional;
import java.util.function.Function;
import java.util.function.Predicate;

import static models.Hand.anyNonActiveMatch;

public class HintUnsafeToDiscard extends HintStrategy {
    public HintUnsafeToDiscard(int minHints, Knowledge.KnowledgeType defaultType)
    {
        super(minHints, defaultType);
    }

    @Override
    public Predicate<TableState> shouldGiveHint() {
        return tableState -> anyNonActiveMatch(Card.cardIsNotSafeToDiscard(tableState))
            .test(tableState);
    }

    @Override
    public Function<TableState, Knowledge> getHintKnowledge() {
        return tableState -> {
            for (Hand hand: Hands.getNonActiveHands().apply(tableState)) {
                Optional<Card> firstNonDiscardableCard = hand.hand().stream()
                    .filter(Card.cardIsNotSafeToDiscard(tableState))
                    .findFirst();
                if (firstNonDiscardableCard.isPresent()) {
                    return Knowledge.getUnknownKnowledge(defaultType, hand.index()).apply(firstNonDiscardableCard.get());
                }
            }
            return null;
        };
    }
}
