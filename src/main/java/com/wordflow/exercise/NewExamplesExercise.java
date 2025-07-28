package com.wordflow.exercise;

import static com.wordflow.ui.InteractionHandler.clearScreen;
import static com.wordflow.ui.InteractionHandler.waitForEnter;

import java.time.LocalTime;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import com.wordflow.model.Dictionary;
import com.wordflow.model.Example;
import com.wordflow.model.FlashCard;
import com.wordflow.model.Progress;

public class NewExamplesExercise extends BaseExercise {
	private static final int MAX_EXAMPLES_TO_REVIEW = 5;
	private List<Example> examplesToLearn = Collections.emptyList();
	private int limit;
	private Dictionary dictionary;

	public NewExamplesExercise(Dictionary dictionary) {
		this.dictionary = dictionary;
		getLessonContent();
	}

	@Override
	public List<FlashCard> getLessonContent() {
		List<FlashCard> allCards = dictionary.getAllCards();

//		Карточки:
//		- для которых есть активные примеры
//		- получить прогресс активного примера
//		- выбрать примеры, доступные для повторения
		
		List<FlashCard> dueCards = allCards.stream()
				.filter(card -> card.hasActiveExample())
				.filter(card -> card.getExamples().stream()
						.filter(Example::isActive)
						.map(Example::getProgress)
						.anyMatch(Progress::isDue))
				.collect(Collectors.toList());
		
		this.limit = (dueCards.size() > MAX_EXAMPLES_TO_REVIEW) ? 0 : getLimit();

		if (this.limit == 0) {
			this.examplesToLearn = Collections.emptyList();
			return Collections.emptyList();
		}

//		Карточки:
//		- для которых есть активные примеры
//		- для которых есть новые примеры
//		- ограничены по количеству
		
		List<FlashCard> cardsForLesson = allCards.stream()
				.filter(card -> card.hasActiveExample())
				.filter(card -> card.getExamples().stream()
						.filter(Example::isActive)
						.map(Example::getProgress)
						.filter(Objects::nonNull)
						.anyMatch(Progress::isNew))
				.limit(limit)
				.collect(Collectors.toList());

		getLessonExamples(cardsForLesson);
		return cardsForLesson;
	}

	private void getLessonExamples(List<FlashCard> cardsForLesson) {
		if (limit == 0) {
			this.examplesToLearn = Collections.emptyList();
		} else {

			this.examplesToLearn = cardsForLesson.stream()
					.map(FlashCard::getActiveExample)
					.collect(Collectors.toList());
			Collections.shuffle(examplesToLearn);
		}
	}

	@Override
	public void runExercise() {
		if (examplesToLearn.isEmpty()) return;
		
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
		case 8, 9, 11, 13, 15, 17 -> 2;
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
