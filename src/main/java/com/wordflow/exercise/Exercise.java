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
