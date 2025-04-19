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
