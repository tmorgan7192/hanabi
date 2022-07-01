package game;

import models.CardStacks;
import models.TableState;

import java.util.function.Predicate;


public class Util {

    public static Predicate<TableState> gameOver() {
        return tableState -> loseGameByExplosion().or(loseGameByOt()).or(winGame()).test(tableState);
    }

    public static Predicate<TableState> winGame() {
        return tableState -> CardStacks.getScore().apply(tableState).equals(25);
    }

    public static Predicate<TableState> loseGameByExplosion() {
        return tableState -> tableState.tokenCount() == 0;
    }

    public static Predicate<TableState> loseGameByOt() {
        return tableState -> tableState.otCount() == 0;
    }

    public static int getNumCards(int numPlayers) {
        return switch (numPlayers) {
            case 2, 3 -> 4;
            case 4, 5 -> 5;
            default -> throw new IllegalArgumentException("Number of players must be between 2 and 5");
        };
    }

}
