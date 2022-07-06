package game;

import metaStrategies.CardCountMeta;
import metaStrategies.MetaStrategy;
import models.CardStacks;
import models.Deck;
import models.Knowledge;
import models.TableState;
import strategies.Strategy;
import strategies.discards.DiscardFirst;
import strategies.discards.DiscardFirstDiscardable;
import strategies.discards.DiscardFirstMeta;
import strategies.hints.HintFirstPlayable;
import strategies.hints.HintKnowledgeWithMeta;
import strategies.hints.HintLowestPlayable;
import strategies.hints.HintUnsafeToDiscard;
import strategies.plays.PlayFirst;
import strategies.plays.PlayFirstMeta;
import strategies.plays.PlayFirstPlayable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Game {
    public static final int numPlayers = 4;
    public static final int numGames = 1000;
    public static final boolean printLogs = false;
    public static List<Integer> scores = new ArrayList<>();
    public static final List<Strategy> strategies = List.of(
        new DiscardFirstDiscardable(),
        new DiscardFirstMeta(),
        new PlayFirstPlayable(),
        new PlayFirstMeta(),
        new HintKnowledgeWithMeta(2, 1, Knowledge.KnowledgeType.NUMBER, Knowledge.Meta.PLAY),
        new HintLowestPlayable(2, Knowledge.KnowledgeType.NUMBER),
        new DiscardFirst(),
        new PlayFirst()
    );
    public static final List<MetaStrategy> metaStrategies = List.of(
        new CardCountMeta(1, Knowledge.Meta.PLAY)
    );

    public static void main(String[] args) {
        for (int gameNumber = 1; gameNumber <= numGames; ++gameNumber) {
            try{
                //Initialization of deck introduces randomness
                Deck deck = Deck.initializeDeck();
                playGame(deck);
            }
            catch (Exception e){
                System.out.println("ERROR!: " + e + "\n" + Arrays.toString(e.getStackTrace()));
                System.exit(1);
            }
        }
        int winCount = (int) scores.stream().filter(score -> score == 25).count();
        double averageScore = scores.stream().reduce(0, Integer::sum).doubleValue() / numGames;
        System.out.println("You won " + winCount + " out of " + numGames + " games!\nAverage score: " + averageScore);
    }

    public static void playGame(Deck deck) throws Exception {
        TableState tableState = TableState.initializeTable(numPlayers, deck);
        if (printLogs) {
            System.out.println(tableState);
        }
        while (!Util.gameOver().test(tableState)) {
            tableState = takeTurn(tableState);
            if (printLogs) {
                System.out.println(tableState);
            }
        }
        scores.add(CardStacks.getScore().apply(tableState));
    }

    public static TableState takeTurn(TableState tableState) throws Exception{
        for (Strategy strategy: strategies) {
            if (strategy.isApplicable().test(tableState)) {
                if(printLogs){
                    System.out.println("Applying strategy " + strategy.getClass());
                }
                return strategy.runStrategy().apply(tableState);
            }
        }
        throw new Exception("No viable strategy");
    }

}
