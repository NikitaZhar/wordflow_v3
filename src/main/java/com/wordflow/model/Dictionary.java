package com.wordflow.model;

import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import com.wordflow.repository.DictionaryRepository;

public class Dictionary {
	private List<FlashCard> dictionary;
	DictionaryRepository dictionaryRepository;
	
	public Dictionary () {
		this.dictionaryRepository = new DictionaryRepository();
		this.dictionary = dictionaryRepository.getDictionary();
	}
	
	public void updateDictionary() {
		dictionaryRepository.saveDictionary(dictionary);
	}
	
	public List<FlashCard> find(Predicate<FlashCard> filter) {
        return dictionary.stream()
            .filter(filter)
            .collect(Collectors.toList());
    }
	
	public int count(Predicate<FlashCard> condition) {
	    return (int) dictionary.stream()
	            .filter(condition)
	            .count();
	}
	
	public List<FlashCard> findByType(FlashCard.FlashCardType type) {
	    return find(card -> card.getType() == type);
	}
	
	public List<FlashCard> findNewCards(FlashCard.FlashCardType type) {
	    return find(card -> card.getType() == type && card.getProgress().isNew());
	}
	
	public List<FlashCard> findDueCards(FlashCard.FlashCardType type) {
	    return find(card -> card.getType() == type && card.getProgress().isDue());
	}


	@Override
	public String toString() {
		return "Dictionary 0\n" + dictionary;
	}
	
	
}
