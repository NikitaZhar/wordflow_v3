package com.wordflow.repository;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Scanner;
import java.util.Set;

import com.wordflow.model.Dictionary;
import com.wordflow.model.FlashCard;
import com.wordflow.model.Progress;

public class DictionaryRepository {
	private List<FlashCard> flashCards;
	String inputFilePath = "dictionary.txt"; 

	public List<FlashCard> getDictionary() {
		flashCards = new ArrayList<>();
		try {
			List<String> lines = Files.readAllLines(Paths.get(inputFilePath));

			for (String line : lines) {
				String[] parts = line.split(";");
				flashCards.add(new FlashCard(parts[0], parts[1], 
						FlashCard.FlashCardType.valueOf(parts[2]),
						new Progress(Integer.parseInt(parts[3]), LocalDateTime.parse(parts[4]))));
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return flashCards; 
	}
	
	public void saveDictionary(List<FlashCard> cards) {
	    try (BufferedWriter writer = Files.newBufferedWriter(Paths.get(inputFilePath))) {
	        for (FlashCard card : cards) {
	            String line = String.join(";",
	                    card.getQuestion(),
	                    card.getAnswer(),
	                    card.getType().name(),
	                    String.valueOf(card.getProgress().getReviewInterval()),
	                    card.getProgress().getLastReviewDate().toString()
	            );
	            writer.write(line);
	            writer.newLine();
	        }
	    } catch (IOException e) {
	        e.printStackTrace();
	    }
	}
	
	public void importDictionaryCSV() {
		Scanner scanner = new Scanner(System.in);
        System.out.print("Enter name CSV file : ");
        String fileName = scanner.nextLine().trim();

        DictionaryRepository repository = new DictionaryRepository();
        repository.importFromCsv(fileName);
	}
	
	public void importFromCsv(String csvPath) {
	    List<FlashCard> allCards = getDictionary();
	    Set<String> existingKeys = getExistingKeys(allCards);

	    List<FlashCard> newCards = readNewCardsFromCsv(csvPath, existingKeys);

	    allCards.addAll(newCards);
	    saveDictionary(allCards);
	    System.out.println("Added new cards : " + newCards.size());
	}
	
	private Set<String> getExistingKeys(List<FlashCard> cards) {
	    Set<String> keys = new HashSet<>();
	    for (FlashCard card : cards) {
	        String key = getKey(card.getQuestion(), card.getType().name());
	        keys.add(key);
	    }
	    return keys;
	}

	private List<FlashCard> readNewCardsFromCsv(String csvPath, Set<String> existingKeys) {
	    List<FlashCard> newCards = new ArrayList<>();

	    try (BufferedReader reader = Files.newBufferedReader(Paths.get(csvPath))) {
	        String line;
	        while ((line = reader.readLine()) != null) {
	            if (line.trim().isEmpty() || line.startsWith("question")) continue;

	            String[] parts = line.split(";", -1);
	            if (parts.length < 3) continue;

	            String question = parts[0].trim(); // RU
	            String answer = parts[1].trim();   // DE
	            String typeStr = parts[2].trim().toUpperCase();

	            try {
	                FlashCard.FlashCardType type = FlashCard.FlashCardType.valueOf(typeStr);
	                String key = getKey(question, type.name());
	                if (!existingKeys.contains(key)) {
	                    FlashCard card = new FlashCard(question, answer, type, new Progress());
	                    newCards.add(card);
	                    existingKeys.add(key);
	                }
	            } catch (IllegalArgumentException e) {
	                System.err.println("❗ Unknown type: " + typeStr + " in row: " + line);
	            }
	        }
	    } catch (IOException e) {
	        e.printStackTrace();
	    }
	    return newCards;
	}

	private String getKey(String question, String type) {
	    return (question + ";" + type).toLowerCase();
	}


}
