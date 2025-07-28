package com.wordflow.exercise;

import java.time.LocalTime;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static com.wordflow.ui.InteractionHandler.*;
import com.wordflow.model.Dictionary;
import com.wordflow.model.Example;
import com.wordflow.model.FlashCard;
import com.wordflow.model.Progress;

public class NewWordsExercise extends BaseExercise {

    private static final int MAX_WORDS_TO_REVIEW = 20;
    private static final int MAX_EXAMPLES_TO_REVIEW = 10;
    private List<FlashCard> cards;
    private Dictionary dictionary;

    public NewWordsExercise(Dictionary dictionary) {
        this.dictionary = dictionary;
        this.cards = getLessonContent();
    }
    
    public List<FlashCard> getLessonContent() {
    	List<FlashCard> allCards = dictionary.getAllCards();
    	
//    	Надо выбрать карточки из всего словаря:
//    	- Карточки без активных примеров
//    	- Карточки, доступные для повторения
    	
    	long numberDueCards = allCards.stream()
    			.filter(card -> !card.hasActiveExample())
    	        .map(FlashCard::getProgress)
    	        .filter(Objects::nonNull)
    	        .filter(Progress::isDue)
    	        .count();
    	
    	long numberDueExamples = allCards.stream()
    		    .filter(card -> card.hasActiveExample())
    		    .filter(card -> card.getExamples().stream()
    		            .filter(Example::isActive)
    		            .map(Example::getProgress)
    		            .anyMatch(Progress::isDue))
    		    .count();
    	
    	int limit = (numberDueCards >= MAX_WORDS_TO_REVIEW || numberDueExamples >= MAX_EXAMPLES_TO_REVIEW)
    			? 0 : getTimeBasedLimit();
    	
//    	Выбрать новые карточки, ограниченные по количеству
    	return allCards.stream()
    	        .filter(card -> card.getProgress() != null && card.getProgress().isNew())
    	        .limit(limit)
    	        .toList();
    }
        
    public void runExercise() {
        if (cards.isEmpty()) return;
        waitForEnter("\n Learn new words. Press Enter to begin\n");
        displayFullCards();
        clearScreen();
        waitForEnter("\n Review new words. Press Enter to begin\n");
        reviewCards();
        cardProgress();
    }

    private int getTimeBasedLimit() {
        int hour = LocalTime.now().getHour();
        return switch (hour) {
            case 9, 10, 12, 14 -> 2;
            case 11, 13, 15, 16, 17, 18, 19 -> 2;
            default -> 1;
        };
    }

    private void reviewCards() {
        for (FlashCard card : cards) {
            while (true) {
                showQuestion(card);
                String userAnswer = getUserAnswerWithPrompt();
                String correctAnswer = card.getCleanAnswer();
                if (userAnswer.equalsIgnoreCase(correctAnswer)) {
                    messageCorrectAnswer();
                    break;
                } else {
                    messageErrorAnswer();
                    clearScreen();
                    displayFullCard(card);
                    clearScreen();
                }
            }
        }
    }

    private void displayFullCards() {
        for (FlashCard card : cards) {
            displayFullCard(card);
        }
    }

    private void cardProgress() {
        for (FlashCard card : cards) {
            card.registerWordProgress(true);
        }
    }
}
