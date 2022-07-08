package metaStrategies;

import models.Card;
import models.Hand;
import models.Knowledge;

import java.util.List;
import java.util.function.Predicate;

import static models.Hand.getCardsNotMatchingKnowledge;

public class SafeToDiscardMeta extends MetaStrategy {
    private final int minNumTurnsInHand;

    public SafeToDiscardMeta(int minNumTurnsInHand)
    {
        super(Knowledge.Meta.DISCARD);
        this.minNumTurnsInHand = minNumTurnsInHand;
    }

    @Override
    public Predicate<Hand> isApplicable(Knowledge knowledge) {
        return hand -> {
            List<Card> cards = getCardsNotMatchingKnowledge(hand).apply(knowledge);
            if (cards == null){
                return false;
            }
            return cards.stream().anyMatch(card -> card.numberOfTurnsInHand() >= minNumTurnsInHand);
        };
    }
}
