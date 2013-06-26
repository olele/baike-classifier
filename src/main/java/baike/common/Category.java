package baike.common;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

public enum Category {
	//=========================== Interface ================================

	//艺术;技术;文化;生活;地理;社会;人物;经济;科学;历史;自然;体育
	ART, TECH, CULTURE, LIFE, GEOGRAPHY, 
	SOCIETY, PERSON, ECONOMICS, SCIENCE, HISTORY, 
	NATURE, SPORTS;
	
	public static Category getCategory(String subCategoryName) {
		if(allCategories == null) {
				init();
		}
		return allCategories.get(subCategoryName);
	}
		
	//=========================== Implementation ================================
	
	private static final String CATEGORIES_FILE = "/baidu-taxonomy.dat";
	private static Map<String, Category> allCategories;
	
	private static void init() {
		allCategories = new HashMap<String, Category>();
		initRoot();
		initSubs();
	}
	private static void initRoot() {
		allCategories.put("艺术", ART);
		allCategories.put("技术", TECH);
		allCategories.put("文化", CULTURE);
		allCategories.put("生活", LIFE);
		allCategories.put("地理", GEOGRAPHY);
		allCategories.put("社会", SOCIETY);
		allCategories.put("人物", PERSON);
		allCategories.put("经济", ECONOMICS);
		allCategories.put("科学", SCIENCE);
		allCategories.put("历史", HISTORY);
		allCategories.put("自然", NATURE);
		allCategories.put("体育", SPORTS);
	}
	
	private static void initSubs() {
		InputStream in = Category.class.getResourceAsStream(CATEGORIES_FILE);
		BufferedReader reader = new BufferedReader(new InputStreamReader(in));
		String line; String[] tokens;
		try {
			line = reader.readLine();	// ignore the first line, which is root categories
			line = reader.readLine();	// start from the second line
			while(line != null) {
				tokens = line.split("\t|;");
				String parent = tokens[0];
				Category category = getCategory(parent);
				assert(category != null);
				for(int child_i = 1; child_i < tokens.length; child_i ++) {
					String child = tokens[child_i];
					allCategories.put(child, category);
				}
				line = reader.readLine();
			}
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
	
}
