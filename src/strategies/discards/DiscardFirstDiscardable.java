package strategies.discards;

import models.Card;
import models.Hand;
import models.Hands;
import models.TableState;

import java.util.Optional;
import java.util.function.Predicate;

public class DiscardFirstDiscardable extends DiscardStrategy {
    public DiscardFirstDiscardable()
    {
        super();
    }

    @Override
    public Integer getDiscardCardIndex(TableState tableState) {
        Hand hand = Hands.getHand().apply(tableState);
        return hand.hand().stream()
                .filter(Card.cardIsKnown())
                .filter(Card.cardIsDiscardable(tableState))
                .map(Hand.getCardIndex(hand)).findAny().orElse(null);
    }
}
