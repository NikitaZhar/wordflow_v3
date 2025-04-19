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
