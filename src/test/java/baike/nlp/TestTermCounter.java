package baike.nlp;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;

public class TestTermCounter {
	private TermCounter counter;
	
	@Before
	public void setup() {
		counter = new TermCounter();
	}
	
	@Test
	public void testCount() {
		String text = "中国科学院计算技术研究所在多年研究基础上，" +
									"耗时一年研制出了基于多层隐码模型的汉语词法分析系统";
		counter.update(text);								// the first time
		checkAllCountEquals(counter, 1);		// every term is unique
		counter.update(text);								// the second time
		checkAllCountEquals(counter, 2);		// every term occurs twice
		counter.reset();										
		checkAllCountEquals(counter, 0); 
	}
	
	@Test
	public void testSegmentation() {
		String text = "Ansj中文分词是一个真正的ict的实现.并且加入了自己的一些数据结构" +
									"和算法的分词.实现了高效率和高准确率的完美结合!";
		counter.update(text);
		checkTermsEqual(counter, new String[] {
			"ansj", "中文", "一个", "真正", "ict", "实现", ".", "加入", 
			"一些", "数据", "结构", "算法", "分词", ".", "实现", "高", "效率", "高", 
			"准确率", "完美", "结合", "!"
		});
	}
	
	@Test
	public void testStopWordsAndSpaces() {
		String cnStopWords = "这的那个等";
		String enStopWords = "and is i    \tyou \nam";
		counter.update(cnStopWords);
		counter.update(enStopWords);
		checkTermsEqual(counter, new String[]{});	// counter should ignore stop words
	}
	
	private void checkAllCountEquals(TermCounter counter, int expectedCount) {
		Set<String> terms = counter.getTerms();
		Iterator<String> it = terms.iterator();
		while(it.hasNext()) {
			String term = it.next();
			Assert.assertEquals(expectedCount, counter.getCount(term));
		}
	}
	
	private void checkTermsEqual(TermCounter counter, String[] expectedTerms) {
		Set<String> realTermSet = counter.getTerms();
		Set<String> expectedTermSet = new HashSet<String>();
		for(String et : expectedTerms) {
			expectedTermSet.add(et);
		}
//		System.out.println("expected = " + expectedTermSet);
//		System.out.println("real = " + realTermSet);
		Assert.assertEquals(expectedTermSet, realTermSet);
	}
}
