package baike.bayes;
import java.util.*;
import java.io.*;

import baike.common.Category;
import baike.common.DataLoader;
import baike.feature.ArticleStatistics;
import baike.feature.BaikeStatistics;

public class ReadFile {
	static ArrayType[] featureArrayType=new ArrayType[10000];
	static int featureLength;
	static int featureLine;
	private static BaikeStatistics baikeStats = null;
	
	public static void readFileByLines(String filename) throws IOException {
		final String BAIKE_RAW_FILE 	= filename;
		final String BAIKE_STATS_FILE = "baike-stats.dat.100000";
		final String ARTICLE_STATS_FILE = "article-stats.dat.100000";
		 //ArrayType[] featureArrayType=new ArrayType[100000000];
		InputStream rawDataIn = DataLoader.getInputStream(BAIKE_RAW_FILE);
	    BaikeStatistics stats = BaikeStatistics.build(rawDataIn);
	     // Write statistics data
	    OutputStream statsOut = DataLoader.getOutputStream(BAIKE_STATS_FILE);
	    stats.dump(statsOut);
	        // Read statistics data
	    InputStream statsIn = DataLoader.getInputStream(BAIKE_STATS_FILE);
	    baikeStats = BaikeStatistics.load(statsIn);
	        					
	    InputStream rawIn = DataLoader.getInputStream(BAIKE_RAW_FILE);
	    OutputStream artStatsOut = DataLoader.getOutputStream(ARTICLE_STATS_FILE);
	     // Write
	    ArticleStatistics.build(artStatsOut, rawIn, baikeStats);
	     // Read
	    InputStream artStatsIn = DataLoader.getInputStream(ARTICLE_STATS_FILE);
	    ArticleStatistics.Loader loader = new ArticleStatistics.Loader(artStatsIn);
	    ArticleStatistics artStats = new ArticleStatistics(baikeStats);
	    ArrayList<float[]> features = new ArrayList<float[]>();
	    ArrayList<Category[]> categories = new ArrayList<Category[]>();
	    float[] emptyArray = new float[]{};
	    int dimension = baikeStats.getNumTerms();
	    int line=0;	   
	    featureArrayType[line].arrayTypeIndex=dimension;
	    while (loader.next(artStats)){
	    	featureArrayType[line]=new ArrayType(dimension);
	    	float[] feature = artStats.getFeature().toArray(emptyArray); 
	    	features.add(feature);
	    	categories.add(artStats.getCategories());
	    	for (int i=0;i<dimension;i++){
	    		//初始化
	    		featureArrayType[line].featureDataType[i]=new DataType();
	    		featureArrayType[line].featureDataType[i].count=feature[i];
	    	}
	    		
	    }
    }
	public static void main(String[] args) throws IOException{
		String fileName = "C:/temp/Character.txt";
		//String fileName = "C:/Character.txt";
        ReadFile.readFileByLines(fileName);
        	// Read raw datas      			
	}
}
