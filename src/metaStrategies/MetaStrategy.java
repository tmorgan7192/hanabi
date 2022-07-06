package metaStrategies;

import models.Hand;
import models.Knowledge;
import models.TableState;
import strategies.Strategy;

import java.util.function.Predicate;

public abstract class MetaStrategy {
    public final Knowledge.Meta meta;
    public MetaStrategy(Knowledge.Meta meta) {
        this.meta = meta;
    }

    public abstract Predicate<Hand> isApplicable(Knowledge knowledge);
}

