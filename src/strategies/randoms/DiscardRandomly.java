package strategies.randoms;

import models.TableState;
import strategies.Strategy;

import java.util.Random;

import static game.Game.numPlayers;
import static game.Util.getNumCards;
import static models.TableState.MAX_HINT_COUNT;

public class DiscardRandomly implements Strategy {
    public DiscardRandomly()
    {
    }

    public boolean isApplicable(TableState tableState) {
        return tableState.hintCount() < MAX_HINT_COUNT;
    }

    public TableState runStrategy(TableState tableState) {
        Random random = new Random();
        return TableState.discardCard(random.nextInt(getNumCards(numPlayers))).apply(tableState);
    }
}
