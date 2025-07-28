package com.wordflow.editor;

import static com.wordflow.ui.InteractionHandler.*;

import java.util.List;

import com.wordflow.model.Dictionary;
import com.wordflow.model.Example;
import com.wordflow.model.FlashCard;
import com.wordflow.repository.DictionaryRepository;
import com.wordflow.ui.InteractionHandler;

public class EditWord {
	public static final String FULL_CARD_TEMPLATE =
			GREEN + " RU : " + RESET + "%s \n" +
			WHITE + " DE : " + RESET + "%s \n";
	
	private Dictionary dictionary;

	public EditWord(DictionaryRepository repository) {
		this.dictionary = new Dictionary(repository);
	}

	public void runFlashCardEditor() {
		List<FlashCard> cards = getWord();
		if (cards == null || cards.isEmpty()) {
			waitForEnter("\n No Flash cards to edit");
			return;
		}
		FlashCard cardToEdit = selectFlashCard(cards);
		displayMessage("\n");
		displayMessage(String.format(FULL_CARD_TEMPLATE,
				cardToEdit.getTranslateWord(),
				cardToEdit.getDeWord()) + "\n");
		
		boolean hasExample = cardToEdit.hasActiveExample();
	    Example activeExample = hasExample ? cardToEdit.getActiveExample() : null;

	    if (hasExample) {
	        displayMessage(String.format(FULL_CARD_TEMPLATE,
	                activeExample.getTranslateExample(),
	                activeExample.getDeExample()) + "\n");
	    }
	    
	    String oldDeWord  = cardToEdit.getDeWord();
	    String oldRuWord  = cardToEdit.getTranslateWord();
	    String oldDeEx    = hasExample ? activeExample.getDeExample() : null;
	    String oldRuEx    = hasExample ? activeExample.getTranslateExample() : null;

	    String inputRuWord = getAnyStringInput(" New " + GREEN + "RU" + RESET + " word : ");
	    String newRuWord = inputRuWord == null || inputRuWord.trim().isEmpty() ? oldRuWord : inputRuWord.trim();

	    String inputDeWord = getAnyStringInput(" New " + WHITE + "DE" + RESET + " word : ");
	    String newDeWord = inputDeWord == null || inputDeWord.trim().isEmpty() ? oldDeWord : inputDeWord.trim();

	    String newDeEx = oldDeEx;
	    String newRuEx = oldRuEx;
	    
	    if (hasExample) {
	    	String inputRuEx = getAnyStringInput(" New " + GREEN + "RU" + RESET + " example : ");
	    	newRuEx = inputRuEx == null || inputRuEx.trim().isEmpty() ? oldRuEx : inputRuEx.trim();

	    	String inputDeEx = getAnyStringInput(" New " + WHITE + "DE" + RESET + " example : ");
	        newDeEx = inputDeEx == null || inputDeEx.trim().isEmpty() ? oldDeEx : inputDeEx.trim();
	    }
	    
	    displayMessage("\n Changes:");
	    displayDiff(GREEN + " RU : " + RESET, oldRuWord, newRuWord);
	    displayDiff(WHITE + " DE : " + RESET, oldDeWord, newDeWord);
	    if (hasExample) {
	    	displayDiff(GREEN + " RU : " + RESET, oldRuEx, newRuEx);
	        displayDiff(WHITE + " DE : " + RESET, oldDeEx, newDeEx);
	    }
	    
	    boolean confirm = getYesNoUserInputConsole("\n *** Confirm? *** : ");
	    if (!confirm) {
	        displayMessage("Changes discard");
	        return;
	    }
	    
	    cardToEdit.setDeWord(newDeWord);
	    cardToEdit.setTranslateWord(newRuWord);
	    if (hasExample) {
	        activeExample.setDeExample(newDeEx);
	        activeExample.setTranslateExample(newRuEx);
	    }
	    dictionary.uploadCards();
	}
	
	private void displayDiff(String label, String oldVal, String newVal) {
	    String o = oldVal == null ? "(null)" : oldVal;
	    String n = newVal == null ? "(null)" : newVal;

	    if (o.equals(n)) {
	        displayMessage(String.format("%s%s ", label, o));
	    } else {
	        displayMessage(String.format("%s%s  ->  %s", label, o, n));
	    }
	}


	private FlashCard selectFlashCard(List<FlashCard> cards) {
		if (cards.size() == 1) {
			return cards.get(0);
		} 
		int deLength = 0;
		int ruLength = 0;

		InteractionHandler.displayMessage("\n There are a few FlashCards \n");
		for (FlashCard card : cards) {
			deLength = Math.max(deLength, card.getDeWord().length());
			ruLength = Math.max(ruLength, card.getTranslateWord().length());
		}
		for (int index = 0; index < cards.size(); index++) {
			String format = "%3d. %-" + ruLength + "s   %-" + deLength + "s";

			displayMessage(String.format(format, index + 1,
					cards.get(index).getTranslateWord(),
					cards.get(index).getDeWord()
					));
		}

		int choice;
		while (true) {
			choice = getIntInputConsole("\n Select word : ");
			if (choice >= 1 && choice <= cards.size()) {
				break;
			}
			waitForEnter("\n Wrong number. Press Enter to try again. ");
		}

		return cards.get(choice - 1);
	}

	private List<FlashCard> getWord() {
		String word = InteractionHandler.getStringInput("\n Enter word to edit : ");
		if (word == null || word.trim().isEmpty()) return List.of();
		return dictionary.findFlashCardByWord(word.trim());
	}
}
