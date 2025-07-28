package com.wordflow.model;

import static com.wordflow.ui.InteractionHandler.*;

import java.util.ArrayList;
import java.util.List;

import com.wordflow.exercise.Exercise;
import com.wordflow.exercise.NewExamplesExercise;
import com.wordflow.exercise.NewWordsExercise;
import com.wordflow.exercise.ReviewExamplesExercise;
import com.wordflow.exercise.ReviewWordsExercise;
import com.wordflow.repository.DictionaryRepository;
import com.wordflow.ui.statistics.CardSnapshot;

public class Lesson {
	private Dictionary dictionary;
	private List<Exercise> exercises;
	private List<CardSnapshot> snapShots;

	public Lesson() {
		this.dictionary = new Dictionary(new DictionaryRepository("dictionary.json"));
		this.exercises = generateExercises();
	}
	
	private List<Exercise> generateExercises() {
		return List.of(
				new NewExamplesExercise(dictionary),
				new ReviewExamplesExercise(dictionary),
				new NewWordsExercise(dictionary),
				new ReviewWordsExercise(dictionary)
////				new AdjectiveDeclensionExercise(dictionary)
				);
	}

	public void runLesson() {
		takeSnapshot();
		for(Exercise exercise : exercises) {
			exercise.runExercise();
		}
		lessonResults(); 
		dictionary.uploadCards();
	}
	
	private void takeSnapshot() {
	    snapShots = new ArrayList<>();
	    for (Exercise exercise : exercises) {
	        List<FlashCard> cards = exercise.getLessonContent();
	        for (FlashCard card : cards) {
	            if (exercise instanceof NewWordsExercise || exercise instanceof ReviewWordsExercise) {
	                snapShots.add(new CardSnapshot(
	                        card.getTranslateWord(),
	                        card.getDeWord(),
	                        card.getProgress().getSuccessCount(),
	                        card.getProgress().getMinsToRepeat(),
	                        card,
	                        null
	                ));
	            } else if (exercise instanceof NewExamplesExercise || exercise instanceof ReviewExamplesExercise) {
	                Example lessonExample = card.getExamples().stream()
	                        .filter(ex -> Boolean.TRUE.equals(ex.isActive()))
	                        .findFirst()
	                        .orElse(null);
	                if (lessonExample != null) {
	                    snapShots.add(new CardSnapshot(
	                            lessonExample.getTranslateExample(),
	                            lessonExample.getDeExample(),
	                            lessonExample.getProgress().getSuccessCount(),
	                            lessonExample.getProgress().getMinsToRepeat(),
	                            card,
	                            lessonExample
	                    ));
	                }
	            }
	        }
	    }
	}

	private void lessonResults() {
	    int deLength = 0;
	    int ruLength = 0;

	    for (CardSnapshot snapShot : snapShots) {
	        deLength = Math.max(deLength, snapShot.de.length());
	        ruLength = Math.max(ruLength, snapShot.ru.length());
	    }
	    
//	    заплатка
	    if(deLength == 0) deLength = 20;
	    if(ruLength == 0) ruLength = 20;
	    
	    displayMessage("\n                          * * * Lesson Results * * * \n");
	    String line = new String(new char[deLength + ruLength + 30]).replace('\0', '-');
	    displayMessage(line);
	    String format = "%-" + ruLength + "s   %-" + deLength + "s   %-12s   %-12s";
	    displayMessage(String.format(format, "RU", "DE", "Success", "Interval"));
	    line = new String(new char[deLength + ruLength + 30]).replace('\0', '-');
	    displayMessage(line);

	    for (CardSnapshot snapShot : snapShots) {
	        String ruAfter;
	        String deAfter;
	        int successAfter;
	        int intervalAfter;

	        if (snapShot.example != null) {
	            Example currentExample = snapShot.card.getExamples().stream()
	                    .filter(ex -> Boolean.TRUE.equals(ex.isActive()))
	                    .findFirst()
	                    .orElse(snapShot.example); // если нет активного — показываем старый

	            ruAfter = currentExample.getTranslateExample();
	            deAfter = currentExample.getDeExample();
	            successAfter = currentExample.getProgress().getSuccessCount();
	            intervalAfter = currentExample.getProgress().getMinsToRepeat();
	        } else {
	            ruAfter = snapShot.card.getTranslateWord();
	            deAfter = snapShot.card.getDeWord();
	            successAfter = snapShot.card.getProgress().getSuccessCount();
	            intervalAfter = snapShot.card.getProgress().getMinsToRepeat();
	        }
	        displayMessage(String.format(format,
	                ruAfter,
	                deAfter,
	                snapShot.success + " → " + successAfter,
	                snapShot.interval + " → " + intervalAfter
	        ));
	    }
	    waitForEnter("\nPress Enter to finish lesson ");
	}
}
