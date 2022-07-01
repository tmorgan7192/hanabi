package strategies.randoms;

import models.Knowledge;
import models.TableState;
import strategies.Strategy;

import java.util.Random;

public class GiveRandomHint implements Strategy {
    private final int minHints;

    public GiveRandomHint(int minHints)
    {
        this.minHints = minHints;
    }

    public boolean isApplicable(TableState tableState) {
        return tableState.hintCount() > minHints;
    }

    public TableState runStrategy(TableState tableState) {
        Random random = new Random();
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
        throw new IllegalStateException("Random strategy switch did not return a value");
    }
}
