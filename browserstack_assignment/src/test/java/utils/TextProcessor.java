package utils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TextProcessor {
    /**
     * Print words repeated more than twice along with their counts.
     */
    public static void analyzeRepeatedWords(List<String> translatedTitles) {
        Map<String, Integer> wordCount = new HashMap<>();

        for (String title : translatedTitles) {
            String[] words = title.toLowerCase().split("[\\s\\p{Punct}]+");
            for (String word : words) {
                if (word.isBlank()) continue;
                wordCount.put(word, wordCount.getOrDefault(word, 0) + 1);
            }
        }

        System.out.println("Words repeated more than twice:");
        boolean foundRepeated = false;
        for (Map.Entry<String, Integer> entry : wordCount.entrySet()) {
            if (entry.getValue() > 2) {
                System.out.println(entry.getKey() + ": " + entry.getValue()+ " times repeated");
                foundRepeated = true;
            }
        }

        if (!foundRepeated) {
            System.out.println("No words repeated more than twice in the translated titles.");
        }
    }
}
