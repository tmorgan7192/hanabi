package strategies;

import models.TableState;

import java.util.function.Function;
import java.util.function.Predicate;

public interface Strategy {
    Predicate<TableState> isApplicable();

    Function<TableState, TableState> runStrategy();
}

