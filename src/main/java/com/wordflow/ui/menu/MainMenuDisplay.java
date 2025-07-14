package com.wordflow.ui.menu;

import com.wordflow.ui.InteractionHandler;

public class MainMenuDisplay {
	
	public void displayMainMenu() {
		String menuItems = """
				
				
				  Main Menu
			
				[1] Start lesson
				[2] Today statistics
				[3] Dictionary management
				[4] Add new words
				
				[0] Exit
				""";
		InteractionHandler.displayMessage(menuItems);
//		System.out.println(InteractionHandler.GREEN + menuItems + InteractionHandler.RESET);
	}
	
	
}

//2. Check Dictionary