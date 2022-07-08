package metaStrategies;

import models.Knowledge;
import models.TableState;

import java.util.function.Function;
import java.util.function.Predicate;

import static metaStrategies.MetaUtil.applyMetaToCardsThatMatchKnowledge;


public class SaveFivesMeta extends MetaStrategy {
    public SaveFivesMeta()
    {
        super(Knowledge.Meta.SAVE);
    }

    @Override
    public Predicate<TableState> isApplicable(Knowledge knowledge) {
        return tableState -> knowledge.value().equals(Knowledge.CardNumber.FIVE.toString());
    }

    @Override
    public Function<TableState, TableState> applyMeta(Knowledge knowledge) {
        return applyMetaToCardsThatMatchKnowledge(knowledge, meta);
    }
}
