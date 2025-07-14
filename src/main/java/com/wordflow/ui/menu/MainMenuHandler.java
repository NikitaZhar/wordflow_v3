package com.wordflow.ui.menu;

import static com.wordflow.ui.InteractionHandler.RED;
import static com.wordflow.ui.InteractionHandler.RESET;

import com.wordflow.editor.Editor;
import com.wordflow.model.Dictionary;
import com.wordflow.model.Lesson;
import com.wordflow.repository.DictionaryRepository;
import com.wordflow.ui.InteractionHandler;
import com.wordflow.ui.statistics.Statistics;

public class MainMenuHandler {

	public boolean handleMenuOption(int choice) {
		switch (choice) {
		case 1 -> {
//			Run Lesson
			InteractionHandler.clearScreen();
			new Lesson().runLesson();
			return true;
		}
		case 2 -> {
//			Statistics
			InteractionHandler.clearScreen();
			Dictionary dictionary = new Dictionary(new DictionaryRepository("dictionary.json"));
			Statistics statistics = new Statistics();
			statistics.displayStats(dictionary);
			return true;
		}
		
		case 3 -> {
//			Edit words
			InteractionHandler.clearScreen();
			
			DictionaryRepository repository = new DictionaryRepository("dictionary.json");
			new Editor(repository).runEditor();
			
			
//			new EditorMenuController().runEditorMenu();
			
//			Dictionary dictionary = new Dictionary(new DictionaryRepository("dictionary.json"));
//			dictionary.editWordInDictionary();
			
			return true;
		}
		
		case 4 -> {
//			Add new words to Dictionary
			InteractionHandler.clearScreen();
			new Dictionary(new DictionaryRepository("dictionary.json")).addNewWordsToDictionary();
			return true;
		}

		case 11 -> {
//			Hidden check of Dictionary
			InteractionHandler.clearScreen();
			new Dictionary(new DictionaryRepository("dictionary.json")).checkDictionary();
			return true;
		}

		case 0 -> {
			return false;
		}
		default -> {
			System.out.println("\n" + RED + " ✘ " + RESET + "Wrong Selection! \n");
			InteractionHandler.waitForEnter("Press Enter to continue ");
			return true;
		}
		}
	}
}
