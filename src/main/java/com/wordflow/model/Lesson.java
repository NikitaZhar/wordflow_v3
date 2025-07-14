package com.wordflow.model;

import static com.wordflow.ui.InteractionHandler.*;

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
				new NewWordsExercise(dictionary),
				new ReviewWordsExercise(dictionary),
				new NewExamplesExercise(dictionary),
				new ReviewExamplesExercise(dictionary)
////				new AdjectiveDeclensionExercise(dictionary)
				);
	}

	public void runLesson() {
//		lessonContent();
		takeSnapshot();
		for(Exercise exercise : exercises) {
			exercise.runExercise();
		}
		lessonResults(); 
		dictionary.uploadCards();
	}
	
	private void takeSnapshot() {
	    snapShots = new java.util.ArrayList<>();

	    for (Exercise exercise : exercises) {
	        List<FlashCard> cards = exercise.getLessonContent();
	        for (FlashCard card : cards) {
	            if (exercise instanceof NewWordsExercise || exercise instanceof ReviewWordsExercise) {
	                snapShots.add(new CardSnapshot(
	                        card.getTranslateWord(),
	                        card.getDeWord(),
	                        card.getProgress().getSuccessCount(),
	                        card.getProgress().getMinsToRepeat(),
	                        card
	                ));
	            } else if (exercise instanceof NewExamplesExercise || exercise instanceof ReviewExamplesExercise) {
	                Example activeExample = card.getExamples().stream()
	                        .filter(ex -> Boolean.TRUE.equals(ex.getActive()))
	                        .findFirst()
	                        .orElse(null);
	                if (activeExample != null) {
	                    snapShots.add(new CardSnapshot(
	                            activeExample.getTranslateExample(),
	                            activeExample.getDeExample(),
	                            activeExample.getProgress().getSuccessCount(),
	                            activeExample.getProgress().getMinsToRepeat(),
	                            card
	                    ));
	                }
	            }
	        }
	    }
	}

	private void lessonResults() {
		int deLength = 0;
		int translateLength = 0;

	    for(CardSnapshot snapShot : snapShots) {
	    	if(snapShot.de.length() > deLength) deLength = snapShot.de.length();
	    	if(snapShot.ru.length() > translateLength) translateLength = snapShot.ru.length();
	    }
	    
	    displayMessage("\n                          * * * Lesson Results * * * \n");
	    
	    String format = "%-" + translateLength + "s   %-" + deLength + "s   %-12s   %-12s";
	    displayMessage(String.format(format, "RU", "DE", "Success", "Interval"));
	    displayMessage("-".repeat(deLength + translateLength + 29));
	    
	    for(CardSnapshot snapShot : snapShots) {
	        int successAfter;
	        int intervalAfter;

	        if (snapShot.card.getExamples().isEmpty() || snapShot.ru.equals(snapShot.card.getTranslateWord())) {
	            // Word case
	            successAfter = snapShot.card.getProgress().getSuccessCount();
	            intervalAfter = snapShot.card.getProgress().getMinsToRepeat();
	        } else {
	            // Example case
	            Example activeExample = snapShot.card.getExamples().stream()
	                    .filter(ex -> Boolean.TRUE.equals(ex.getActive()))
	                    .findFirst()
	                    .orElse(null);
	            if (activeExample != null) {
	                successAfter = activeExample.getProgress().getSuccessCount();
	                intervalAfter = activeExample.getProgress().getMinsToRepeat();
	            } else {
	                successAfter = -1;
	                intervalAfter = -1;
	            }
	        }

	        String successStr = snapShot.successBefore + " → " + successAfter;
	        String intervalStr = snapShot.intervalBefore + " → " + intervalAfter;

	        displayMessage(String.format(format,
	        		snapShot.ru,
	        		snapShot.de,
	                successStr,
	                intervalStr
	        ));
	    }
	    waitForEnter("\nPress Enter to finish lesson ");
	}

//	private String getPrintableExerciseName(String className) {
//		return switch (className) {
//		case "NewWordsExercise" -> "New words to learn";
//		case "ReviewWordsExercise" -> "Words to review";
//		case "NewExamplesExercise" -> "New examples to learn";
//		case "ReviewExamplesExercise" -> "Examples to review";
//		default -> "Unknown exercise";
//		};
//	}
//	
//	private void lessonContent() {
//	    displayMessage("\n            * * * Lesson Content * * * \n");
//	    for (Exercise exercise : exercises) {
//	        List<FlashCard> cards = exercise.getLessonContent();
//	        if(cards.size() != 0) {
//	        	displayMessage("  " + getPrintableExerciseName(exercise.getClass().getSimpleName()) + " (" + cards.size() + "):");
//	        	for (FlashCard card : cards) {
//	        		if (exercise instanceof NewWordsExercise || exercise instanceof ReviewWordsExercise) {
//	        			displayMessage("   RU: " + card.getTranslateWord() + " | DE: " + card.getDeWord()
//	        			+ "  До урока : " + card.getProgress().getSuccessCount()
//	        			+ "  Интервал до урока : " + card.getProgress().getMinsToRepeat()
//	        					);
//	        		} else if (exercise instanceof NewExamplesExercise || exercise instanceof ReviewExamplesExercise) {
//	        			Example activeExample = card.getExamples().stream()
//	        					.filter(ex -> Boolean.TRUE.equals(ex.getActive()))
//	        					.findFirst()
//	        					.orElse(null);
//	        			if (activeExample != null) {
//	        				displayMessage("   RU: " + activeExample.getTranslateExample() +
//	        						" | DE: " + activeExample.getDeExample()
//	        						+ "  До урока : " + activeExample.getProgress().getSuccessCount()
//	        	        			+ "  Интервал до урока : " + activeExample.getProgress().getMinsToRepeat()
//	        						);
//	        			} else {
//	        				displayMessage("  ️ Нет активного примера у слова: " + card.getTranslateWord() +
//	        						" | " + card.getDeWord());
//	        			}
//	        		} else {
//	        			displayMessage("  ️ Неизвестный тип упражнения для: " + card.getTranslateWord() +
//	        					" | " + card.getDeWord());
//	        		}
//	        	}
//	        	displayMessage("");
//	        }
//	    }
//	    waitForEnter("Press Enter to start lesson ");
//	    clearScreen();
//	}
}
