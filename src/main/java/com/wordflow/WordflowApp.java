package com.wordflow;

import java.sql.Connection;

import com.wordflow.ui.menu.MainMenuController;

public class WordflowApp {
    public static void main(String[] args) {
    	
    	
    	try (Connection conn = DatabaseConnector.getConnection()) {
    	    System.out.println("✅ Подключение к БД успешно");
    	} catch (Exception e) {
    	    System.out.println("❌ Не удалось подключиться к БД");
    	    e.printStackTrace();
    	}
    	
    	
    	new MainMenuController().runMainMenu();
    }
}
