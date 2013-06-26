package baike.common;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import junit.framework.Assert;

import org.junit.Test;

public class TestCategory {
	@Test
	public void test() {
		Map<String, Category> expectedCategories = new HashMap<String, Category>();
		expectedCategories.put("绘画", 		Category.ART);
		expectedCategories.put("四书五经", 	Category.CULTURE);
		expectedCategories.put("期货", 		Category.ECONOMICS);
		expectedCategories.put("广东", 		Category.GEOGRAPHY);
		expectedCategories.put("三国", 		Category.HISTORY);
		expectedCategories.put("旅游", 		Category.LIFE);
		expectedCategories.put("地震", 		Category.NATURE);
		expectedCategories.put("总统", 		Category.PERSON);
		expectedCategories.put("物理", 		Category.SCIENCE);
		expectedCategories.put("坦克", 		Category.SOCIETY);
		expectedCategories.put("万智牌", 		Category.SPORTS);
		expectedCategories.put("网卡", 		Category.TECH);

		Iterator<Entry<String, Category>> it = expectedCategories.entrySet().iterator();
		while(it.hasNext()) {
			Entry<String, Category> e = it.next();
			String subCategoryName 		= e.getKey();
			Category expectedCategory = e.getValue(),
							 realCategory 		= Category.getCategory(subCategoryName);
			Assert.assertEquals(expectedCategory, realCategory);
		}
	}
}
