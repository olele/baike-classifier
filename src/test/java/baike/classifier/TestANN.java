package baike.classifier;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.junit.Test;

import baike.classifier.MultiClassSVM;
import baike.classifier.MultiLayerANN;
import baike.common.*;
import baike.feature.*;
import baike.nlp.*;

public class TestANN {

	private final String BAIKE_RAW_FILE = "baidu-dump.dat.200k";
	private final String BAIKE_STATS_FILE = "baike-stats.dat.200k";
	private final String ARTICLE_STATS_FILE = "article-stats.dat.200k";
	private BaikeStatistics baikeStats = null;
	private ArticleStatistics artStats = null;
	ArticleStatistics.Loader loader = null;
	
	void get_data() throws IOException
	{
		/*
		 * build baike stats file
		 */
//		InputStream rawDataIn = DataLoader.getInputStream(BAIKE_RAW_FILE);
//		BaikeStatistics stats = BaikeStatistics.build(rawDataIn);
//		OutputStream statsOut = DataLoader.getOutputStream(BAIKE_STATS_FILE);
//		stats.dump(statsOut);
		
		// Read statistics data
		InputStream statsIn = DataLoader.getInputStream(BAIKE_STATS_FILE);
		baikeStats = BaikeStatistics.load(statsIn);
		
		/*
		 * build article stats file
		 */
//		InputStream rawIn = DataLoader.getInputStream(BAIKE_RAW_FILE);
//		OutputStream artStatsOut = DataLoader
//				.getOutputStream(ARTICLE_STATS_FILE);
//		ArticleStatistics.build(artStatsOut, rawIn, baikeStats);
		
		// Read
		InputStream artStatsIn = DataLoader.getInputStream(ARTICLE_STATS_FILE);
		loader = new ArticleStatistics.Loader(
				artStatsIn);
		artStats = new ArticleStatistics(baikeStats);
	}
	
	@Test
	void ann_classify() throws IOException {
		get_data();
		/*
		 * create ANN
		 */
		System.out.println("ann: "+baikeStats.getNumTerms()+","+baikeStats.getNumTerms()*2+","+12);
		double[] double_array = new double[1];
		MultiLayerANN ann = new MultiLayerANN( baikeStats.getNumTerms(), baikeStats.getNumTerms()*2, 12 );
		int train_count = 0;
		while (loader.next(artStats)) {
			if ( train_count < 100 )
			{
				Category[] c = artStats.getCategories();
				double[] labels = new double[ann.output_dim];
				for ( int i=0; i<c.length; i++ )
					labels[ c[i].ordinal() ] = 1; 
				ann.addDataSetRow(artStats.getFeature().toArray(double_array), labels);
			}
			train_count++;
		}
		loader.close();
	}
}
