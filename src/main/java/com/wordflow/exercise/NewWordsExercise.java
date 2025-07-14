package com.wordflow.exercise;

import java.time.LocalTime;
import java.util.List;
import static com.wordflow.ui.InteractionHandler.*;
import com.wordflow.model.Dictionary;
import com.wordflow.model.FlashCard;

public class NewWordsExercise extends BaseExercise {

    private static final int MAX_WORDS_TO_REVIEW = 20;
    private final List<FlashCard> cards;

    public NewWordsExercise(Dictionary dictionary) {
        this.cards = selectCards(dictionary);
    }
    
    public List<FlashCard> getLessonContent() {
    	return cards;
    }

    private List<FlashCard> selectCards(Dictionary dictionary) {
        long dueCount = dictionary.findDueCards().stream()
                .filter(c -> c.getExerciseType() == FlashCard.FlashCardType.VOCAB)
                .count();

        
//        Добавить сообщение, что слишком много слов для повторения
        int limit = (dueCount >= MAX_WORDS_TO_REVIEW) ? 0 : getTimeBasedLimit();

        return dictionary.findNewCards(FlashCard.FlashCardType.VOCAB)
                         .stream()
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
            case 9, 10, 12, 14 -> 3;
            case 11, 13, 15, 16, 17, 18, 19 -> 2;
            default -> 0;
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
