package baike.bayes;
public class ArrayType {
	DataType [] featureDataType;
	int arrayTypeIndex;
	
	public ArrayType(int dimension) {
		featureDataType = new DataType [dimension];
	}
	
	public DataType[] getFeatureDataType() {
		return featureDataType;
	}
	public void setFeatureDataType(DataType[] featureDataType) {
		this.featureDataType = featureDataType;
	}
	public int getArrayTypeIndex() {
		return arrayTypeIndex;
	}
	public void setArrayTypeIndex(int arrayTypeIndex) {
		this.arrayTypeIndex = arrayTypeIndex;
	}
	
}
