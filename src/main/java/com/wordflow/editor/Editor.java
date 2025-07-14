package com.wordflow.editor;

import com.wordflow.model.FlashCard;
import com.wordflow.model.Dictionary;
import com.wordflow.repository.DictionaryRepository;
import com.wordflow.ui.InteractionHandler;
import com.wordflow.ui.menu.EditorMenuController;
import com.wordflow.ui.menu.EditorMenuDisplay;

import java.util.List;

public class Editor {
	private List<FlashCard> flashCards;
	int indexDisplay = 0;
	boolean continueEdit = true;

	public Editor(DictionaryRepository repository) {
		this.flashCards = new Dictionary(repository).getCards();
	}

	public void runEditor() {
	    while (continueEdit) {
	        InteractionHandler.clearScreen();
	        displayHeader();
	        displayListWords();

	        indexDisplay += 15;
	        if (indexDisplay >= flashCards.size()) {
	            continueEdit = false;
	        } else {
	            InteractionHandler.waitForEnter("\nPress Enter to show next 15 words...");
	        }
	    }
	    InteractionHandler.waitForEnter("\nAll words displayed. Press Enter to return to menu.");
	}

	private void displayHeader() {
		StringBuilder builder = new StringBuilder();
		String headerFormat = "    %-4s   %-50s      %-55s      %-12s%n";
		builder.append(String.format(headerFormat, "#", "DE Word", "RU Translation", "Interval"));
		builder.append("    ").append("-".repeat(127)).append("\n");
		InteractionHandler.displayMessage(builder.toString());
	}

	private void displayListWords() {
		StringBuilder builder = new StringBuilder();
		String rowFormat =    "    %-4d   %-50s      %-55s      %-12s%n";
		int limit = Math.min(15, flashCards.size() - indexDisplay);
		for (int index = 0; index < limit; index++) {
			FlashCard card = flashCards.get(index + indexDisplay);
			builder.append(String.format(
					rowFormat,
					index + indexDisplay + 1,
					card.getDeWord(),
					card.getTranslateWord(),
					card.getProgress().getMinsToRepeat() + " mins"
					));
		}
		InteractionHandler.displayMessage(builder.toString());
	}
}
