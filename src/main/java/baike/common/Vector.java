package baike.common;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;


public class Vector implements Writable {
	// vals.get(index) is the value of the (index)-th attribute  
	private SortedMap<Integer, Float> vals;
	private int dimension;
	
	public Vector(int dimension) {
		this.dimension = dimension;
		this.vals = new TreeMap<Integer, Float>();
	}

	// Clone constructor
	public Vector(Vector v) {
		this.dimension = v.dimension;
		this.vals = new TreeMap<Integer, Float>(v.vals);
	}
	
	public void reset() {
		vals.clear();
	}
	
	public float get(int index) {
		if(index < 0 || index >= dimension) 
			throw new IndexOutOfBoundsException();

		Float val = vals.get(Integer.valueOf(index));
		return val != null ? val.floatValue() : 0;
	}
	
	public void set(int index, float val) {
		if(index < 0 || index >= dimension) 
			throw new IndexOutOfBoundsException();
		
		vals.put(Integer.valueOf(index), Float.valueOf(val));
	}
	
	public int dimension() {
		return dimension;
	}
	
	public float[] toArray(float[] array) {
		// Make sure array has correct size and is filled with 0s
		if(array.length != dimension) 
			array = new float[dimension];
		else 
			Arrays.fill(array, 0.0f);
		
		Iterator<Entry<Integer, Float>> it = iterator();
		Entry<Integer, Float> entry; Integer index; Float val;
		while(it.hasNext()) {
			entry = it.next(); index = entry.getKey(); val = entry.getValue();
			// Set non-zero values
			array[index.intValue()] = val.floatValue();
		}
		
		return array;
	}
	
	public void write(DataOutput out) throws IOException {
		out.writeInt(dimension);
		
		Set<Entry<Integer, Float>> entries = vals.entrySet();
		int numNonZeroVals = entries.size();
		out.writeInt(numNonZeroVals);
		
		Iterator<Entry<Integer, Float>> it = entries.iterator();
		while(it.hasNext()) {
			Entry<Integer, Float> e = it.next();
			Integer index = e.getKey();
			Float		val		= e.getValue();
			out.writeInt(index.intValue());
			out.writeFloat(val.floatValue());
		}
	}

	public void readFields(DataInput in) throws IOException {
		int newDimension = in.readInt();
		if(newDimension != dimension) {
			throw new IOException("The dimension is not matched");
		}

		int numNonZeroVals = in.readInt();
		vals.clear();
		for(int i = 0; i < numNonZeroVals; i++) {
			int index = in.readInt();
			float val = in.readFloat();
			set(index, val);
		}
	}

	@Override
	public boolean equals(Object o) {
		if(!(o instanceof Vector)) return false;
		
		Vector another = (Vector) o;
		return dimension == another.dimension &&
					 vals.equals(another.vals);
	}
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append('<').append(dimension).append('|');

		Iterator<Entry<Integer, Float>> it = iterator();
		boolean is_first = true;
		while(it.hasNext()) {
			if(is_first) is_first = false;
			else sb.append(',');
			
			Entry<Integer, Float> e = it.next();
			Integer index = e.getKey();
			Float		val		= e.getValue();
			
			sb.append(index).append(':').append(val);
		}
		
		sb.append(">");
		return sb.toString();
	}
	
	private Iterator<Entry<Integer, Float>> iterator() {		
		Set<Entry<Integer, Float>> entries = vals.entrySet();
		return entries.iterator();
	}
}
