package baike.classifier;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.junit.Test;

import baike.classifier.MultiClassSVM;
import baike.common.*;
import baike.feature.*;
import baike.nlp.*;

public class TestSVM {

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
	public void svm_classify() throws IOException
	{
		get_data();
		/*
		 * create ANN
		 */
		System.out.println("baike total: "+baikeStats.getTotal());
		int train_total_count = 18000;
		System.out.println("svm: dim="+baikeStats.getNumTerms()+",count="+train_total_count);		
		MultiClassSVM mc_svm = new MultiClassSVM(train_total_count, baikeStats.getNumTerms());
		
		System.out.println("svm is created");
		
		int i=0;
		double[] double_array = new double[1];
		while (loader.next(artStats)) {
			
			if ( i < train_total_count )
			{
//				System.out.println(artStats);
				if ( i%(train_total_count/1000) == 0 )
					System.out.println( "training progress: " + (i/train_total_count*100) + "%" ); 
				Category[] c = artStats.getCategories();
				double[] labels = new double[12];
				for ( int j=0; j<c.length; j++ )
					labels[ c[j].ordinal() ] = 1;
//				for ( int k=0; k<c.length; k++)
//					mc_svm.addDataRow(i, artStats.getFeature().toArray(double_array), c[k].ordinal());
				double[] f = artStats.getFeature().toArray(double_array);
				double sum=0;
				for ( int u=0; u<f.length; u++ )
					sum += f[u];
				for ( int u=0; u<f.length; u++ )
					f[u] = f[u] / sum * 10;
				mc_svm.addDataRow(i, f, c[0].ordinal());
			}else{
				break;
			}
			i++;
		}
		if ( i<train_total_count ){
			System.out.println( "sample error!" );
			System.exit(0);
		}
		
		System.out.println("begin train");
		mc_svm.train("-t 2 -c 100");
		
		// predict
		int right=0;
		int total = 0;
		while( loader.next(artStats) )
		{
			double[] f = artStats.getFeature().toArray(double_array);
			double sum=0;
			for ( int u=0; u<f.length; u++ )
				sum += f[u];
			for ( int u=0; u<f.length; u++ )
				f[u] = f[u] / sum * 10;
			int predict = (int)mc_svm.predict( f );
			Category[] c = artStats.getCategories();
			System.out.print( "catogories: ");
			for( int l=0; l<c.length; l++ ){
				System.out.print(c[l].ordinal()+",");
				if ( predict == c[l].ordinal() )
					right ++;
			}
			total ++;
			System.out.println("predict: " + predict);
		}
		System.out.println("right: "+right+"/"+total);
	}
}
