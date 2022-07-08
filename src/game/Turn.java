package game;

import models.*;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.function.Function;

import static models.TableState.*;

public class Turn {
    @Contract(pure=true)
    public static @NotNull Function<TableState, TableState> discardCard(int cardIndex) {
        return doDiscardCard(cardIndex)
            .andThen(drawCard())
            .andThen(updateHintCount(1))
            .andThen(endTurn(null));
    }

    @Contract(pure=true)
    public static @NotNull Function<TableState, TableState> playCard(int cardIndex) {
        return attemptPlayCard(cardIndex)
            .andThen(drawCard())
            .andThen(endTurn(null));
    }

    @Contract(pure=true)
    public static @NotNull Function<TableState, TableState> giveHint(Knowledge knowledge) {
        return updateHintCount(-1)
            .andThen(updateKnowledge(knowledge))
            .andThen(endTurn(knowledge));
    }
}
