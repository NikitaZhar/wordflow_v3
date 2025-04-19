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
	private static final String EXERCISE_PROMPT =
			WHITE + " DE : " + RESET + "%s" + " >";
	
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
	
	public void displayFullExercise(FlashCard card) {
		waitForEnter(String.format(FULL_CARD_TEMPLATE, card.getQuestion(), card.getExerciseFullText()));
	}
	
	public String getUserAnswerWithPrompt() {
		return getStringInput(String.format(REVIEW_PROMPT));
	}
	
	public String getUserAnswerWithPromptExercise(String prompt) {
		return getStringInput(String.format(EXERCISE_PROMPT, prompt));
	}
	
	public void messageCorrectAnswer() {
		waitForEnter("\n" + GREEN + " ✔ " + RESET+ "Correct. Press Enter to continue\n");
	}
	
	public void messageErrorAnswer() {
		waitForEnter("\n" + RED + " ✘ " + RESET + "WRONG. Press Enter to repeat\n");
	}
	
	
}
