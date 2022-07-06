package strategies.discards;

import models.TableState;

public class DiscardFirst extends DiscardStrategy {
    public DiscardFirst()
    {
        super();
    }

    @Override
    public Integer getDiscardCardIndex(TableState tableState) {
        return 0;
    }
}
