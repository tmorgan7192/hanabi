package strategies.plays;

import models.TableState;

public class PlayFirst extends PlayStrategy {
    public PlayFirst()
    {
    }

    @Override
    public Integer getPlayCardIndex(TableState tableState) {
        return 0;
    }
}
