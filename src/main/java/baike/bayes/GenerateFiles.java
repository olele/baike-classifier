package baike.bayes;

import java.io.*;
import baike.*;
import baike.common.*;
import baike.feature.*;
import baike.nlp.*;
//Small dataset
public class GenerateFiles {
	static private final String BAIKE_RAW_FILE 	= "baidu-dump.dat.50000";
	static private final String BAIKE_STATS_FILE = "baike-stats.dat.50000";
	static private final String ARTICLE_STATS_FILE = "article-stats.dat.50000";
	static private BaikeStatistics baikeStats = null;

	public static void main(String[] args) throws IOException{
	// Read raw datas
			
			InputStream rawDataIn = DataLoader.getInputStream(BAIKE_RAW_FILE);
			BaikeStatistics stats = BaikeStatistics.build(rawDataIn);
			// Write statistics data
			OutputStream statsOut = DataLoader.getOutputStream(BAIKE_STATS_FILE);
			stats.dump(statsOut);
			// Read statistics data
			InputStream statsIn = DataLoader.getInputStream(BAIKE_STATS_FILE);
			baikeStats = BaikeStatistics.load(statsIn);
					
			InputStream rawIn = DataLoader.getInputStream(BAIKE_RAW_FILE);
			OutputStream artStatsOut = DataLoader
					.getOutputStream(ARTICLE_STATS_FILE);
			// Write
			ArticleStatistics.build(artStatsOut, rawIn, baikeStats);
			// Read
			InputStream artStatsIn = DataLoader.getInputStream(ARTICLE_STATS_FILE);
			ArticleStatistics.Loader loader = new ArticleStatistics.Loader(
					artStatsIn);
			ArticleStatistics artStats = new ArticleStatistics(baikeStats);
	}

}
