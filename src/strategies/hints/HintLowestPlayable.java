package strategies.hints;

import models.*;

import java.util.Comparator;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Predicate;

import static models.Hand.anyActiveMatch;
import static models.Hand.anyNonActiveMatch;

public class HintLowestPlayable extends HintStrategy {
    public HintLowestPlayable(int minHints, Knowledge.KnowledgeType defaultType)
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
                Optional<Card> firstPlayableCard = hand.hand().stream()
                    .filter(Card.cardIsPlayable(tableState))
                    .min(Comparator.comparing(Card::getNumber));
                if (firstPlayableCard.isPresent()) {
                    return Knowledge.getUnknownKnowledge(defaultType, hand.index()).apply(firstPlayableCard.get());
                }
            }
            return null;
        };
    }
}
