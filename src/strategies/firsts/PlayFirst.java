package strategies.firsts;

import models.TableState;
import strategies.Strategy;

import java.util.Random;

import static game.Game.numPlayers;
import static game.Util.getNumCards;

public class PlayFirst implements Strategy<TableState> {
    public PlayFirst()
    {
    }

    public boolean isApplicable(TableState tableState) {
        return true;
    }

    public TableState runStrategy(TableState tableState) {
        return TableState.playCardOnStack(0).apply(tableState);
    }
}
