package baike.common;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Random;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class TestVector {
	private static final double PRECISION = 1E-10;

	private Random random = new Random();
	private final int dimension = 100;
	private Vector vector;
	
	@Before
	public void setup() {
		vector = new Vector(dimension);
	}
	
	@Test
	public void testIO() throws IOException {
		// Write 
		ByteArrayOutputStream out0 = new ByteArrayOutputStream();
		DataOutputStream out = new DataOutputStream(out0);
		for(int i = 0; i < dimension; i++)
			vector.set(i, random.nextFloat());
		vector.write(out);
		// Read content
		ByteArrayInputStream in0 = new ByteArrayInputStream(out0.toByteArray());
		DataInputStream in = new DataInputStream(in0);		
		Vector readVector = new Vector(dimension);
		readVector.readFields(in);
		// Check equality
		Assert.assertEquals(vector, readVector);
	}
	
	@Test
	public void testGetSet() {
		int dimension = 100;
		Vector vector = new Vector(dimension);
		for(int i = 0; i < dimension; i++) {
			// Values are 0 by default
			Assert.assertEquals(0, vector.get(i), PRECISION);
			// Randomly assign a new value
			float newVal = random.nextFloat();
			vector.set(i, newVal);
			Assert.assertEquals(newVal, vector.get(i), PRECISION);
		}
	}
}
