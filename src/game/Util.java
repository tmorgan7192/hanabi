package game;

import models.CardStacks;
import models.Knowledge;
import models.TableState;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.function.Predicate;

public class Util {
    @Contract(pure = true)
    public static @NotNull Predicate<TableState> gameOver() {
        return loseGameByExplosion().or(loseGameByOt()).or(winGame());
    }

    @Contract(pure = true)
    public static @NotNull Predicate<TableState> winGame() {
        return tableState -> CardStacks.getScore().apply(tableState).equals(25);
    }

    @Contract(pure = true)
    public static @NotNull Predicate<TableState> loseGameByExplosion() {
        return tableState -> tableState.tokenCount() == 0;
    }

    @Contract(pure = true)
    public static @NotNull Predicate<TableState> loseGameByOt() {
        return tableState -> tableState.otCount() == 0;
    }

    @Contract(pure = true)
    public static int getNumCards(int numPlayers) {
        return switch (numPlayers) {
            case 2, 3 -> 4;
            case 4, 5 -> 5;
            default -> throw new IllegalArgumentException("Number of players must be between 2 and 5");
        };
    }

    @Contract(pure = true)
    public static int getNumCards(Knowledge.CardNumber number) {
        return switch (number) {
            case ONE -> 3;
            case TWO, THREE, FOUR -> 2;
            case FIVE -> 1;
        };
    }

}
