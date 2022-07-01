package game;

import models.CardStacks;
import models.Deck;
import models.TableState;
import strategies.randoms.GiveRandomHint;
import strategies.sureThings.DiscardFirstDiscardable;
import strategies.sureThings.PlayFirstPlayable;
import strategies.randoms.RandomStrategy;
import strategies.Strategy;

import java.util.ArrayList;
import java.util.List;

public class Game {
    public static final int numPlayers = 4;
    public static final int numGames = 1000;
    public static List<Integer> scores = new ArrayList<>();
    //TODO Replace random strategy with non-random strategies
    public static final List<Strategy> strategies = List.of(
        new DiscardFirstDiscardable(),
        new PlayFirstPlayable(),
        new GiveRandomHint(3),
        new RandomStrategy()
    );

    public static void main(String[] args) {
        for (int gameNumber = 1; gameNumber <= numGames; ++gameNumber) {
            try{
                //Initialization of deck introduces randomness
                Deck deck = Deck.initializeDeck();
                playGame(deck);
            }
            catch (Exception e){
                System.out.println("ERROR!: " + e);
                System.exit(1);
            }
        }
        int winCount = (int) scores.stream().filter(score -> score == 25).count();
        double averageScore = scores.stream().reduce(0, Integer::sum).doubleValue() / numGames;
        System.out.println("You won " + winCount + " out of " + numGames + " games!\nAverage score: " + averageScore);
    }

    public static void playGame(Deck deck) throws Exception {
        TableState tableState = TableState.initializeTable(numPlayers, deck);
        int playerIndex = 0;
        while (!Util.gameOver().test(tableState)) {
            tableState = takeTurn(tableState);
            playerIndex = (playerIndex + 1) % numPlayers;
        }
        scores.add(CardStacks.getScore().apply(tableState));
    }

    public static TableState takeTurn(TableState tableState) throws Exception{
        for (Strategy strategy: strategies) {
            if (strategy.isApplicable(tableState)) {
                return strategy.runStrategy(tableState);
            }
        }
        throw new Exception("No viable strategy");
    }

}
