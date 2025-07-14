package com.wordflow.exercise;

import static com.wordflow.ui.InteractionHandler.*;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import com.wordflow.model.Dictionary;
import com.wordflow.model.FlashCard;

public class ReviewWordsExercise extends BaseExercise {
	private final List<FlashCard> cards;
	private int limit;

	public ReviewWordsExercise(Dictionary dictionary) {
		this.limit = getLimit();

		this.cards = new ArrayList<>(
				dictionary.findDueCards().stream()
				.sorted(Comparator.comparingInt(card -> card.getProgress().getMinsToRepeat()))
				.limit(limit)
				.toList()
				);
	}
	
	public List<FlashCard> getLessonContent() {
    	return cards;
    }

	@Override
	public void runExercise() {
		if(cards.isEmpty()) return;
		Collections.shuffle(cards);
		clearScreen();
		waitForEnter("\n Review past words. Press Enter to begin\n");
		practice();
	}

	private int getLimit() {
		int hour = LocalTime.now().getHour();
		return switch (hour) {
		case 8 -> 3;
		case 9, 10, 12, 14 -> 5;
		case 11, 13, 15, 16 -> 8;
		case 17, 18 -> 5;
		case 19, 20 -> 5;
		default -> 0; 
		};
	}

	private void practice() {
		for(FlashCard card : cards) {
			boolean isCorrect;
			boolean toProgressInterval = true;
			do {
				showQuestion(card);
				String userAnswer = getUserAnswerWithPrompt();
				String correctAnswer = card.getCleanAnswer();
				isCorrect = userAnswer.equals(correctAnswer);
				if (isCorrect) {
					messageCorrectAnswer();
					isCorrect = true;
				} else {
					messageErrorAnswer();
					clearScreen();
					displayFullCard(card);
					clearScreen();
					isCorrect = false;
					toProgressInterval = false;
				}
			} while (!isCorrect);
			card.registerWordProgress(toProgressInterval);
		}
	}
}
