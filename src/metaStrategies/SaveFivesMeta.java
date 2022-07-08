package metaStrategies;

import models.Hand;
import models.Knowledge;

import java.util.function.Predicate;


public class SaveFivesMeta extends MetaStrategy {
    public SaveFivesMeta()
    {
        super(Knowledge.Meta.SAVE);
    }

    @Override
    public Predicate<Hand> isApplicable(Knowledge knowledge) {
        return hand -> knowledge.value().equals(Knowledge.CardNumber.FIVE.toString());
    }
}
