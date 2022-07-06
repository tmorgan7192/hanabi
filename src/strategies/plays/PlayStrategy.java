package strategies.plays;

import models.*;
import strategies.Strategy;

import java.util.Optional;
import java.util.function.Function;
import java.util.function.Predicate;

import static game.Game.printLogs;
import static models.TableState.MAX_HINT_COUNT;

public abstract class PlayStrategy implements Strategy {

    protected PlayStrategy() {}

    public final Predicate<TableState> isApplicable() {
         return playCardIsPresent().and(checkMeta()).and(shouldPlay());
    }

    public final Function<TableState, TableState> runStrategy() {
        return tableState -> {
            int index = getPlayCardIndex(tableState);
            if (printLogs) {
                System.out.println("Playing card with index " + index);
            }
            return TableState.playCard(index).apply(tableState);
        };
    }

    private Predicate<TableState> checkMeta() {
        return tableState -> Card.metaMightBe(Knowledge.Meta.PLAY).test(
            Hand.getCardFromActivePlayer().apply(getPlayCardIndex(tableState), tableState)
        );
    }

    public Predicate<TableState> shouldPlay() {
        return tableState -> true;
    }

    public Predicate<TableState> playCardIsPresent() {
        return tableState -> getPlayCardIndex(tableState) != null;
    }

    public abstract Integer getPlayCardIndex(TableState tableState);
}

