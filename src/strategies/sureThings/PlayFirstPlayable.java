package strategies.sureThings;

import models.Card;
import models.Hand;
import models.Hands;
import models.TableState;
import strategies.Strategy;

import java.util.Optional;

public class PlayFirstPlayable implements Strategy {
    public PlayFirstPlayable()
    {
    }

    public boolean isApplicable(TableState tableState) {
        return playableCardIndex(tableState).isPresent();
    }

    public TableState runStrategy(TableState tableState) {
        Optional<Integer> playableCardIndex = playableCardIndex(tableState);
        if (playableCardIndex.isPresent()) {
            return TableState.playCardOnStack(playableCardIndex.get()).apply(tableState);
        }
        throw new IllegalStateException("Playable card should be present");
    }

    private static Optional<Integer> playableCardIndex(TableState tableState) {
        Hand hand = Hands.getHand().apply(tableState);
        return hand.hand().stream()
                .filter(Card.cardIsKnown())
                .filter(Card.cardIsPlayable(tableState))
                .map(Hand.getCardIndex(hand)).findAny();
    }
}
