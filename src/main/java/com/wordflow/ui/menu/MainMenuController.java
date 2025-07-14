package com.wordflow.ui.menu;

import com.wordflow.ui.InteractionHandler;

public class MainMenuController {
	private final MainMenuDisplay mainMenu = new MainMenuDisplay();
	private final MainMenuHandler menuHandler = new MainMenuHandler();

	public void runMainMenu() {
		boolean continueLesson = true;
		while(continueLesson) {
			InteractionHandler.clearScreen();
			mainMenu.displayMainMenu();
			int choice = InteractionHandler.getIntInputConsole(
					InteractionHandler.BRIGHT_BLACK + 
					"  Please choose an option\n" +
					InteractionHandler.RESET +
					"  > ");
            continueLesson = menuHandler.handleMenuOption(choice);		}
	}
}
