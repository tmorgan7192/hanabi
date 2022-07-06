package strategies.plays;

import models.Card;
import models.Hand;
import models.Hands;
import models.TableState;
import strategies.Strategy;

import java.util.Optional;
import java.util.function.Function;
import java.util.function.Predicate;

public class PlayFirstPlayable extends PlayStrategy {
    public PlayFirstPlayable()
    {
    }

    @Override
    public Integer getPlayCardIndex(TableState tableState) {
        Hand hand = Hands.getHand().apply(tableState);
        return hand.hand().stream()
                .filter(Card.cardIsKnown())
                .filter(Card.cardIsPlayable(tableState))
                .map(Hand.getCardIndex(hand)).findAny().orElse(null);
    }
}
