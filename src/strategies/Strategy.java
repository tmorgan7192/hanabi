package strategies;

import models.TableState;

public interface Strategy {
    boolean isApplicable(TableState tableState);

    TableState runStrategy(TableState tableState);
}

