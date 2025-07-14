package com.wordflow.model;

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

	public void setTranslateWord(String translateWord) {
		this.translateWord = translateWord;
	}

	public void setProgress(Progress progress) {
		this.progress = progress;
	}

	public String getTranslateWord() {
		return translateWord;
	}

	public String getDeWord() {
		return deWord;
	}

//  Не знаю зачем нужен метод
//	public FlashCardType getType() {
//		return FlashCardType.valueOf(exerciseType);
//	}

	public FlashCardType getExerciseType() {
	    return exerciseType;
	}

	public Progress getProgress() {
		return progress;
	}
	
	public List<Example> getExamples() {
		return examples;
	}


	public void setExamples(List<Example> examples) {
		this.examples = examples;
	}

	public void setExerciseType(FlashCardType exerciseType) {
	    this.exerciseType = exerciseType;
	}

	public boolean checkAnswer(String userAnswer) {
		return userAnswer.trim().equalsIgnoreCase(deWord);
	}

	@JsonIgnore
	public LocalDateTime getNextRepeatTime() {
	    if (progress == null || progress.getNextRepeatTime() == null) {
	        return null;
	    }
	    return progress.getNextRepeatTime();
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
		
//		Переделать - не оптимально. Проверки идут каждый раз при увеличении счетчика, а должно быть только один раз
//		Второй вопрос (пока нет ответа) - если счетчик снижается ниже 400, надо ли выключать примеры???
		
		if (progress.getMinsToRepeat() >= AppDefaults.THRESHHOLD_TO_START_EXAMPLES) {
	        if (examples != null && !examples.isEmpty()) {
	            Example firstExample = examples.get(0);
	            if (firstExample.getActive() == null || !firstExample.getActive()) {
	                firstExample.setActive(true);
	            }
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
