package baike.feature;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.junit.Assert;
import org.junit.Test;

import baike.common.DataLoader;

public class TestBaikeStatistics {
	private final String BAIKE_RAW_FILE 	= "baidu-dump.dat.50000";
	private final String BAIKE_STATS_FILE = "baike-stats.dat.50000";
	
	@Test 
	public void testReadWrite() throws IOException {		
		// Read raw data
		InputStream rawDataIn = DataLoader.getInputStream(BAIKE_RAW_FILE);
		BaikeStatistics stats = BaikeStatistics.build(rawDataIn);
		// Write statistics data
		OutputStream statsOut = DataLoader.getOutputStream(BAIKE_STATS_FILE);
		stats.dump(statsOut);
		// Read statistics data
		InputStream statsIn   = DataLoader.getInputStream(BAIKE_STATS_FILE);
		BaikeStatistics stats2 = BaikeStatistics.load(statsIn);
		Assert.assertEquals(stats, stats2);
	}
	
	@Test 
	public  void testBulid() throws IOException {
		String strRawData = "ID:32\nFullText:技巧一个 第一，“积极起跳式”，\n\n" +
												"ID:33\nFullText:三级跳又称为三级跳远，是田径中的其中一个项目之一";

		RawData.Loader loader = TestRawData.str2Raw(strRawData);
		BaikeStatistics stats = BaikeStatistics.build(loader);
		String[] terms = new String[] {
			"技巧", "一个", "第一",  "积极", "起跳", "式",
			"三级跳", "称为", "三级跳远", "田径", "中", "项目"
		};
		int expectedNumTerms  = terms.length;
		int actualNumTerms 		= stats.getNumTerms();
		Assert.assertEquals(expectedNumTerms, actualNumTerms);
		int expectedTermIndex = 0;
		for(String term : terms) {
			int actualTermIndex = stats.getTermIndex(term);
			Assert.assertEquals(expectedTermIndex, actualTermIndex);
			int expectedFeq = 1;
			if(term.equals("一个")) expectedFeq = 2;
			Assert.assertEquals(expectedFeq, stats.getTermFrequency(actualTermIndex));
			Assert.assertEquals(expectedFeq, stats.getDocumentFrequency(actualTermIndex));
			
			expectedTermIndex ++;
		}
	}
}
