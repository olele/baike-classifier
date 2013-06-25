package baike.nlp;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashSet;
import java.util.Set;

public class StopWords {
	public static boolean contains(String word) {
		if(stopWords == null) init();
		return stopWords.contains(word);
	}
	
	private static Set<String> stopWords; 
	
	private static void init() {
		stopWords = new HashSet<String>();
		String[] stopWordFiles = new String[] {
			"cn.txt", "en.txt"
		};
		String base = "/stop-words/";
		for(String name : stopWordFiles) {
			String path = base + name;
			addWordsFromFile(path);
		}
	}
	
	private static void addWordsFromFile(String path) {
		InputStream in = StopWords.class.getResourceAsStream(path);
		if(in == null) {
			System.err.println("Error: failed to load stop words. File " + 
													path + " is not found");
			return;
		}
		BufferedReader reader = new BufferedReader(new InputStreamReader(in));
		String line;
		try {
			line = reader.readLine();
			while(line != null) {
				String stopWord = line.trim();
				stopWords.add(stopWord);
				line = reader.readLine();
			}
		} catch (IOException e) {
			System.err.println("Error occurred while loading stop words");
			e.printStackTrace();
		}
	}
}
