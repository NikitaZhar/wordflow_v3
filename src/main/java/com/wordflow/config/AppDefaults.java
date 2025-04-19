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
