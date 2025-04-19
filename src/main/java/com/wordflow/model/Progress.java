package com.wordflow.model;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

import com.wordflow.config.AppDefaults;

public class Progress {
	private int successCount;
	private LocalDateTime lastReviewDate;
	
	public Progress() {
		this.successCount = 0;
		this.lastReviewDate = LocalDateTime.now();
	}
	
	public Progress(int successCount, LocalDateTime lastReviewDate) {
		this.successCount = successCount;
		this.lastReviewDate = lastReviewDate;
	}

	public int getReviewInterval() {
		return successCount;
	}

	public LocalDateTime getLastReviewDate() {
		return lastReviewDate;
	}

//	public void setReviewInterval() {
//		this.successCount = 1;
//	}

	public void setLastReviewDate() {
		lastReviewDate = LocalDateTime.now();
	}
	
	public void incReviewInterval() {
		if(successCount < 13) {
			successCount++;
		}
	}
	
	public void decReviewInterval() {
		successCount = successCount/2;
	}
	
	public boolean isNew() {
        return successCount == 0;
    }
	
	public boolean isDue() {
		long minsPassed = ChronoUnit.MINUTES.between(lastReviewDate, LocalDateTime.now());
		return minsPassed >= getTimeForNextReview(successCount) &&
				!isNew();
    }
	
	private int getTimeForNextReview(int interval) {
		final int baseInterval = AppDefaults.REVIEW_INTERVAL_IN_MINS;
		final int valueZero = 15;
		if (interval == 0) return valueZero;
	    if (interval == 1) return baseInterval / 2;

	    int previousValue = valueZero;
	    int currentValue = baseInterval / 2;
	    int nextValue = 0;

	    for (int index = 2; index <= interval; index++) {
	    	nextValue = previousValue + currentValue;
	        previousValue = currentValue;
	        currentValue = nextValue;
	    }
	    return currentValue;
	}
	
	public LocalDateTime getNextRepeatDate() {
	    if (isNew()) {
	        return null; // новая карточка — дата не назначена
	    }
	    int minutes = getTimeForNextReview(successCount);
	    return lastReviewDate.plusMinutes(minutes);
	}

	@Override
	public String toString() {
		return "Progress \n successCount : " + successCount + "\n lastReviewDate : " + lastReviewDate;
	}
	
	
}
