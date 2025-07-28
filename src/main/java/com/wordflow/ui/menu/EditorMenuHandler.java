package com.wordflow.ui.menu;
import com.wordflow.editor.EditWord;
import com.wordflow.editor.Editor;
import com.wordflow.model.Dictionary;
import com.wordflow.repository.DictionaryRepository;
import com.wordflow.ui.InteractionHandler;

public class EditorMenuHandler {
	public boolean handleViewMenu(int choice) {
		switch (choice) {
		case 1 -> {
//			View list of words
			InteractionHandler.clearScreen();
			DictionaryRepository repository = new DictionaryRepository("dictionary.json");
			new Editor(repository).showDictionary();
			return true;
		}
		case 2 -> {
//			Add words
			InteractionHandler.clearScreen();
			new Dictionary(new DictionaryRepository("dictionary.json")).addNewWordsToDictionary();
			return true;
		}
		case 3 -> {
//			Edit word
			InteractionHandler.clearScreen();
            DictionaryRepository repository = new DictionaryRepository("dictionary.json");
            new EditWord(repository).runFlashCardEditor();
			return true;
		}
		case 4 -> {
//			Delete word
			InteractionHandler.clearScreen();
			InteractionHandler.waitForEnter("Press Enter to return to View Menu");
			return true;
		}

		case 0 -> {
			return false;
		}
		default -> {
			System.out.println("Invalid selection. Try again.");
			InteractionHandler.waitForEnter("Press Enter to continue");
			return true;
		}
		}
	}
}
