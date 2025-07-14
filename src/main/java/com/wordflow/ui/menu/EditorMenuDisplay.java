package com.wordflow.ui.menu;
import com.wordflow.ui.InteractionHandler;

public class EditorMenuDisplay {
//	public void displayEditorMenuOld() {
//		String menuItems = """
//				
//				
//				  Editor
//			
//				[1] View list of words
//				[2] Edit words
//				[3] Add new words
//				[0] Exit
//				""";
//		InteractionHandler.displayMessage(menuItems);
//	}
	
	public void displayMenuEditor() {
		String menuItems = """
				
				
				[🔻🔺] Up, Down     [1] Edit word     [2] Add new words     [3] Delete word 
				
				""";
		InteractionHandler.displayMessage(menuItems);
	}
}
