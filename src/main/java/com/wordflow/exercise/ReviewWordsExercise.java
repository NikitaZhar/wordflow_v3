package com.wordflow.exercise;

import static com.wordflow.ui.InteractionHandler.*;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import com.wordflow.model.Dictionary;
import com.wordflow.model.FlashCard;

public class ReviewWordsExercise extends BaseExercise {
	private final List<FlashCard> cards;
	private Dictionary dictionary;
	private int limit;

	public ReviewWordsExercise(Dictionary dictionary) {
		this.limit = getLimit();
		this.dictionary = dictionary;
		this.cards = getLessonContent();
	}
	
	public List<FlashCard> getLessonContent() {
		List<FlashCard> allCards = dictionary.getAllCards();
		
//		Выбрать карточки:
//		- для которых нет активных примеров
//		- достдупные для повторения
//		- отсортированные по времени повторения
//		- ограниченные по количеству
		
		return allCards.stream()
				.filter(card -> !card.hasActiveExample())
				.filter(card -> card.getProgress().isDue())
				.sorted(Comparator.comparingInt(card -> card.getProgress().getMinsToRepeat()))
				.limit(limit)
				.collect(Collectors.toCollection(ArrayList::new));
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
		case 9, 10, 12, 14 -> 10;
//		5;
		case 11, 13, 15, 16 -> 10;
//		8;
		case 17, 18 -> 10;
//		5;
		case 19, 20 -> 10;
//		5;
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
