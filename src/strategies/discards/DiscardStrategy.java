package strategies.discards;

import game.Turn;
import models.Card;
import models.Hand;
import models.Knowledge;
import models.TableState;
import strategies.Strategy;

import java.util.function.Function;
import java.util.function.Predicate;

import static game.Game.printLogs;
import static game.Turn.discardCard;
import static models.TableState.MAX_HINT_COUNT;

public abstract class DiscardStrategy implements Strategy {

    protected DiscardStrategy() {}

    public final Predicate<TableState> isApplicable() {
         return discardCardIsPresent().and(checkHints()).and(checkMeta()).and(shouldDiscard());
    }

    private Predicate<TableState> checkHints() {
        return tableState -> tableState.hintCount() < MAX_HINT_COUNT;
    }

    private Predicate<TableState> checkMeta() {
        return tableState -> Card.metaMightBe(Knowledge.Meta.DISCARD).test(
            Hand.getCardFromActivePlayer(getDiscardCardIndex(tableState)).apply(tableState)
        );
    }

    public final Function<TableState, TableState> runStrategy() {
        return tableState -> {
            int index = getDiscardCardIndex(tableState);
            if (printLogs) {
                System.out.println("Discarding card with index " + index);
            }
            return discardCard(index).apply(tableState);
        };
    }

    public Predicate<TableState> shouldDiscard() {
        return tableState -> true;
    }

    public Predicate<TableState> discardCardIsPresent() {
        return tableState -> getDiscardCardIndex(tableState) != null;
    }

    public abstract Integer getDiscardCardIndex(TableState tableState);
}

