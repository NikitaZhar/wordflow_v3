package com.wordflow.exercise;

import static com.wordflow.ui.InteractionHandler.*;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import com.wordflow.model.Dictionary;
import com.wordflow.model.Example;
import com.wordflow.model.FlashCard;

public class ReviewExamplesExercise extends BaseExercise {

	private final List<Example> examplesToReview;
	private final int limit;
	private Dictionary dictionary;

	public ReviewExamplesExercise(Dictionary dictionary) {
		this.dictionary = dictionary;
		this.limit = getLimit();
		this.examplesToReview = selectExamples(dictionary);
	}
	
	@Override
	public List<FlashCard> getLessonContent() {
	    return examplesToReview.stream()
	            .map(example -> dictionary.getAllCards().stream()
	                    .filter(card -> card.getExamples().contains(example))
	                    .findFirst()
	                    .orElse(null))
	            .filter(card -> card != null)
	            .collect(Collectors.toList());
	}

	private List<Example> selectExamples(Dictionary dictionary) {
		return dictionary.getAllCards().stream()
				.flatMap(card -> card.getExamples().stream())
				.filter(example -> Boolean.TRUE.equals(example.getActive()))
				.filter(example -> example.getProgress() != null && example.getProgress().isDue())
				.sorted(Comparator.comparingInt(example -> example.getProgress().getSuccessCount()))
				.limit(limit)
				.collect(Collectors.toCollection(ArrayList::new));
	}

	private int getLimit() {
		int hour = LocalTime.now().getHour();
		return switch (hour) {
		case 8 -> 1;
		case 9, 10, 12, 14 -> 2;
		case 11, 13, 15, 16 -> 3;
		case 17, 18 -> 2;
		case 19, 20 -> 1;
		default -> 1;
		};
	}

	@Override
	public void runExercise() {
		if (examplesToReview.isEmpty()) return;
		Collections.shuffle(examplesToReview);
		clearScreen();
		waitForEnter("\n Review past examples. Press Enter to begin\n");
		practice();
	}

	private void practice() {
		for(Example example : examplesToReview) {
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
			activateNextExample(example);
		}
	}

	private void activateNextExample(Example currentExample) {
		if (currentExample.getProgress().getMinsToRepeat() > 400) {
			currentExample.setActive(false);
			
			waitForEnter("\n Passed example : " + currentExample.getDeExample() + " Press Enter to continue ");

			dictionary.getAllCards().stream()
			.filter(card -> card.getExamples().contains(currentExample))
			.findFirst()
			.ifPresent(card -> {
				List<Example> examples = card.getExamples();
				int idx = examples.indexOf(currentExample);
				if (idx >= 0 && idx + 1 < examples.size()) {
					Example nextExample = examples.get(idx + 1);
					if (!Boolean.TRUE.equals(nextExample.getActive())) {
						nextExample.setActive(true);
						waitForEnter("\n New example : " + currentExample.getDeExample() + " Press Enter to continue ");
					}
				}
			}
					);
		}
	}
}
