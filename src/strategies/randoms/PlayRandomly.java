package strategies.randoms;

import models.TableState;
import strategies.Strategy;

import java.util.Random;

import static game.Game.numPlayers;
import static game.Util.getNumCards;

public class PlayRandomly implements Strategy {
    public PlayRandomly()
    {
    }

    public boolean isApplicable(TableState tableState) {
        return true;
    }

    public TableState runStrategy(TableState tableState) {
        Random random = new Random();
        return TableState.playCardOnStack(random.nextInt(getNumCards(numPlayers))).apply(tableState);
    }
}
