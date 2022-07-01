package strategies.randoms;

import models.Knowledge;
import models.TableState;
import strategies.Strategy;

import java.util.Random;

import static game.Game.numPlayers;
import static game.Util.getNumCards;

public class RandomStrategy implements Strategy<TableState> {
    public RandomStrategy()
    {
    }

    public boolean isApplicable(TableState tableState) {
        return true;
    }

    public TableState runStrategy(TableState tableState) {
        int allowedMoves = tableState.hintCount() == 0 ? 2: 3;
        Random random = new Random();
        switch (random.nextInt(allowedMoves)) {
            case 0 -> {
                return TableState.playCardOnStack(random.nextInt(getNumCards(numPlayers))).apply(tableState);
            }
            case 1 -> {
                return TableState.discardCard(random.nextInt(getNumCards(numPlayers))).apply(tableState);
            }
            case 2 -> {
                switch (random.nextInt(2)) {
                    case 0 -> {
                        return TableState.giveHint(
                           new Knowledge(
                               Knowledge.KnowledgeType.NUMBER,
                               Knowledge.CardNumber.values()[random.nextInt(5)].toString()
                           )
                        ).apply(tableState);
                    }
                    case 1 -> {
                        return TableState.giveHint(
                            new Knowledge(
                                Knowledge.KnowledgeType.COLOR,
                                Knowledge.Color.values()[random.nextInt(5)].toString()
                            )
                        ).apply(tableState);
                    }
                }
            }
        }
        throw new IllegalStateException("Random strategy switch did not return a value");
    }
}
