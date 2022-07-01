package strategies;

import models.TableState;

public interface Strategy<T> {
    boolean isApplicable(TableState tableState);

    T runStrategy(TableState tableState);
}

