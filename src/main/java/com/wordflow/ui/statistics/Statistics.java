package com.wordflow.ui.statistics;

import static com.wordflow.ui.InteractionHandler.displayMessage;

import java.util.*;
import java.util.stream.Collectors;

import com.wordflow.model.Dictionary;
import com.wordflow.model.FlashCard;
import com.wordflow.model.Example;
import com.wordflow.ui.InteractionHandler;

public class Statistics {

    public record StatBlock(int toLearn, int toReview, int notReady) {}

    public void displayStats(Dictionary dictionary) {
        StatBlock wordStats = calculateFlashCardStats(dictionary);
        StatBlock exampleStats = calculateExampleStats(dictionary);

        displayMessage("\n            * * * Current Statistics * * * \n");

        displayMessage(formatStatLine(" Words : ", wordStats));
        displayMessage(formatStatLine(" Examples : ", exampleStats));

        displayGroupedTagBlocks(dictionary);

        InteractionHandler.waitForEnter("\n Press Enter to continue ... ");
    }

    private StatBlock calculateFlashCardStats(Dictionary dictionary) {
        List<FlashCard> cards = dictionary.getAllCards();

        int toLearn = (int) cards.stream()
                .filter(card -> card.getProgress() != null && card.getProgress().isNew())
                .count();

        int toReview = (int) cards.stream()
                .filter(card -> card.getProgress() != null && card.getProgress().isDue())
                .count();

        int notReady = (int) cards.stream()
                .filter(card -> card.getProgress() != null &&
                        !card.getProgress().isNew() && !card.getProgress().isDue())
                .count();

        return new StatBlock(toLearn, toReview, notReady);
    }

    private StatBlock calculateExampleStats(Dictionary dictionary) {
        List<Example> examples = dictionary.getAllCards().stream()
                .flatMap(card -> card.getExamples().stream())
                .filter(Example::getActive)
                .collect(Collectors.toList());
        
        int toLearn = (int) examples.stream()
                .filter(ex -> ex.getProgress() != null && ex.getProgress().isNew())
                .count();

        int toReview = (int) examples.stream()
                .filter(ex -> ex.getProgress() != null && ex.getProgress().isDue())
                .count();

        int notReady = (int) examples.stream()
                .filter(ex -> ex.getProgress() != null &&
                        !ex.getProgress().isNew() && !ex.getProgress().isDue())
                .count();

        return new StatBlock(toLearn, toReview, notReady);
    }

    private void displayGroupedTagBlocks(Dictionary dictionary) {
        List<Example> activeExamples = dictionary.getAllCards().stream()
                .flatMap(card -> card.getExamples().stream())
                .filter(ex -> Boolean.TRUE.equals(ex.getActive()))
                .collect(Collectors.toList());

        // Группируем по уникальным комбинациям тегов
        Map<String, List<Example>> grouped = activeExamples.stream()
                .collect(Collectors.groupingBy(ex -> ex.getGrammarTags().stream()
                        .sorted()
                        .collect(Collectors.joining(" + "))
                ));

        grouped.entrySet().stream()
                .sorted(Map.Entry.comparingByKey())
                .forEach(entry -> {
                    String tagBlock = "   " + entry.getKey();
                    List<Example> examples = entry.getValue();
                    StatBlock stats = calculateStatsForExamples(examples);
                    displayMessage(formatStatLine(tagBlock, stats));
                });
    }

    private StatBlock calculateStatsForExamples(List<Example> examples) {
        int toLearn = (int) examples.stream()
                .filter(ex -> ex.getProgress() != null && ex.getProgress().isNew())
                .count();

        int toReview = (int) examples.stream()
                .filter(ex -> ex.getProgress() != null && ex.getProgress().isDue())
                .count();

        int notReady = (int) examples.stream()
                .filter(ex -> ex.getProgress() != null &&
                        !ex.getProgress().isNew() && !ex.getProgress().isDue())
                .count();

        return new StatBlock(toLearn, toReview, notReady);
    }

    private String formatStatLine(String label, StatBlock stats) {
        return String.format(
                "%-25s  | %3d to learn | %3d to review | %3d not ready",
                label,
                stats.toLearn(),
                stats.toReview(),
                stats.notReady()
        );
    }
}
