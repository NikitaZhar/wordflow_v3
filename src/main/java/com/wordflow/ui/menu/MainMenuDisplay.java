package com.wordflow.ui.menu;

import com.wordflow.ui.InteractionHandler;

public class MainMenuDisplay {
	
	public void display() {
		String menuItems = """
				
				
				  Main Menu
			
				0. Exit
				1. Start lesson
				
				5. Add new word and example to Dictionary
				6. Add words and examples from CSV file to Dictionary
				7. Today statistics
				""";
		System.out.println(InteractionHandler.GREEN + menuItems + InteractionHandler.RESET);
	}
}
