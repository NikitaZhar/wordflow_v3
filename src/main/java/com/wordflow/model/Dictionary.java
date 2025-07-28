package com.wordflow.model;

import static com.wordflow.ui.InteractionHandler.GREEN;
import static com.wordflow.ui.InteractionHandler.RESET;
import static com.wordflow.ui.InteractionHandler.RED;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import com.wordflow.repository.DictionaryRepository;
import com.wordflow.ui.InteractionHandler;

public class Dictionary {
	private final DictionaryRepository repository;
	private List<FlashCard> flashCards;

	public Dictionary(DictionaryRepository repository) {
		this.repository = repository;
		this.flashCards = getCards();
	}

	public List<FlashCard> getCards() {
	    List<FlashCard> loadedCards = repository.loadDictionaryFromFile();

	    for (FlashCard card : loadedCards) {
	        if (card.getProgress() == null) {
	            card.setProgress(new Progress());
	        }
	        if (card.getExamples() != null) {
	            for (Example example : card.getExamples()) {
	                if (example.getProgress() == null) {
	                    example.setProgress(new Progress());
	                }
	            }
	        }
	    }
	    return loadedCards;
	}

	public void uploadCards() {
		repository.saveDictionaryToFile(this.flashCards);
		this.flashCards = getCards();
	}

	public List<FlashCard> getAllCards() {
		return new ArrayList<>(flashCards);
	}

//	private boolean isDuplicate(FlashCard newCard) {
//		return flashCards.stream()
//				.anyMatch(existing -> existing.equals(newCard));
//	}

	public List<FlashCard> find(Predicate<FlashCard> condition) {
		return flashCards.stream()
				.filter(condition)
				.collect(Collectors.toList());
	}

	public List<FlashCard> findNewCards(FlashCard.FlashCardType type) {
		return find(card -> 
		card.getExerciseType() == type && 
		card.getProgress() != null && 
		card.getProgress().isNew()
				);
	}
	
	public long count(Predicate<FlashCard> filter) {
	    return flashCards.stream()
	            .filter(filter)
	            .count();
	}

	public List<FlashCard> findDueCards() {
	    LocalDateTime now = LocalDateTime.now();
	    return find(card -> {
	        LocalDateTime next = card.getNextRepeatTime();
	        return next != null && !next.isAfter(now);
	    });
	}
	
	public void addNewWordsToDictionary() {
	    List<FlashCard> newCards = repository.readNewWords();
	    List<FlashCard> cardsToAdd = new ArrayList<>();

	    for (FlashCard newCard : newCards) {
	        Optional<FlashCard> existingCardOpt = flashCards.stream()
	                .filter(card -> card.getDeWord().equalsIgnoreCase(newCard.getDeWord()))
	                .findFirst();

	        if (existingCardOpt.isPresent()) {
	            String prompt = "\nWord '" + newCard.getDeWord() + "' already exists in the dictionary.\n"
	                    + "RU (existing): " + existingCardOpt.get().getTranslateWord() + "\n"
	                    + "RU (new): " + newCard.getTranslateWord() + "\n"
	                    + "Is this a duplicate and should be skipped?";
	            boolean isDuplicate = InteractionHandler.getYesNoUserInputConsole(prompt);
	            if (isDuplicate) {
	                continue;
	            }
	        }
	        newCard.setId(UUID.randomUUID().toString());
	        newCard.setProgress(new Progress(0));
	        cardsToAdd.add(newCard);
	    }

	    if (!cardsToAdd.isEmpty()) {
	        flashCards.addAll(cardsToAdd);
	        uploadCards();
	        InteractionHandler.waitForEnter("\n " + GREEN + " ✔ " + RESET+ " Successfully imported " + cardsToAdd.size() + " new cards. Press Enter to continue ... ");
	    } else {
	        InteractionHandler.waitForEnter("\n " + RED + " ✘ " + RESET + "️ No new cards were added. Press Enter to continue ... ");
	    }
	}

//	public void editWordInDictionary() {
//	    Optional<FlashCard> updatedCardOpt = repository.manageEditWord();
//
//	    if (updatedCardOpt.isPresent()) {
//	        FlashCard updatedCard = updatedCardOpt.get();
//	        String cardId = updatedCard.getId();
//
//	        Optional<FlashCard> existingCardOpt = flashCards.stream()
//	                .filter(card -> card.getId().equals(cardId))
//	                .findFirst();
//
//	        if (existingCardOpt.isPresent()) {
//	            FlashCard existingCard = existingCardOpt.get();
//	            existingCard.setDeWord(updatedCard.getDeWord());
//	            existingCard.setTranslateWord(updatedCard.getTranslateWord());
//	            existingCard.setExamples(updatedCard.getExamples());
//	            uploadCards();
//	            InteractionHandler.waitForEnter(
//	                "\n Successfully updated the card with ID: " + cardId + ". Press Enter to continue ..."
//	            );
//	        } else {
//	            InteractionHandler.waitForEnter(
//	                "\n No card found with ID: " + cardId + ". Press Enter to continue ..."
//	            );
//	        }
//	    } else {
//	        InteractionHandler.waitForEnter(
//	            "\n Edit cancelled or no file provided. Press Enter to continue ..."
//	        );
//	    }
//	}
	
	public FlashCard findFlashCardById(String id) {
	    return flashCards.stream()
	            .filter(card -> card.getId().equals(id))
	            .findFirst()
	            .orElse(null);
	}
	
	public List<FlashCard> findFlashCardByWord(String word) {
		if (word == null) return List.of();
		return flashCards.stream()
	            .filter(card -> {
	                String deWord = card.getDeWord();
	                return deWord != null && deWord.toLowerCase(Locale.ROOT).contains(word);
	            })
	            .toList();
	}

	public void checkDictionary() {
		InteractionHandler.waitForEnter("\n Press Enter to read File ... ");
		for (FlashCard card : flashCards) {
			System.out.println(card);
		}
		InteractionHandler.waitForEnter("\n Press Enter to save File without changes ... ");
		uploadCards();
		InteractionHandler.waitForEnter("\n Press Enter to read new File (after saving)... ");

		List<FlashCard> reloadedCards = getCards();
		for (FlashCard card : reloadedCards) {
			System.out.println(card);
		}
	}
}