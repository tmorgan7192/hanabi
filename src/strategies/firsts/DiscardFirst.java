package strategies.firsts;

import models.TableState;
import strategies.Strategy;

import static models.TableState.MAX_HINT_COUNT;

public class DiscardFirst implements Strategy<TableState> {
    public DiscardFirst()
    {
    }

    public boolean isApplicable(TableState tableState) {
        return tableState.hintCount() < MAX_HINT_COUNT;
    }

    public TableState runStrategy(TableState tableState) {
        return TableState.discardCard(0).apply(tableState);
    }
}
