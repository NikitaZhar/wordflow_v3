package com.wordflow.exercise;

import static com.wordflow.ui.InteractionHandler.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import com.wordflow.model.Dictionary;
import com.wordflow.model.FlashCard;

public class AdjectiveDeclensionExercise extends BaseExercise {

	private final List<FlashCard> cards;
	private int limit= getLimit();

	public AdjectiveDeclensionExercise(Dictionary dictionary) {
		this.cards = new ArrayList<>(
				dictionary.find(card ->
				card.getType() == FlashCard.FlashCardType.ADJECTDECLIN
						).stream()
				.sorted(Comparator.comparingInt(card -> card.getProgress().getReviewInterval()))
				.limit(limit)
				.toList()
				);
	}

	@Override
	public void runExercise() {
		if (cards.isEmpty()) return;
		Collections.shuffle(cards);
		clearScreen();
		waitForEnter("\n Adjective Declension Practice. Press Enter to begin\n");
		practice();
	}

	private void practice() {
		for (FlashCard card : cards) {
			boolean isCorrect;
			boolean toProgressInterval = true;
			//			this.currentCard = card;
			do {
				showQuestion(card);
				String exercisePrompt = card.getExercisePrompt();
				String userAnswer = getUserAnswerWithPromptExercise(exercisePrompt).trim();
				String correctAnswer = card.getExerciseAnswer().trim();
				isCorrect = userAnswer.equalsIgnoreCase(correctAnswer);
				if (isCorrect) {
					messageCorrectAnswer();
				} else {
					messageErrorAnswer();
					clearScreen();
					displayFullExercise(card);
					clearScreen();
				}
			} while (!isCorrect);
			card.toProgress(toProgressInterval);
		}
	}

	private int getLimit() {
		int limitExercises = 4;
		return limitExercises;
	}
}
