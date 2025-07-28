package com.wordflow.ui.menu;
import com.wordflow.ui.InteractionHandler;

public class EditorMenuDisplay {
	
	public void displayMenuEditor() {
		String menuItems = """
				
				
				   Editor
				
				[1] Show all words
				[2] Add new words
				[3] Edit word
				[4] Delete word
				
				[0] Exit
				""";
		InteractionHandler.displayMessage(menuItems);
	}
}


//[1] Show all words
//[2] Search DE word
//[3] Edit DE word
//[4] Edit translated word
//[5] 
//[6] Edit DE example
//[7] Edit translated example
//[] Delete whole word
//
//[0] Exit