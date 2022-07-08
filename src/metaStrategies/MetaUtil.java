package metaStrategies;

import models.Card;
import models.Hand;
import models.Knowledge;
import models.TableState;

import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static models.Card.matchesKnowledge;
import static models.Card.updateMeta;
import static models.Hands.getHand;
import static models.TableState.updateHand;

public class MetaUtil {
    public static Function<TableState, TableState> applyMetaToCardsThatMatchKnowledge(
        Knowledge knowledge, Knowledge.Meta meta
    ) {
        return applyMetaToInactivePlayer(knowledge, meta, matchesKnowledge(knowledge));
    }

    public static Function<TableState, TableState> applyMetaToCardsMeetingCondition(
        Knowledge.Meta meta, int playerIndex, Predicate<Card> condition
    ) {
        return tableState -> {
            Hand oldHand = getHand(playerIndex).apply(tableState);
            return updateHand(
                    new Hand(
                            oldHand.stream().map(
                                    card -> condition.test(card) ? updateMeta(meta).apply(card) : card
                            ).collect(Collectors.toList()),
                            oldHand.index()
                    ),
                    oldHand.index()
            ).apply(tableState);
        };
    }

    public static Function<TableState, TableState> applyMetaToActivePlayer(
        Knowledge.Meta meta, Predicate<Card> condition
    ) {
        return tableState -> applyMetaToCardsMeetingCondition(
            meta, tableState.activePlayerIndex(), condition
        ).apply(tableState);
    }

    public static Function<TableState, TableState> applyMetaToInactivePlayer(
            Knowledge knowledge, Knowledge.Meta meta, Predicate<Card> condition
    ) {
        return applyMetaToCardsMeetingCondition(meta, knowledge.playerIndex(), condition);
    }
}
