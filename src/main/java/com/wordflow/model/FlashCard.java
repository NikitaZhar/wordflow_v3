package com.wordflow.model;

import static com.wordflow.ui.InteractionHandler.waitForEnter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.wordflow.config.AppDefaults;

public class FlashCard {
	private String id;
	private String translateWord;
	private String deWord;
	private List<Example> examples;
	private FlashCardType exerciseType;
	private Progress progress;

	public FlashCard() {
		this.progress = new Progress(30);
		this.examples = new ArrayList<>();
	}

	public FlashCard(String deWord, String translateWord, FlashCardType exerciseType, Progress progress) {
		this.id = UUID.randomUUID().toString();
		this.deWord = deWord;
		this.translateWord = translateWord;
		this.exerciseType = exerciseType;
		this.progress = progress != null ? progress : new Progress(0);
		this.examples = new ArrayList<>();
	}

	public enum FlashCardType {
		VOCAB
		//		ADJECTDECLIN - склонения прилагательных
		//		, ADJECTDECLIN
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public void setDeWord(String deWord) {
		this.deWord = deWord;
	}

	public String getDeWord() {
		return deWord;
	}

	public void setTranslateWord(String translateWord) {
		this.translateWord = translateWord;
	}

	public String getTranslateWord() {
		return translateWord;
	}

	public void setProgress(Progress progress) {
		this.progress = progress;
	}

	public Progress getProgress() {
		return progress;
	}

	//  Не знаю зачем нужен метод
	//	public FlashCardType getType() {
	//		return FlashCardType.valueOf(exerciseType);
	//	}

	public FlashCardType getExerciseType() {
		return exerciseType;
	}

	public void setExerciseType(FlashCardType exerciseType) {
		this.exerciseType = exerciseType;
	}

	public List<Example> getExamples() {
		return examples;
	}

	public void setExamples(List<Example> examples) {
		this.examples = examples;
	}

	@JsonIgnore
	public LocalDateTime getNextRepeatTime() {
		if (progress == null || progress.getNextRepeatTime() == null) {
			return null;
		}
		return progress.getNextRepeatTime();
	}
	
	//	======================================================================
	//	Методы работы с примерами (большой вопрос - почему это надо делать тут!!!!!!!)

	@JsonIgnore
	public boolean hasActiveExample() {
//		крроткий вариант - надо проверить его работу
//		return examples != null && examples.stream().anyMatch(Example::isActive);
		
		if (examples == null || examples.isEmpty()) {
			return false;
		}
		for (Example example : examples) {
			if (example.isActive()) {
				return true;
			}
		}
		return false;
	}

	@JsonIgnore
	public Example getActiveExample() {
		return getExamples().stream()
				.filter(example -> Boolean.TRUE.equals(example.isActive()))
//				Еще один вариант оператора
//				.filter(Example::isActive)
				.findFirst()
				.orElseThrow(() -> new IllegalStateException("No active example in card."));
	}

	@JsonIgnore
	public int getActiveExampleIndex() {
		List<Example> examples = getExamples();
		for (int indexExample = 0; indexExample < examples.size(); indexExample++) {
			if (Boolean.TRUE.equals(examples.get(indexExample).isActive())) {
				return indexExample;
			}
		}
		throw new IllegalStateException("No active example in card.");
	}

	private void activateExample(List<Example> examples, int index) {
		Example example = examples.get(index);
		if (example.isActive() == null || !example.isActive()) {
			example.setActive(true);
//			waitForEnter("\n Start to learn example : " + example.getDeExample() + " Press Enter to begin ...\n");
		}
	}

	//	=========================================================================
	//	Методы получения и проверки ответов

	public boolean checkAnswer(String userAnswer) {
		return userAnswer.trim().equalsIgnoreCase(deWord);
	}

	@JsonIgnore
	public String getCleanAnswer() {
		String cleanAnswer;
		int firstIndex = deWord.indexOf(" - ");
		cleanAnswer = firstIndex < 0 ? deWord : deWord.substring(0, firstIndex);
		return cleanAnswer.trim().toLowerCase();
	}

	@JsonIgnore
	public String getExercisePrompt() {
		Matcher matcher = Pattern.compile("_(.*?)_").matcher(deWord);
		if (matcher.find()) {
			String blanks = Arrays.stream(matcher.group(1).trim().split("\\s+"))
					.map(w -> "___")
					.collect(Collectors.joining(" "));
			return new StringBuilder(deWord)
					.replace(matcher.start(), matcher.end(), blanks + " ")
					.toString()
					.trim();
		}
		return deWord;
	}

	@JsonIgnore
	public String getExerciseAnswer() {
		var matcher = java.util.regex.Pattern.compile("_(.+?)_").matcher(deWord);
		return matcher.find() ? matcher.group(1) : "";
	}

	@JsonIgnore
	public String getExerciseFullText() {
		return deWord.replace("_", " ").stripLeading();
	}

	//	Изменение прогресса.Включение первого примера
	//	Это надо переносить в Progress и Example
	public void registerWordProgress(boolean reviewSuccess) {
		if(reviewSuccess) {
			if(progress.getMinsToRepeat() <= AppDefaults.MAX_INTERVAL_FOR_WORDS) {
				progress.incReviewInterval();
			}
		} else {
			progress.decReviewInterval();
		}
		progress.setLastReviewTime();
		progress.setIntervalToRepeat();

		//		Вопрос (пока нет ответа) - если счетчик снижается ниже 400, надо ли выключать примеры???
		if (progress.getMinsToRepeat() >= AppDefaults.THRESHHOLD_TO_START_EXAMPLES) {
			if (examples != null && !examples.isEmpty() && !hasActiveExample()) {
				activateExample(examples, 0);
			}
		}
	}

	@Override
	public boolean equals(Object o) {
		if (!(o instanceof FlashCard other)) return false;
		return deWord.equalsIgnoreCase(other.deWord) &&
				translateWord.equalsIgnoreCase(other.translateWord);
	}

	@Override
	public String toString() {
		return 
				"id : " + id + '\n' +
				"deWord : " + deWord + '\n' +
				"translateWord : " + translateWord + '\n' +
				"progress : " + progress + "\n" +
				"exerciseType : " + exerciseType + '\n' +
				"examples : " + examples
				;
	}
}
