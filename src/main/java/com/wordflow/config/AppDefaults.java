package com.wordflow.config;

public class AppDefaults {
	private AppDefaults() {
	}
	
	public static final int REVIEW_INTERVAL_IN_MINS = 30;
	public static final Language LANGUAGE = Language.GER;
	public static final int THRESHHOLD_TO_START_EXAMPLES = 400;
	public static final int MAX_INTERVAL_FOR_WORDS = 1440;
	public static final int THRESHHOLD_FOR_NEXT_EXAMPLE = 500;
	
//	Параметры для расчета интервала повторения
//	=====================================================
//	достичь интервала 2–3 дня → увеличиваем `MAX_INTERVAL`
//	сделать медленное начало → снизить `K` до 5.5–5.8
//	ускоряет рост в конце → увеличить BETA до 2.7+
	
	public static final double INTERVAL_K = 6.82;
    public static final double INTERVAL_ALPHA = 1.0;
    public static final double INTERVAL_BETA = 2.14;
    public static final int INTERVAL_MIN_INTERVAL = 15;     // minutes
    public static final int INTERVAL_MAX_INTERVAL = 1440;
	
	public enum Language {
		GER, ENG, SLK
	}
}
