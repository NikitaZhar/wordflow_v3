package com.wordflow.repository;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.wordflow.model.FlashCard;
import com.wordflow.ui.InteractionHandler;

public class DictionaryRepository {
	private String dictionaryFilePath;
	private final ObjectMapper mapper;

	public DictionaryRepository(String dictionaryFilePath) {
		this.dictionaryFilePath = dictionaryFilePath;
		this.mapper = new ObjectMapper()
				.registerModule(new JavaTimeModule())
				.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
	}

	public List<FlashCard> readNewWords() {
	    String importFilePath = InteractionHandler.getStringInput(
	            "\n Path to JSON file with card(s):");
	    if (!isFileExists(importFilePath)) {
	        return new ArrayList<>();
	    }
	    if (!importFilePath.endsWith(".json")) {
	        notifyUnsupportedFileType();
	        return new ArrayList<>();
	    }
	    return getNewCardFromFile(importFilePath);
	}

	
	public Optional<FlashCard> manageEditWord() {
	    String id = InteractionHandler.getStringInput("\n Enter the ID of the card you want to edit: ");
	    if (id == null || id.isBlank()) {
	        InteractionHandler.waitForEnter("\n No ID provided. Edit cancelled. Press Enter to continue ... ");
	        return Optional.empty();
	    }

	    String filePath = InteractionHandler.getStringInput("\n Enter the path to the JSON file with the updated card: ");
	    if (filePath == null || filePath.isBlank()) {
	        InteractionHandler.waitForEnter("\n No file path provided. Edit cancelled. Press Enter to continue ... ");
	        return Optional.empty();
	    }

	    try {
	        FlashCard updatedCard = getCardFromFile(filePath);
	        updatedCard.setId(id);
	        return Optional.of(updatedCard);
	    } catch (RuntimeException e) {
	        InteractionHandler.waitForEnter(e.getMessage() + "\n Edit cancelled. Press Enter to continue ... ");
	        return Optional.empty();
	    }
	}

	private void notifyUnsupportedFileType() {
		InteractionHandler.waitForEnter(
				"\n Unsupported file type. Only .json or .txt allowed. " +
				"Press Enter to continue ... ");
	}

	private boolean isFileExists(String path) {
		Path file = Paths.get(path);
		if (!Files.exists(file)) {
			InteractionHandler.waitForEnter("\n File not found: " + path +
					". Press Enter to continue ... ");
			return false;
		}
		return true;
	}
	
	public FlashCard getCardFromFile(String filePath) {
//		Метод для замены карточки
	    try {
	        File file = new File(filePath);
	        if (!file.exists()) {
	            throw new RuntimeException("\n File not found: " + filePath);
	        }
	        return mapper.readValue(file, FlashCard.class);
	    } catch (IOException e) {
	        throw new RuntimeException("\n Failed to load card from file: " + filePath, e);
	    }
	}

	public List<FlashCard> getNewCardFromFile(String filePath) {
	    try {
	        File file = new File(filePath);
	        if (!file.exists()) {
	            System.err.println("\n️ File not found: " + filePath);
	            return new ArrayList<>();
	        }
	        try {
	            return mapper.readValue(
	                    file,
	                    mapper.getTypeFactory().constructCollectionType(List.class, FlashCard.class)
	            );
	        } catch (IOException e) {
	            FlashCard card = mapper.readValue(file, FlashCard.class);
	            List<FlashCard> singleCardList = new ArrayList<>();
	            singleCardList.add(card);
	            return singleCardList;
	        }
	    } catch (IOException e) {
	        throw new RuntimeException("\n Failed to load cards from file: " + filePath, e);
	    }
	}

//	private List<FlashCard> getCardsFromListFile(String listFilePath) {
//		List<FlashCard> allCards = new ArrayList<>();
//		try (Stream<String> lines = Files.lines(Paths.get(listFilePath))) {
//			lines.map(String::trim)
//			.filter(line -> !line.isEmpty())
//			.forEach(path -> {
//				try {
//					List<FlashCard> cards = getNewCardFromFile(path);
//					allCards.addAll(cards);
//				} catch (Exception e) {
//					System.err.println("⚠️ Failed to import from: " + path + ". Reason: " + e.getMessage());
//				}
//			});
//		} catch (IOException e) {
//			throw new RuntimeException("\n Failed to read list file: " + listFilePath, e);
//		}
//		return allCards;
//	}

	public List<FlashCard> loadDictionaryFromFile() {
		return getNewCardFromFile(dictionaryFilePath);
	}

	public void saveDictionaryToFile(List<FlashCard> cards) {
		try {
			File file = new File(dictionaryFilePath);
			if (!file.exists()) {
				file.createNewFile();
			}
			mapper.writeValue(file, cards);
		} catch (IOException e) {
			throw new RuntimeException("\n Failed to save cards to file: " + dictionaryFilePath, e);
		}
	}
}