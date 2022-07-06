package strategies.plays;

import models.Card;
import models.Knowledge;
import models.TableState;

import java.util.function.Predicate;

import static models.Hand.anyActiveMatch;
import static models.Hand.firstActiveMatch;

public class PlayFirstMeta extends PlayStrategy {
    public PlayFirstMeta()
    {
    }

    @Override
    public Predicate<TableState> shouldPlay() {
        return anyActiveMatch(Card.metaEquals(Knowledge.Meta.PLAY));
    }

    @Override
    public Integer getPlayCardIndex(TableState tableState) {
        return firstActiveMatch(Card.metaEquals(Knowledge.Meta.PLAY)).apply(tableState);
    }
}
