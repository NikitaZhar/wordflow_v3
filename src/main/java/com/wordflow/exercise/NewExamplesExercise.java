package com.wordflow.exercise;

import static com.wordflow.ui.InteractionHandler.clearScreen;
import static com.wordflow.ui.InteractionHandler.waitForEnter;

import java.time.LocalTime;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import com.wordflow.model.Dictionary;
import com.wordflow.model.Example;
import com.wordflow.model.FlashCard;

public class NewExamplesExercise extends BaseExercise {
//    private static final int MAX_EXAMPLES_TO_REVIEW = 5;
    private List<Example> examplesToLearn;
    private int limit;
    private Dictionary dictionary;

    public NewExamplesExercise(Dictionary dictionary) {
    	this.dictionary = dictionary;
        selectExamples();
    }
    
    @Override
    public List<FlashCard> getLessonContent() {
        return examplesToLearn.stream()
                .map(example -> dictionary.getAllCards().stream()
                        .filter(card -> card.getExamples().contains(example))
                        .findFirst()
                        .orElse(null))
                .filter(card -> card != null)
                .collect(Collectors.toList());
    }

    private void selectExamples() {
//        long numberDueCards = dictionary.findDueCards().size();
//        this.limit = (numberDueCards >= MAX_EXAMPLES_TO_REVIEW) ? 0 : getLimit();
        
        this.limit = getLimit();

        if (limit == 0) {
            this.examplesToLearn = Collections.emptyList();
            return;
        }

        List<Example> candidateExamples = dictionary.getAllCards().stream()
                .flatMap(card -> card.getExamples().stream())
                .filter(ex -> Boolean.TRUE.equals(ex.getActive()))
                .filter(ex -> ex.getProgress().getSuccessCount() == 0)
                .collect(Collectors.toList());

        Collections.shuffle(candidateExamples);

        this.examplesToLearn = candidateExamples.stream()
                .limit(limit)
                .collect(Collectors.toList());
    }

    public void runExercise() {
        if (examplesToLearn.isEmpty()) {
            return;
        }
        clearScreen();
        waitForEnter("\n Learn new examples. Press Enter to begin\n");
        displayExamples();
        clearScreen();
        waitForEnter("\n Review new examples. Press Enter to begin\n");
        reviewExamples();
        updateExampleProgress();

    }

    private int getLimit() {
        int hour = LocalTime.now().getHour();
        return switch (hour) {
            case 9, 11, 13, 15, 17 -> 2;
            case 10, 12, 14, 16, 18 -> 1;
            default -> 1;
        };
    }

    private void displayExamples() {
        for (Example example : examplesToLearn) {
            displayFullExample(example);
        }
    }

    private void reviewExamples() {
        for (Example example : examplesToLearn) {
            boolean isCorrect;
            do {
                showQuestionExample(example);
                String userAnswer = getUserAnswerWithPrompt();
                String correctAnswer = example.getDeExample().trim();
                isCorrect = userAnswer.trim().equalsIgnoreCase(correctAnswer);
                if (isCorrect) {
                    messageCorrectAnswer();
                } else {
                    messageErrorAnswer();
                    clearScreen();
                    displayFullExample(example);
                    clearScreen();
                }
            } while (!isCorrect);
        }
    }

    private void updateExampleProgress() {
        for (Example example : examplesToLearn) {
//        	System.out.printf("\n Новый пример. До обновления %s \n", example.getProgress().getSuccessCount());
        	example.registerExampleProgress(true);
//        	System.out.printf("\n Новый пример. После обновления %s \n", example.getProgress().getSuccessCount());
        }
    }

}
