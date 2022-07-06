package strategies.hints;

import models.*;

import java.util.Comparator;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Predicate;

import static models.Hand.anyNonActiveMatch;

public class HintLowestDiscardable extends HintStrategy {
    public HintLowestDiscardable(int minHints, Knowledge.KnowledgeType defaultType)
    {
        super(minHints, defaultType);
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
                    .min(Comparator.comparing(Card::getNumber));
                if (firstDiscardableCard.isPresent()) {
                    return Knowledge.getUnknownKnowledge(defaultType, hand.index()).apply(firstDiscardableCard.get());
                }
            }
            return null;
        };
    }
}
