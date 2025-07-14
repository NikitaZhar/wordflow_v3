package com.wordflow.ui.menu;
import com.wordflow.editor.Editor;
import com.wordflow.repository.DictionaryRepository;
import com.wordflow.ui.InteractionHandler;

public class EditorMenuHandler {
	public boolean handleViewMenu(int choice) {
		switch (choice) {
		case 1 -> {
//			View list of words
			InteractionHandler.clearScreen();
			DictionaryRepository repository = new DictionaryRepository("dictionary.json");
			new Editor(repository).runEditor();
			return true;
		}
		case 2 -> {
//			Edit words
			InteractionHandler.clearScreen();
//                    dictionary.viewWordList();
			InteractionHandler.waitForEnter("Press Enter to return to View Menu");
			return true;
		}
		case 3 -> {
//			Add new words
			InteractionHandler.clearScreen();
//                    dictionary.handleWordEditing();
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
