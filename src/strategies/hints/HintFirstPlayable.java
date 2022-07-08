package strategies.hints;

import models.*;

import java.util.Optional;
import java.util.function.Function;
import java.util.function.Predicate;

import static models.Hand.anyNonActiveMatch;

public class HintFirstPlayable extends HintStrategy {
    public HintFirstPlayable(int minHints, Knowledge.KnowledgeType defaultType)
    {
        super(minHints, defaultType);
    }

    @Override
    public Predicate<TableState> shouldGiveHint() {
        return tableState -> anyNonActiveMatch(
                Card.cardIsPlayable(tableState).and(Card.cardIsKnown().negate())
            ).test(tableState);
    }

    @Override
    public Function<TableState, Knowledge> getHintKnowledge() {
        return tableState -> {
            for (Hand hand: Hands.getNonActiveHands().apply(tableState)) {
                Optional<Card> firstPlayableCard = hand.stream()
                    .filter(Card.cardIsPlayable(tableState))
                    .findFirst();
                if (firstPlayableCard.isPresent()) {
                    return Knowledge.getUnknownKnowledge(defaultType, hand.index()).apply(firstPlayableCard.get());
                }
            }
            return null;
        };
    }
}
