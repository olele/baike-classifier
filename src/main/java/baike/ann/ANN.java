package baike.ann;

import java.util.ArrayList;

import org.neuroph.core.learning.DataSet;
import org.neuroph.core.learning.DataSetRow;
import org.neuroph.nnet.MultiLayerPerceptron;

public class ANN {
	public int input_dim;
	public int output_dim;
	int data_length;
	DataSet trainingSet;
	MultiLayerPerceptron mp;
	
	public ANN( int FirstLayer, int MiddleLayer, int LastLayer )
	{
		data_length = 0;
		input_dim = FirstLayer;
		output_dim = LastLayer;
		trainingSet = new DataSet(FirstLayer, LastLayer);
		ArrayList<Integer> layers = new ArrayList<Integer>();
//		System.out.println("haha1");
		layers.add(FirstLayer);
		layers.add(MiddleLayer);
		layers.add(LastLayer);
//		System.out.println("haha2");
		mp = new MultiLayerPerceptron( layers );
//		System.out.println("haha3");
		System.out.println("Create ANN: "+FirstLayer+" - "+MiddleLayer+" - "+LastLayer);
	}
	
	public boolean addDataSetRow( double[] input_vector, double[] output_vector )
	{
		if ( input_vector.length != input_dim && output_vector.length != output_dim )
			return false;
		trainingSet.addRow(new DataSetRow(input_vector, output_vector));
		data_length++;
		System.out.println("Add Data Row index: "+data_length+" and output: "+output_vector.toString());
		return true;
	}
	
	public void train()
	{
		System.out.println("Training ANN...");
		mp.learn(trainingSet);	
	}
	
	public double[] predict( double[] input_vector )
	{
		System.out.println("Predict...");
		mp.setInput(input_vector);
		// calculate network
		mp.calculate();
		// get network output 
		return mp.getOutput();
//		double[] networkOutput = mp.getOutput(); 
//		for ( int i=0; i<networkOutput.length; i++ )
//			System.out.println(networkOutput[i]+",");
	}
	
	public static void main(String[] args) {
//		for (int i = 1; i < 1000; i+=100) {
//			System.out.print("time of "+i+" : ");
//			long l = System.currentTimeMillis();
//			new ANN(i, 1000, 10);
//			System.out.println(System.currentTimeMillis()-l);
//		}
		new ANN(10000, 1000, 10);
		
		
	}
	
	
	
}
