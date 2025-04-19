package com.wordflow;

import com.wordflow.ui.menu.MainMenuController;

public class WordflowApp {
    public static void main(String[] args) {
    	new MainMenuController().runMainMenu();
    }
}

package com.wordflow.config;

public class AppDefaults {
	private AppDefaults() {
	}
	
//	public static final int NUMBER_NEW_WORDS_TO_LEARN = 3;
//	public static final int NUMBER_WORDS_TO_REVIEW = 5;
	public static final int REVIEW_INTERVAL_IN_MINS = 30;
//	public static final int NUMBER_NEW_EXAMPLES_TO_LEARN = 1;
//	public static final int NUMBER_EXAMPLES_TO_REVIEW = 3;
	public static final Language LANGUAGE = Language.GER;;
	
	public enum Language {
		GER, ENG, SLK
	}
}

package com.wordflow.exercise;

import static com.wordflow.ui.InteractionHandler.BRIGHT_BLACK;
import static com.wordflow.ui.InteractionHandler.GREEN;
import static com.wordflow.ui.InteractionHandler.RED;
import static com.wordflow.ui.InteractionHandler.RESET;
import static com.wordflow.ui.InteractionHandler.WHITE;
import static com.wordflow.ui.InteractionHandler.displayMessage;
import static com.wordflow.ui.InteractionHandler.getStringInput;
import static com.wordflow.ui.InteractionHandler.waitForEnter;

import com.wordflow.model.FlashCard;

public abstract class BaseExercise implements Exercise {
	private static final String QUESTION = GREEN + " RU : " + RESET + "%s";
	private static final String REVIEW_PROMPT =
			WHITE + " DE >" + RESET;
	private static final String FULL_CARD_TEMPLATE =
			QUESTION + "\n" +
					GREEN + " DE : " + RESET + "%s\n" +  
					BRIGHT_BLACK + "	Press Enter to continue" +
					RESET;


	public void showQuestion(FlashCard card) {
		displayMessage(String.format(QUESTION, card.getQuestion()));
	}
	
	public void displayFullCard(FlashCard card) {
		waitForEnter(String.format(FULL_CARD_TEMPLATE, card.getQuestion(), card.getAnswer()));
	}
	
	public String getUserAnswerWithPrompt() {
		return getStringInput(String.format(REVIEW_PROMPT));
	}
	
	public void messageCorrectAnswer() {
		waitForEnter("\n" + GREEN + " ✔ " + RESET+ "Correct. Press Enter to continue\n");
	}
	
	public void messageErrorAnswer() {
		waitForEnter("\n" + RED + " ✘ " + RESET + "WRONG. Press Enter to repeat\n");
	}
	
	
}

package com.wordflow.exercise;

import com.wordflow.model.FlashCard;

public interface Exercise {
	public void runExercise();
	public void messageCorrectAnswer();
	public void messageErrorAnswer();
	public void showQuestion(FlashCard card);
	public void displayFullCard(FlashCard card);
	public String getUserAnswerWithPrompt();
}

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
		
		
		
		
//		List<FlashCard> availableCards = dictionary.findByType(FlashCard.FlashCardType.EXAMPLE);
//		
//		long numberPastCards = availableCards.stream()
//				.filter(card -> card.getProgress().isDue())
//				.count();
//		
//		this.limit = (numberPastCards >= MAX_EXAMPLES_TO_REVIEW) ? 0 : getLimit();
//		this.cards = availableCards.stream()
//				.filter(card -> card.getProgress().isNew())
//				.limit(limit)
//				.toList();
	}

	public void runExercise() {
		if(cards.isEmpty()) return;
		clearScreen();
		waitForEnter("\n Learn new examples. Press Enter to continue\n");
		displayFullCards();
		clearScreen();
		waitForEnter("\n Review new examples. Press Enter to continue\n");
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
		case 14 -> 1;
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
		
		
		
		
		
		
		
		
//		List<FlashCard> availableCards = dictionary.findByType(FlashCard.FlashCardType.WORD);
//		
//		long numberPastCards = availableCards.stream()
//				.filter(card -> card.getProgress().isDue())
//				.count();
//		
//		this.limit = (numberPastCards >= MAX_WORDS_TO_REVIEW) ? 0 : getLimit();
//		this.cards = availableCards.stream()
//				.filter(card -> card.getProgress().isNew())
//				.limit(limit)
//				.toList();
	}

	public void runExercise() {
		if(cards.isEmpty()) return;
		waitForEnter("\n Learn new words. Press Enter to continue\n");
		displayFullCards();
		clearScreen();
		waitForEnter("\n Review new words. Press Enter to continue\n");
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

package com.wordflow.exercise;

import static com.wordflow.ui.InteractionHandler.*;

import java.time.LocalTime;
import java.util.Comparator;
import java.util.List;

import com.wordflow.model.Dictionary;
import com.wordflow.model.FlashCard;

public class ReviewExamplesExercise extends BaseExercise {
	private final List<FlashCard> cards;
	private int limit;

	public ReviewExamplesExercise(Dictionary dictionary) {
		this.limit = getLimit();

		this.cards = dictionary.findDueCards(FlashCard.FlashCardType.EXAMPLE).stream()
			.sorted(Comparator.comparingInt(card -> card.getProgress().getReviewInterval()))
			.limit(limit)
			.toList();

		
		
		
		
		
//		this.limit = getLimit();
//		this.cards = dictionary.find(card ->
//		card.getType() == FlashCard.FlashCardType.EXAMPLE &&
//		card.getProgress().isDue()
//				).stream()
//				.sorted(Comparator.comparingInt(card -> card.getProgress().getReviewInterval()))
//				.limit(limit)
//				.toList();
	}

	public void runExercise() {
		if(cards.isEmpty()) return;
		clearScreen();
		waitForEnter("\n Review past examples. Press Enter to continue\n");
		reviewCard();
	}

	private int getLimit() {
		int hour = LocalTime.now().getHour();
		return switch (hour) {
		case 8 -> 1;
		case 9 -> 2;
		case 10 -> 2;
		case 11 -> 3;
		case 12 -> 2;
		case 13 -> 3;
		case 14 -> 2;
		case 15 -> 3;
		case 16 -> 2;
		case 17 -> 3;
		case 18 -> 1;
		case 19 -> 1;
		default -> 0; 
		};
	}

	private void reviewCard() {
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

package com.wordflow.exercise;

import static com.wordflow.ui.InteractionHandler.*;

import java.time.LocalTime;
import java.util.Comparator;
import java.util.List;
import com.wordflow.model.Dictionary;
import com.wordflow.model.FlashCard;

public class ReviewWordsExercise extends BaseExercise {
	private final List<FlashCard> cards;
	private int limit;

	public ReviewWordsExercise(Dictionary dictionary) {
		this.limit = getLimit();

		this.cards = dictionary.findDueCards(FlashCard.FlashCardType.WORD).stream()
			.sorted(Comparator.comparingInt(card -> card.getProgress().getReviewInterval()))
			.limit(limit)
			.toList();

		
		
		
		
		
//		this.limit = getLimit();
//		
//		this.cards = dictionary.find(card ->
//		card.getType() == FlashCard.FlashCardType.WORD &&
//		card.getProgress().isDue()
//				).stream()
//				.sorted(Comparator.comparingInt(card -> card.getProgress().getReviewInterval()))
//				.limit(limit)
//				.toList();
	}

	public void runExercise() {
		if(cards.isEmpty()) return;
		clearScreen();
		waitForEnter("\n Review past words. Press Enter to continue\n");
		reviewCard();
	}

	private int getLimit() {
		int hour = LocalTime.now().getHour();
		return switch (hour) {
		case 8 -> 3;
		case 9 -> 5;
		case 10 -> 8;
		case 11 -> 6;
		case 12 -> 8;
		case 13 -> 6;
		case 14 -> 8;
		case 15 -> 6;
		case 16 -> 8;
		case 17 -> 6;
		case 18 -> 5;
		case 19 -> 4;
		default -> 0; 
		};
	}

	private void reviewCard() {
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

package com.wordflow.model;

import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import com.wordflow.repository.DictionaryRepository;

public class Dictionary {
	private List<FlashCard> dictionary;
	DictionaryRepository dictionaryRepository;
	
	public Dictionary () {
		this.dictionaryRepository = new DictionaryRepository();
		this.dictionary = dictionaryRepository.getDictionary();
	}
	
	public void updateDictionary() {
		dictionaryRepository.saveDictionary(dictionary);
	}
	
	public List<FlashCard> find(Predicate<FlashCard> filter) {
        return dictionary.stream()
            .filter(filter)
            .collect(Collectors.toList());
    }
	
	public int count(Predicate<FlashCard> condition) {
	    return (int) dictionary.stream()
	            .filter(condition)
	            .count();
	}
	
	public List<FlashCard> findByType(FlashCard.FlashCardType type) {
	    return find(card -> card.getType() == type);
	}
	
	public List<FlashCard> findNewCards(FlashCard.FlashCardType type) {
	    return find(card -> card.getType() == type && card.getProgress().isNew());
	}
	
	public List<FlashCard> findDueCards(FlashCard.FlashCardType type) {
	    return find(card -> card.getType() == type && card.getProgress().isDue());
	}


	@Override
	public String toString() {
		return "Dictionary 0\n" + dictionary;
	}
	
	
}

package com.wordflow.model;

import java.time.LocalDate;

public class FlashCard {
	private final String question;
	private final String answer;
	private final FlashCardType type;
	private Progress progress;
	
	public FlashCard(String question, String answer, FlashCardType type, Progress progress) {
		this.question = question;
		this.answer = answer;
		this.type = type;
		this.progress = progress;
	}

	public enum FlashCardType {
		WORD, EXAMPLE, ADJECTIVEARTICLE
	}
	
	public String getQuestion() {
		return question;
	}

	public String getAnswer() {
		return answer;
	}
	
	public FlashCardType getType() {
		return type;
	}
	
	public Progress getProgress() {
		return progress;
	}
	
	public boolean isWord() {
	    return type == FlashCardType.WORD;
	}

	public boolean isExample() {
	    return type == FlashCardType.EXAMPLE;
	}

	public boolean checkAnswer(String userAnswer) {
		return userAnswer.trim().equalsIgnoreCase(answer);
	}
	
	public LocalDate getNextRepeatDate() {
	    return progress != null ? progress.getNextRepeatDate().toLocalDate() : null;
	}
	
	public String getCleanAnswer() {
		String cleanAnswer;
		int firstIndex = answer.indexOf(" - ");
		cleanAnswer = firstIndex < 0 ? answer : answer.substring(0, firstIndex);
		return cleanAnswer.trim().toLowerCase();
	}
	
	public void toProgress(boolean toProgress) {
		if(toProgress) {
			progress.incReviewInterval();
		} else {
			progress.decReviewInterval();
		}
		progress.setLastReviewDate();
	}

	@Override
	public String toString() {
		return "\n question : " + question + "\n answer : " + answer + "\n type : " + type + "\n progress : " + progress;
	}
	
	
}

package com.wordflow.model;

import java.util.ArrayList;
import java.util.List;

import com.wordflow.exercise.Exercise;
import com.wordflow.exercise.NewExamplesExercise;
import com.wordflow.exercise.NewWordsExercise;
import com.wordflow.exercise.ReviewExamplesExercise;
import com.wordflow.exercise.ReviewWordsExercise;

public class Lesson {
	private List<Exercise> exercises;
	private final Dictionary dictionary;
	private List<Integer> passedCards;

	public Lesson() {
		this.dictionary = new Dictionary();
		this.exercises = generateExercises();
		this.passedCards = new ArrayList<>();
	}
	
	private List<Exercise> generateExercises() {
		return List.of(
				new NewWordsExercise(dictionary),
				new ReviewWordsExercise(dictionary),
				new NewExamplesExercise(dictionary),
				new ReviewExamplesExercise(dictionary)
				);
	}

	public void runLesson() {
		for(Exercise exercise : exercises) {
			exercise.runExercise();
		}
		dictionary.updateDictionary();
	}
}

package com.wordflow.model;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

import com.wordflow.config.AppDefaults;

public class Progress {
	private int successCount;
	private LocalDateTime lastReviewDate;
	
	public Progress() {
		this.successCount = 0;
		this.lastReviewDate = LocalDateTime.now();
	}
	
	public Progress(int successCount, LocalDateTime lastReviewDate) {
		this.successCount = successCount;
		this.lastReviewDate = lastReviewDate;
	}

	public int getReviewInterval() {
		return successCount;
	}

	public LocalDateTime getLastReviewDate() {
		return lastReviewDate;
	}

//	public void setReviewInterval() {
//		this.successCount = 1;
//	}

	public void setLastReviewDate() {
		lastReviewDate = LocalDateTime.now();
	}
	
	public void incReviewInterval() {
		if(successCount < 13) {
			successCount++;
		}
	}
	
	public void decReviewInterval() {
		successCount = successCount/2;
	}
	
	public boolean isNew() {
        return successCount == 0;
    }
	
	public boolean isDue() {
		long minsPassed = ChronoUnit.MINUTES.between(lastReviewDate, LocalDateTime.now());
		return minsPassed >= getTimeForNextReview(successCount) &&
				!isNew();
    }
	
	private int getTimeForNextReview(int interval) {
		final int baseInterval = AppDefaults.REVIEW_INTERVAL_IN_MINS;
		final int valueZero = 15;
		if (interval == 0) return valueZero;
	    if (interval == 1) return baseInterval / 2;

	    int previousValue = valueZero;
	    int currentValue = baseInterval / 2;
	    int nextValue = 0;

	    for (int index = 2; index <= interval; index++) {
	    	nextValue = previousValue + currentValue;
	        previousValue = currentValue;
	        currentValue = nextValue;
	    }
	    return currentValue;
	}
	
	public LocalDateTime getNextRepeatDate() {
	    if (isNew()) {
	        return null; // новая карточка — дата не назначена
	    }
	    int minutes = getTimeForNextReview(successCount);
	    return lastReviewDate.plusMinutes(minutes);
	}

	@Override
	public String toString() {
		return "Progress \n successCount : " + successCount + "\n lastReviewDate : " + lastReviewDate;
	}
	
	
}

package com.wordflow.repository;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Scanner;
import java.util.Set;

import com.wordflow.model.Dictionary;
import com.wordflow.model.FlashCard;
import com.wordflow.model.Progress;

public class DictionaryRepository {
	private List<FlashCard> flashCards;
	String inputFilePath = "dictionary.txt"; 

	public List<FlashCard> getDictionary() {
		flashCards = new ArrayList<>();
		try {
			List<String> lines = Files.readAllLines(Paths.get(inputFilePath));

			for (String line : lines) {
				String[] parts = line.split(";");
				flashCards.add(new FlashCard(parts[0], parts[1], 
						FlashCard.FlashCardType.valueOf(parts[2]),
						new Progress(Integer.parseInt(parts[3]), LocalDateTime.parse(parts[4]))));
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return flashCards; 
	}
	
	public void saveDictionary(List<FlashCard> cards) {
	    try (BufferedWriter writer = Files.newBufferedWriter(Paths.get(inputFilePath))) {
	        for (FlashCard card : cards) {
	            String line = String.join(";",
	                    card.getQuestion(),
	                    card.getAnswer(),
	                    card.getType().name(),
	                    String.valueOf(card.getProgress().getReviewInterval()),
	                    card.getProgress().getLastReviewDate().toString()
	            );
	            writer.write(line);
	            writer.newLine();
	        }
	    } catch (IOException e) {
	        e.printStackTrace();
	    }
	}
	
	public void importDictionaryCSV() {
		Scanner scanner = new Scanner(System.in);
        System.out.print("Enter name CSV file : ");
        String fileName = scanner.nextLine().trim();

        DictionaryRepository repository = new DictionaryRepository();
        repository.importFromCsv(fileName);
	}
	
	public void importFromCsv(String csvPath) {
	    List<FlashCard> allCards = getDictionary();
	    Set<String> existingKeys = getExistingKeys(allCards);

	    List<FlashCard> newCards = readNewCardsFromCsv(csvPath, existingKeys);

	    allCards.addAll(newCards);
	    saveDictionary(allCards);
	    System.out.println("Added new cards : " + newCards.size());
	}
	
	private Set<String> getExistingKeys(List<FlashCard> cards) {
	    Set<String> keys = new HashSet<>();
	    for (FlashCard card : cards) {
	        String key = getKey(card.getQuestion(), card.getType().name());
	        keys.add(key);
	    }
	    return keys;
	}

	private List<FlashCard> readNewCardsFromCsv(String csvPath, Set<String> existingKeys) {
	    List<FlashCard> newCards = new ArrayList<>();

	    try (BufferedReader reader = Files.newBufferedReader(Paths.get(csvPath))) {
	        String line;
	        while ((line = reader.readLine()) != null) {
	            if (line.trim().isEmpty() || line.startsWith("question")) continue;

	            String[] parts = line.split(";", -1);
	            if (parts.length < 3) continue;

	            String question = parts[0].trim(); // RU
	            String answer = parts[1].trim();   // DE
	            String typeStr = parts[2].trim().toUpperCase();

	            try {
	                FlashCard.FlashCardType type = FlashCard.FlashCardType.valueOf(typeStr);
	                String key = getKey(question, type.name());
	                if (!existingKeys.contains(key)) {
	                    FlashCard card = new FlashCard(question, answer, type, new Progress());
	                    newCards.add(card);
	                    existingKeys.add(key);
	                }
	            } catch (IllegalArgumentException e) {
	                System.err.println("❗ Unknown type: " + typeStr + " in row: " + line);
	            }
	        }
	    } catch (IOException e) {
	        e.printStackTrace();
	    }
	    return newCards;
	}

	private String getKey(String question, String type) {
	    return (question + ";" + type).toLowerCase();
	}


}

