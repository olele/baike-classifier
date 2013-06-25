package baike.nlp;

import org.junit.Assert;
import org.junit.Test;

import baike.nlp.StopWords;

public class TestStopWords {
	@Test
	public void testCN() {
		String[] someStopWords = new String[] {
			"这", "这边", "这儿", "一样", "我", "且"	
		};
		checkContains(someStopWords);
	}
	
	@Test
	public void testEN() {
		String[] someStopWords = new String[] {
			"the", "you", "is"	
		};
		checkContains(someStopWords);
	}
	
	private void checkContains(String[] words) {
		for(String word : words)
			Assert.assertEquals(true, StopWords.contains(word));
	}
 }
