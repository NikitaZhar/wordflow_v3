package com.wordflow.ui.menu;

import static com.wordflow.ui.InteractionHandler.RED;
import static com.wordflow.ui.InteractionHandler.RESET;

import com.wordflow.model.Dictionary;
import com.wordflow.model.Lesson;
import com.wordflow.repository.DictionaryRepository;
import com.wordflow.ui.InteractionHandler;
import com.wordflow.ui.statistics.Statistics;

public class MainMenuHandler {
	private final InteractionHandler interaction;

	public MainMenuHandler(InteractionHandler interaction) {
		this.interaction = interaction;
	}

	public boolean handleMenuOption(int choice) {
		switch (choice) {

		case 6 -> {
			DictionaryRepository dictionary = new DictionaryRepository();
			dictionary.importDictionaryCSV();
			interaction.waitForEnter("Press Enter to continue");
			return true;
		}
		
		case 7 -> {
			Dictionary dictionary = new Dictionary();
			Statistics stat = new Statistics();
			stat.displayStats(dictionary);
			interaction.waitForEnter("Press Enter to continue");
			return true;
		}
		case 1 -> {
			interaction.clearScreen();
			new Lesson().runLesson();
			return true;
		}
		case 0 -> {
			return false;
		}
		default -> {
			System.out.println("\n" + RED + " ✘ " + RESET + "Wrong Selection! \n");
			interaction.waitForEnter("Press Enter to continue ");
			return true;
		}
		}
	}
}
