package com.wordflow.ui.statistics;

import static com.wordflow.ui.InteractionHandler.displayMessage;
import com.wordflow.model.Dictionary;
import com.wordflow.model.FlashCard;

public class Statistics {
	public record StatBlock(int toLearn, int toReview, int notReady) {}

    public void displayStats(Dictionary dictionary) {
        StatBlock wordStats = calculateStats(dictionary, FlashCard.FlashCardType.WORD);
        StatBlock exampleStats = calculateStats(dictionary, FlashCard.FlashCardType.EXAMPLE);

        String wordLine = String.format("Words:     %4d to learn   |   %2d to review   |   %2d not ready",
                wordStats.toLearn(), wordStats.toReview(), wordStats.notReady());

        String exampleLine = String.format("Examples:  %4d to learn   |   %2d to review   |   %2d not ready",
                exampleStats.toLearn(), exampleStats.toReview(), exampleStats.notReady());

        displayMessage("\n" + wordLine + "\n" + exampleLine + "\n");
    }

    private StatBlock calculateStats(Dictionary dictionary, FlashCard.FlashCardType type) {
        int toLearn = dictionary.count(card -> card.getType() == type && card.getProgress().isNew());
        int toReview = dictionary.count(card -> card.getType() == type && card.getProgress().isDue());
        int notReady = dictionary.count(card ->
                card.getType() == type &&
                !card.getProgress().isNew() &&
                !card.getProgress().isDue()
        );
        return new StatBlock(toLearn, toReview, notReady);
    }
}
