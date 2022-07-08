package strategies.discards;

import models.Card;
import models.Hand;
import models.Hands;
import models.TableState;

public class DiscardFirstDiscardable extends DiscardStrategy {
    public DiscardFirstDiscardable()
    {
        super();
    }

    @Override
    public Integer getDiscardCardIndex(TableState tableState) {
        Hand hand = Hands.getActivePlayerHand().apply(tableState);
        return hand.hand().stream()
                .filter(Card.cardIsKnown())
                .filter(Card.cardIsDiscardable(tableState))
                .map(Hand.getCardIndex(hand)).findAny().orElse(null);
    }
}
