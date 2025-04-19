package com.wordflow.exercise;

import java.time.LocalTime;
import java.util.List;
import static com.wordflow.ui.InteractionHandler.*;
import com.wordflow.model.Dictionary;
import com.wordflow.model.FlashCard;

public class NewWordsExercise extends BaseExercise {
	private static final int MAX_WORDS_TO_REVIEW = 20;
	private List<FlashCard> cards;
	private int limit= getLimit();

	public NewWordsExercise(Dictionary dictionary) {
		selectCards(dictionary);
	}
	
	private void selectCards(Dictionary dictionary) {
		long numberDueCards = dictionary.findDueCards(FlashCard.FlashCardType.WORD).size();

		this.limit = (numberDueCards >= MAX_WORDS_TO_REVIEW) ? 0 : getLimit();
		this.cards = dictionary.findNewCards(FlashCard.FlashCardType.WORD).stream()
				.limit(limit)
				.toList();
	}

	public void runExercise() {
		if(cards.isEmpty()) return;
		waitForEnter("\n Learn new words. Press Enter to begin\n");
		displayFullCards();
		clearScreen();
		waitForEnter("\n Review new words. Press Enter to begin\n");
		reviewCards();
		progressCards();
	}

	private int getLimit() {
		int hour = LocalTime.now().getHour();
		return switch (hour) {
		case 8 -> 0;
		case 9 -> 0;
		case 10 -> 3;
		case 11 -> 0;
		case 12 -> 3;
		case 13 -> 0;
		case 14 -> 3;
		case 15, 16, 17, 18, 19, 20 -> 1;
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
