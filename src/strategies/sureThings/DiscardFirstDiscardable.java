package strategies.sureThings;

import models.Card;
import models.Hand;
import models.Hands;
import models.TableState;
import strategies.Strategy;

import java.util.Optional;

public class DiscardFirstDiscardable implements Strategy {
    public DiscardFirstDiscardable()
    {
    }

    public boolean isApplicable(TableState tableState) {
        return discardableCardIndex(tableState).isPresent();
    }

    public TableState runStrategy(TableState tableState) {
        Optional<Integer> discardableCardIndex = discardableCardIndex(tableState);
        if (discardableCardIndex.isPresent()) {
            return TableState.playCardOnStack(discardableCardIndex.get()).apply(tableState);
        }
        throw new IllegalStateException("Discardable card should be present");
    }

    private static Optional<Integer> discardableCardIndex(TableState tableState) {
        Hand hand = Hands.getHand().apply(tableState);
        return hand.hand().stream()
                .filter(Card.cardIsKnown())
                .filter(Card.cardIsDiscardable(tableState))
                .map(Hand.getCardIndex(hand)).findAny();
    }
}
