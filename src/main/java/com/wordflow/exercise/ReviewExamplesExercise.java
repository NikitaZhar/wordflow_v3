package com.wordflow.exercise;

import static com.wordflow.ui.InteractionHandler.*;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import com.wordflow.config.AppDefaults;
import com.wordflow.model.Dictionary;
import com.wordflow.model.Example;
import com.wordflow.model.FlashCard;

public class ReviewExamplesExercise extends BaseExercise {

	private List<FlashCard> cardsToReview;
	private final int limit;
	private Dictionary dictionary;

	public ReviewExamplesExercise(Dictionary dictionary) {
		this.dictionary = dictionary;
		this.limit = getLimit();
		this.cardsToReview = getLessonContent();
	}

	@Override
	public List<FlashCard> getLessonContent() {
		return dictionary.getAllCards().stream()
				.filter(card -> card.hasActiveExample())
				.filter(card -> card.getActiveExample().getProgress() != null &&
				card.getActiveExample().getProgress().isDue())
				.sorted(Comparator.comparingInt(card -> card.getActiveExample().getProgress().getSuccessCount()))
				.limit(limit)
				.collect(Collectors.toCollection(ArrayList::new));
	}

	private int getLimit() {
		int hour = LocalTime.now().getHour();
		return switch (hour) {
		case 8 -> 1;
		case 9, 11, 13, 15, 17 -> 3;
		case 10, 12, 14, 16, 18, 19 -> 2;
		case 20 -> 1;
		default -> 1;
		};
	}

	@Override
	public void runExercise() {
		if (cardsToReview.isEmpty()) return;
		Collections.shuffle(cardsToReview);
		clearScreen();
		waitForEnter("\n Review past examples. Press Enter to begin\n");
		practice();
		examplesPromotion();
	}

	private void practice() {
		for(FlashCard card : cardsToReview) {
			Example example = card.getActiveExample();
			boolean isCorrect;
			boolean toProgressInterval = true;
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
					toProgressInterval = false;
				}
			} while (!isCorrect);
			example.registerExampleProgress(toProgressInterval);
		}
	}

	private void examplesPromotion() {
		for(FlashCard card : cardsToReview) {
			Example example = card.getActiveExample();
			if(example.getProgress().getMinsToRepeat() > AppDefaults.THRESHHOLD_FOR_NEXT_EXAMPLE) {
				int indexActiveExample = card.getActiveExampleIndex();
				if(indexActiveExample >= 0 && indexActiveExample < card.getExamples().size() -1) {
					List<Example> examples = card.getExamples();
					if (indexActiveExample < examples.size() - 1) {
						examples.get(indexActiveExample).setActive(false);
//						waitForEnter("\n Passed example : " + example.getDeExample() + " Press Enter to continue ...");
						examples.get(indexActiveExample + 1).setActive(true);
//						waitForEnter("\n New example : " + card.getExamples().get(indexActiveExample + 1).getDeExample() + " Press Enter to continue ...");
					} else {
						examples.get(indexActiveExample).setActive(false);
						waitForEnter("\n Passed last example: " + example.getDeExample() + " (no more examples). Press Enter to continue...");
					}
				}
			}
		}
	}
}
