package baike.feature;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import baike.common.Category;
import baike.common.DataLoader;

public class TestArticleStatistics {
	private final String BAIKE_RAW_FILE 	= "baidu-dump.dat.50000";
	private final String BAIKE_STATS_FILE = "baike-stats.dat.50000";
	private final String ARTICLE_STATS_FILE = "article-stats.dat.50000";
	
	private BaikeStatistics baikeStats = null;
	
	@Before
	public void setup() throws IOException {
		InputStream statsIn = DataLoader.getInputStream(BAIKE_STATS_FILE);
		baikeStats = BaikeStatistics.load(statsIn);
	}
	
	@Test
	public void testBuildAndLoad() throws IOException {
		InputStream rawIn = DataLoader.getInputStream(BAIKE_RAW_FILE);
		OutputStream artStatsOut = DataLoader.getOutputStream(ARTICLE_STATS_FILE);
		// Write
		ArticleStatistics.build(artStatsOut, rawIn, baikeStats);
		// Read
		InputStream artStatsIn = DataLoader.getInputStream(ARTICLE_STATS_FILE);
		ArticleStatistics.Loader loader = new ArticleStatistics.Loader(artStatsIn);
		ArticleStatistics artStats = new ArticleStatistics(baikeStats);
		while(loader.next(artStats)) {
//			System.out.println(artStats);
		}
		loader.close();
	}
	
	@Test
	public void testReadWrite() throws IOException {
		// Write 
		ByteArrayOutputStream out0 = new ByteArrayOutputStream();
		DataOutputStream out = new DataOutputStream(out0);
		ArticleStatistics artStats = new ArticleStatistics(baikeStats);
		artStats.setID(2); 
		artStats.setCategories(new Category[]{Category.ART, Category.NATURE});
		artStats.write(out);
		// Read content
		ByteArrayInputStream in0 = new ByteArrayInputStream(out0.toByteArray());
		DataInputStream in = new DataInputStream(in0);		
		ArticleStatistics artStats2 = new ArticleStatistics(baikeStats);
		artStats2.readFields(in);
		// Check equality
		Assert.assertEquals(artStats, artStats2);
	}
}
