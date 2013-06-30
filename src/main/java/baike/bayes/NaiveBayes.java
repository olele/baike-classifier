package baike.bayes;
import java.io.*;
import java.util.*;
import java.math.*;

import baike.common.Category;
import baike.common.DataLoader;
import baike.feature.ArticleStatistics;
import baike.feature.BaikeStatistics;

public class NaiveBayes {
	 /** 
     * 将原训练元组按类别划分 
     * @param datas 训练元组 
     * @return Map<类别，属于该类别的训练元组> 
     */ 
	
	/*
	 * 多分类定义
	 */
	 static ProType[] cateProTypes = new ProType[100];
	 static ProType[][][] featureProTypes =  new ProType[13][100000][100000];//记录多少个文件
	 static ProType[][] featureMaxProTypes = new ProType[13][100000];
	 static ProType[][] featureMinProTypes = new ProType[13][100000];
//	 static DocFeature[] docFeature = new DocFeature [10000];
	 static double[][] featureCount = new double [100000][100000]; //每一行特征的总数
	 static double[][] featureTotalCount = new double [50][100000];
//	 static double[] featureLineCount = new double [100000];
	 static int[] testCategory = new int [100000];
	 static int fileIndex=0;
	 static double PrePro=0.2;
	 static ArrayType[] featureArrayType=new ArrayType[100000];
	 static int featureLength;
	 static int featureLine;
	 
	 static private BaikeStatistics baikeStats = null;
//	  统计每个特征取值
	 static double[][][] featurePiecewise = new double [50][100000][3]; 
	 static double[][][] featurePiecewisePro = new double [50][100000][4];
	 /** 
     * 在训练数据的基础上预测测试元组的类别 
     * @param datas 训练元组 
     * @param testT 测试元组 
     * @return 测试元组的类别 
	 * @throws IOException 
     */  
	 
	 /*
	  * 统计分类元组所占的比率 Category
	  */
	 
		
	 public static void readFileByLines() throws IOException {
			final String BAIKE_RAW_FILE 	= "baidu-dump.dat.100000";
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
	 public void CategoryPro () throws IOException
	 {
		 File file=new File("C:\\FeatureKE");
		 String categoryID[];
		 categoryID=file.list();
		 System.out.println(categoryID.length);
		 int totalCount=0;
		 for(int i=0;i<categoryID.length;i++){
			 cateProTypes[i].category = categoryID[i];
			 String fileName = "C:\\FeatureKE\\" + categoryID[i];
			 //直接读入单文件
			 readFileByLines();
			 cateProTypes[i].length = ReadFile.featureArrayType.length;
			 totalCount+= cateProTypes[i].length;
		 }
		 for (int i=0;i<categoryID.length;i++){
			 cateProTypes[i].proType=Arith.div((double)cateProTypes[i].length, (double)totalCount);
		 }
	 }
	 /*
	  * 统计各类别下特征的概率，面临高维度的问题
	  */
	 public static void TrainFeaturePro() throws IOException{
		 File file=new File("C:\\FeatureKE");
		 String categoryID[];
		 categoryID=file.list();
//		 double featureCount=0.00;
//		 double[] featureCount = new double[10000];
		 int arrayTypeLength = ReadFile.featureArrayType[0].arrayTypeIndex;
		 for(int i=0;i<categoryID.length;i++){
			 cateProTypes[i].category = categoryID[i];
			 double temp1=0,temp2=1;
			 String fileName = "C:\\FeatureKE\\" + categoryID[i];
			 readFileByLines();
			 //假设选取的feature的size相同	
			 for (int f=0; f<ReadFile.featureLength; f++)
				 for (int k=0; k<arrayTypeLength; k++)
				 {
					 //add feature count 维度相等，直接覆盖  不同文档中,对应于每一行统计特征的总数
					 featureCount[fileIndex][f]+=ReadFile.featureArrayType[f].featureDataType[k].count;						 
				 }
			 //计算每个特征发生的概率 针对这一行的所有特征总和
			 for (int k=0;k<arrayTypeLength;k++)//统计列属性
				 {
				 for (int f=0;f<ReadFile.featureLength;f++)
				 	{
					 featureProTypes[fileIndex][f][k].proType=Arith.div(ReadFile.featureArrayType[f].featureDataType[k].count,featureCount[fileIndex][f]);
					 if (featureProTypes[fileIndex][f][k].proType>temp1){
						 temp1=featureProTypes[fileIndex][f][k].proType;
					 	}
					 if (featureProTypes[fileIndex][f][k].proType<temp2){
						 temp2=featureProTypes[fileIndex][f][k].proType;
					 	}
				 	}
				 featureMaxProTypes[fileIndex][k].proType=temp1;
				 featureMinProTypes[fileIndex][k].proType=temp2;
				 //分段计算训练数据集中的概率
				 featurePiecewise[fileIndex][k][1]=Arith.div(temp1-temp2, 3, 3);
				 featurePiecewise[fileIndex][k][2]=Arith.div((temp2-temp1)*2, 3, 3);				 
				 }				 
			 
//			 docFeature[fileIndex].categoryString = fileName;
//			 docFeature[fileIndex].feaArrayList.add(featureCount);
			 //将所有的feature进行总和相加
			 for (int k=0; k<arrayTypeLength; k++)
				 for (int f=0; f<ReadFile.featureLength;f++)
					 featureTotalCount[fileIndex][k]+=ReadFile.featureArrayType[f].featureDataType[k].count;	 
			 fileIndex++;			 
		}
		 //计算训练数据中总和的概率
		 int pieceCount1=0,pieceCount2=0,pieceCount3=0,totalCount=0;
		 for (int i=0;i<fileIndex;i++)
			 for (int k=0;k<arrayTypeLength;k++){
				 for (int f=0;f<ReadFile.featureLength;f++){
					 if (featureProTypes[i][f][k].proType < featurePiecewise[i][k][1]){
						 pieceCount1++;
					 }
					 else 
					 if (featureProTypes[i][f][k].proType>=featurePiecewise[i][k][1] && featureProTypes[i][f][k].proType<featurePiecewisePro[i][k][2]){
						 pieceCount2++;
					 }
					 else {
						 pieceCount3++;
					}	
				 }
				 totalCount=pieceCount1+pieceCount2+pieceCount3;
				 featurePiecewisePro[i][k][1]=Arith.div(pieceCount1, totalCount, 3);
				 featurePiecewisePro[i][k][2]=Arith.div(pieceCount2, totalCount, 3);
				 featurePiecewisePro[i][k][3]=Arith.div(pieceCount3, totalCount, 3);
			 }
				 
		 //分段划分概率
//		for (int i=0; i<fileIndex; i++)
//			for (int j=0; j<arrayTypeLength;j++){
//				featureProTypes[i].proType = Arith.div(featureCount[i][j], featureTotalCount[j]);
//			}
		
	 }
	 /** 
     * 计算指定属性列上指定值出现的概率 
     * @param dArrayTypes 属于某一类的训练元组的一行
     * @param count 对应取值 
     * @param index 属性列索引 
     * @return 概率 
     */ 
	public static double LineProbability (ArrayType dArrayTypes,double count, int index){
		double pro=0.00;
		double proCount =0.00;
		for (int i=0; i<dArrayTypes.arrayTypeIndex; i++){
			proCount+=dArrayTypes.featureDataType[i].count;
		}
		pro=Arith.div(count, proCount, 3);
		return pro;
	}
	public static double PiecewisePro(int fileIndex,int feaPos, double count){
		//double[] PiecewiseCate = new double[100];
		double PiecewiseCate=0.00;
		if (count < featurePiecewise[fileIndex][feaPos][1]){
			PiecewiseCate=featurePiecewisePro[fileIndex][feaPos][1];
		}
		else if(count>=featurePiecewise[fileIndex][feaPos][1] && count<featurePiecewise[fileIndex][feaPos][2]){
			PiecewiseCate=featurePiecewisePro[fileIndex][feaPos][2];
		}
		else{
			PiecewiseCate=featurePiecewisePro[fileIndex][feaPos][3];
		}
		return PiecewiseCate;
	}
	public static int MaxClassifyPro(double[] classifyPro,int cSize){
		int classifyIndex = 0;
		double temp=0;
		int tempCl=(int)(Math.random()*12);
		for (int i=0;i<cSize;i++){
			if (classifyPro[i]>temp){
				temp=classifyPro[i];
				classifyIndex=i;
				
			}
		}
			classifyIndex=tempCl;
			return classifyIndex;
	}
	public static void PredictCategory() throws IOException{
		String testFileString = "baidu-dump.dat.50000";
		BufferedWriter bw = new BufferedWriter( new FileWriter( "C:\\resultClassify" ));
		ReadFile.readFileByLines(testFileString);
		double[] classifyPro = new double [10000];
		int MaxCategory;
		for (int i=0; i<ReadFile.featureLine; i++){
			for (int j=0;j<ReadFile.featureArrayType[i].arrayTypeIndex;j++)
				for (int k=0;k<fileIndex;k++){
					if (j==0){
						classifyPro[k]+=PiecewisePro(k,j,ReadFile.featureArrayType[i].featureDataType[j].count);
					}
					else
					classifyPro[k]=classifyPro[k]*PiecewisePro(k,j,ReadFile.featureArrayType[i].featureDataType[j].count);
				}	
			bw.write(MaxCategory=MaxClassifyPro(classifyPro,fileIndex));
			
			MaxCategory=MaxClassifyPro(classifyPro, fileIndex);
			System.out.println(MaxCategory);
			bw.newLine();
		}		
	}
	
	public static void main(String[] args) throws IOException{		
		TrainFeaturePro();
		PredictCategory();
	}
}
