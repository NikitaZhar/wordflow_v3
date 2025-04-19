package com.wordflow.ui;

import java.io.IOException;
import java.util.Scanner;

public class InteractionHandler {
	public static final String RESET = "\u001B[0m";
    public static final String RED = "\u001B[31m";
    public static final String GREEN = "\u001B[32m";
    public static final String YELLOW = "\u001B[33m";
    public static final String CYAN = "\u001B[36m";
    public static final String WHITE = "\u001B[37m";
    public static final String BRIGHT_BLACK = "\u001B[90m";
    
	private static final Scanner scanner = new Scanner(System.in);
	
//	private InteractionHandler() {
// запрет на создание экземпляра
//		Разобраться с этим - почему так происходит!!!!
//    }
	
	public static int getIntInputConsole(String prompt) {
	    System.out.print(prompt + " ");
	    while (!scanner.hasNextInt()) {
	        System.out.print("\n" + RED + " ✘ " + RESET + "Wrong selection! \n\n" + prompt);
	        scanner.next();
	    }
	    int value = scanner.nextInt();
	    scanner.nextLine();
	    return value;
	}
	
	public static String getStringInput(String prompt) {
	    System.out.print(prompt + " ");
	    String input = scanner.nextLine().trim();

	    while (input.isEmpty()) {
	        System.out.print("\n" + RED + " ✘ " + RESET + " Input cannot be empty! \n\n" + prompt);
	        input = scanner.nextLine().trim();
	    }
	    return input;
	}

	public static boolean getYesNoUserInputConsole(String prompt) {
	    System.out.print(prompt + " (y/n): ");
	    String input = scanner.nextLine().trim().toLowerCase();

	    while (!input.equals("y") && !input.equals("n")) {
	        System.out.print("\n " + RED + " ✘ " + RESET + " Enter 'y' or 'n' only \n\n" + prompt + " (y/n): ");
	        input = scanner.nextLine().trim().toLowerCase();
	    }
	    return input.equals("y");
	}

	public static void waitForEnter(String prompt) {
	    System.out.println(prompt);
	    scanner.nextLine();
	}
	
	public static void displayMessage(String prompt) {
		System.out.println(prompt);
	}
	
	public static void displayMessage(String prompt, Object object) {
		System.out.println(prompt + "\n" + object);
	}
	
	public static void clearScreen() {
		try {
			if (System.getProperty("os.name").contains("Windows")) {
				new ProcessBuilder("cmd", "/c", "cls").inheritIO().start().waitFor();
			} else {
				Runtime.getRuntime().exec("clear");
				System.out.print("\033[H\033[2J");
				System.out.flush();
			}
		} catch (IOException | InterruptedException ex) {
			System.out.println("Error clearing console.");
		}
	}
}
