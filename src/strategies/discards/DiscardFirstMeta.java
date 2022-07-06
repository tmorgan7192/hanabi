package strategies.discards;

import models.Card;
import models.Knowledge;
import models.TableState;

import java.util.function.Predicate;

import static models.Hand.anyActiveMatch;
import static models.Hand.firstActiveMatch;

public class DiscardFirstMeta extends DiscardStrategy {
    public DiscardFirstMeta()
    {
    }

    @Override
    public Predicate<TableState> shouldDiscard() {
        return anyActiveMatch(Card.metaEquals(Knowledge.Meta.DISCARD));
    }

    @Override
    public Integer getDiscardCardIndex(TableState tableState) {
        return firstActiveMatch(Card.metaEquals(Knowledge.Meta.DISCARD)).apply(tableState);
    }
}
