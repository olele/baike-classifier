package baike.common;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.Arrays;


public class Vector implements Writable {
	private float[] vals;
	private int dimension;
	
	public Vector(int dimension) {
		this.dimension = dimension;
		this.vals = new float[dimension];
	}

	// Clone constructor
	public Vector(Vector v) {
		this.dimension = v.dimension;
		this.vals = Arrays.copyOf(v.vals, v.dimension);
	}
	
	public float get(int index) {
		return vals[index];
	}
	
	public void set(int index, float val) {
		vals[index] = val;
	}
	
	public int dimension() {
		return dimension;
	}
	
	public void write(DataOutput out) throws IOException {
		out.writeInt(dimension);
		for(float v : vals)
			out.writeFloat(v);
	}

	public void readFields(DataInput in) throws IOException {
		int newDimension = in.readInt();
		if(newDimension != dimension) {
			dimension = newDimension;
			vals = new float[dimension];
		}
		for(int i = 0; i < dimension; i++)
			vals[i] = in.readFloat();
	}

	@Override
	public boolean equals(Object o) {
		if(!(o instanceof Vector)) return false;
		
		Vector another = (Vector) o;
		return dimension == another.dimension &&
					 Arrays.equals(vals, another.vals);
	}
	
	@Override
	public String toString() {
		return '<' + Arrays.toString(vals) + '>';
	}
}
