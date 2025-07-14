package com.wordflow.model;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.wordflow.config.AppDefaults;

public class Progress {
	private int successCount;
	private LocalDateTime lastReviewTime;
	private int minsToRepeat;
	
	public Progress() {}
	
	public Progress(int interval) {
		this.successCount = 0;
		this.lastReviewTime = LocalDateTime.now();
		this.minsToRepeat = interval;
	}
	
	public Progress(int successCount, LocalDateTime lastReviewDate) {
		this.successCount = successCount;
		this.lastReviewTime = lastReviewDate;
	}
	
	public LocalDateTime getLastReviewTime() {
		return lastReviewTime;
	}

	public void setLastReviewTime() {
		this.lastReviewTime = LocalDateTime.now();
	}

	public int getMinsToRepeat() {
		return minsToRepeat;
	}

//	public void setIntervalToRepeat(int intervalToRepeat) {
//		this.minsToRepeat = intervalToRepeat;
//	}

	public int getSuccessCount() {
		return successCount;
	}

	public void setSuccessCount(int successCount) {
		this.successCount = successCount;
	}

	public void incReviewInterval() {
		successCount++;
	}
	
//	public void incrementSuccessCount() {
//	    incReviewInterval();
//	}
	
	public void decReviewInterval() {
		this.successCount = successCount/2;
	}
	
	public int calculateIntervalMinutes(int successRepetition) {
		double raw = AppDefaults.INTERVAL_K *
				Math.pow(successRepetition + AppDefaults.INTERVAL_ALPHA, AppDefaults.INTERVAL_BETA);
		return (int) Math.max(
				AppDefaults.INTERVAL_MIN_INTERVAL,
				Math.min(AppDefaults.INTERVAL_MAX_INTERVAL, Math.round(raw))
				);
	}
	
	public void setIntervalToRepeat() {
		this.minsToRepeat = calculateIntervalMinutes(successCount);
	}
	
	@JsonIgnore
	public boolean isNew() {
        return successCount == 0;
    }
	
	@JsonIgnore
	public boolean isDue() {
		if (isNew()) return false;
		long minsPassed = ChronoUnit.MINUTES.between(lastReviewTime, LocalDateTime.now());
		
//		надо подумать как сделать, если перерыв был большой
//		if(minsPassed > minsToRepeat * 2) decReviewInterval();
		
		return minsPassed >= calculateIntervalMinutes(successCount);
    }
	
	@JsonIgnore
	public LocalDateTime getNextRepeatTime() {
	    if (isNew()) {
	        return null;
	    }
	    return lastReviewTime.plusMinutes(minsToRepeat);
	}

	@Override
	public String toString() {
		return "\n * Progress * " + "\nsuccessCount : " + successCount + 
				"\nlastReviewDate : " + lastReviewTime +
				"\nintervalToRepeat : " + minsToRepeat;
	}
	
	
}
