package com.wordflow.ui.statistics;

import com.wordflow.model.Example;
import com.wordflow.model.FlashCard;

public class CardSnapshot {
    public String ru;
    public String de;
    public int success;
    public int interval;
    public FlashCard card;
    public Example example;

    public CardSnapshot(String ru, String de, int success, int interval, FlashCard card, Example example) {
        this.ru = ru;
        this.de = de;
        this.success = success;
        this.interval = interval;
        this.card = card;
        this.example = example;
    }
}
