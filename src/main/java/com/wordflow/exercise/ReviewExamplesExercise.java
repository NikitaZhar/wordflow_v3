package com.wordflow.exercise;

import static com.wordflow.ui.InteractionHandler.*;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import com.wordflow.model.Dictionary;
import com.wordflow.model.FlashCard;

public class ReviewExamplesExercise extends BaseExercise {
	private final List<FlashCard> cards;
	private int limit;

	public ReviewExamplesExercise(Dictionary dictionary) {
		this.limit = getLimit();

		this.cards = new ArrayList<>(
				dictionary.findDueCards(FlashCard.FlashCardType.EXAMPLE).stream()
				.sorted(Comparator.comparingInt(card -> card.getProgress().getReviewInterval()))
				.limit(limit)
				.toList()
				);
	}

	@Override
	public void runExercise() {
		if(cards.isEmpty()) return;
		Collections.shuffle(cards);
		clearScreen();
		waitForEnter("\n Review past examples. Press Enter to begin\n");
		practice();
	}

	private int getLimit() {
		int hour = LocalTime.now().getHour();
		return switch (hour) {
		case 8 -> 0;
		case 9 -> 2;
		case 10 -> 2;
		case 11 -> 3;
		case 12 -> 2;
		case 13 -> 3;
		case 14 -> 2;
		case 15 -> 3;
		case 16 -> 2;
		case 17 -> 3;
		case 18, 19, 20 -> 2;
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

			card.toProgress(toProgressInterval);
		}
	}
}
