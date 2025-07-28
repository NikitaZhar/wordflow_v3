package com.wordflow.ui.menu;

import com.wordflow.ui.InteractionHandler;

public class EditorMenuController {

    private final EditorMenuDisplay editorMenu = new EditorMenuDisplay();
    private final EditorMenuHandler editorMenuHandler = new EditorMenuHandler();

    public void runEditorMenu() {
        boolean continueViewMenu = true;
        while (continueViewMenu) {
            InteractionHandler.clearScreen();
            editorMenu.displayMenuEditor();
            int choice = InteractionHandler.getIntInputConsole(
                    InteractionHandler.BRIGHT_BLACK +
                            "  Please choose an option\n" +
                            InteractionHandler.RESET +
                            "  > "
            );
            continueViewMenu = editorMenuHandler.handleViewMenu(choice);
        }
    }
}
