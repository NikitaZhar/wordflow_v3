package com.wordflow.model;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

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
		//		WORD - новые слова
		//		EXAMPLE - примеры использования слов
		//		ADJECTDECLIN - склонения прилагательных
		WORD, EXAMPLE, ADJECTDECLIN
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

	public String getExercisePrompt() {
		Matcher matcher = Pattern.compile("_(.*?)_").matcher(answer);
	    if (matcher.find()) {
	        String blanks = Arrays.stream(matcher.group(1).trim().split("\\s+"))
	                              .map(w -> "___")
	                              .collect(Collectors.joining(" "));
	        return new StringBuilder(answer)
	                .replace(matcher.start(), matcher.end(), blanks + " ")
	                .toString()
	                .trim();
	    }
	    return answer;
	}

	public String getExerciseAnswer() {
		var matcher = java.util.regex.Pattern.compile("_(.+?)_").matcher(answer);
		return matcher.find() ? matcher.group(1) : "";
	}

	public String getExerciseFullText() {
		return answer.replace("_", " ").stripLeading();
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

import java.util.ArrayList;
import java.util.List;

import com.wordflow.exercise.AdjectiveDeclensionExercise;
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
				new ReviewExamplesExercise(dictionary),
				new AdjectiveDeclensionExercise(dictionary)
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

