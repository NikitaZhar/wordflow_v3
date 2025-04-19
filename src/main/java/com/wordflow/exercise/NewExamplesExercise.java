package com.wordflow.exercise;

import static com.wordflow.ui.InteractionHandler.*;

import java.time.LocalTime;
import java.util.List;
import com.wordflow.model.Dictionary;
import com.wordflow.model.FlashCard;

public class NewExamplesExercise extends BaseExercise {
	private static final int MAX_EXAMPLES_TO_REVIEW = 5;
	private List<FlashCard> cards;
	private int limit;

	public NewExamplesExercise(Dictionary dictionary) {
		selectCards(dictionary);
	}

	private void selectCards(Dictionary dictionary) {
		long numberDueCards = dictionary.findDueCards(FlashCard.FlashCardType.EXAMPLE).size();

		this.limit = (numberDueCards >= MAX_EXAMPLES_TO_REVIEW) ? 0 : getLimit();
		this.cards = dictionary.findNewCards(FlashCard.FlashCardType.EXAMPLE).stream()
				.limit(limit)
				.toList();
	}

	public void runExercise() {
		if(cards.isEmpty()) return;
		clearScreen();
		waitForEnter("\n Learn new examples. Press Enter to begin\n");
		displayFullCards();
		clearScreen();
		waitForEnter("\n Review new examples. Press Enter to begin\n");
		reviewCards();
		progressCards();
	}

	private int getLimit() {
		int hour = LocalTime.now().getHour();
		return switch (hour) {
		case 8 -> 0;
		case 9 -> 0;
		case 10 -> 1;
		case 11 -> 1;
		case 12 -> 0;
		case 13 -> 1;
		case 14, 16, 18, 20 -> 0;
		case 15, 17, 19 -> 1;
		default -> 0; 
		};
	}

	private void reviewCards() {
		for(FlashCard card : cards) {
			boolean isCorrect;
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
				}
			} while (!isCorrect);
		}
	}

	private void displayFullCards() {
		for(FlashCard card : cards) {
			displayFullCard(card);
		}
	}

	private void progressCards() {
		for(FlashCard card : cards) {
			card.toProgress(true);
		}
	}
}
