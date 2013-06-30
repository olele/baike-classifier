package baike.bayes;
import java.util.*;


public class DocFeature {
	 static ArrayList<double []> feaArrayList = new ArrayList<double[]>();
	 String categoryString;
	public static ArrayList<double[]> getFeaArrayList() {
		return feaArrayList;
	}
	public static void setFeaArrayList(ArrayList<double[]> feaArrayList) {
		DocFeature.feaArrayList = feaArrayList;
	}
	public String getCategoryString() {
		return categoryString;
	}
	public void setCategoryString(String categoryString) {
		this.categoryString = categoryString;
	}
	 
}
