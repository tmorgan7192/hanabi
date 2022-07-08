package metaStrategies;

import models.*;

import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;

import static metaStrategies.MetaUtil.applyMetaToActivePlayer;
import static models.Hand.getCardsNotMatchingKnowledge;
import static models.Hands.getHand;

public class SafeToDiscardMeta extends MetaStrategy {
    private final int minNumTurnsInHand;

    public SafeToDiscardMeta(int minNumTurnsInHand)
    {
        super(Knowledge.Meta.DISCARD);
        this.minNumTurnsInHand = minNumTurnsInHand;
    }

    @Override
    public Predicate<TableState> isApplicable(Knowledge knowledge) {
        return tableState -> {
            List<Card> cards = getCardsNotMatchingKnowledge(
                getHand(knowledge.playerIndex()).apply(tableState)
            ).apply(knowledge);
            if (cards == null){
                return false;
            }
            return cards.stream().anyMatch(card -> card.numberOfTurnsInHand() >= minNumTurnsInHand);
        };
    }

    @Override
    public Function<TableState, TableState> applyMeta(Knowledge knowledge) {
        return applyMetaToActivePlayer(meta, card -> card.number().equals(Knowledge.CardNumber.FIVE));
    }
}
