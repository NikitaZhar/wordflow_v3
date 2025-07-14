package com.wordflow.ui.statistics;

import com.wordflow.model.FlashCard;

public class CardSnapshot {
    public String ru;
    public String de;
    public int successBefore;
    public int intervalBefore;
    public FlashCard card;

    public CardSnapshot(String ru, String de, int successBefore, int intervalBefore, FlashCard card) {
        this.ru = ru;
        this.de = de;
        this.successBefore = successBefore;
        this.intervalBefore = intervalBefore;
        this.card = card;
    }
    
//    public FlashCard getCard() {
//    	return card;
//    }
}
