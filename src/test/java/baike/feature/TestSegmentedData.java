package baike.feature;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;

import baike.common.Category;
import baike.common.DataLoader;

public class TestSegmentedData {
	private final String BAIKE_RAW_FILE 			= "baidu-dump.dat.50000";
	private final String BAIKE_SEGMENTED_FILE = "baidu-segmented.data.50000"; 
	
//	@Test @Ignore
//	public void testBuild() throws FileNotFoundException {		
//		InputStream rawIn = DataLoader.getInputStream(BAIKE_RAW_FILE);
//		int parallelLevel = 5;
//		SegmentedData.build(BAIKE_SEGMENTED_FILE, rawIn, parallelLevel);
//		
//		SegmentedData.Loader segLoader = new SegmentedData.Loader(BAIKE_SEGMENTED_FILE);
//		SegmentedData segData = new SegmentedData();
//		while(segLoader.next(segData)) {
//			System.out.println(segData);
//		}
//	}
	
	@Test
	public void testWriteRead() throws IOException {
		// Init
		SegmentedData segData = new SegmentedData();
		segData.setId(100); 
		segData.setTotalTerms(1000);
		segData.setCategories(new Category[]{ Category.HISTORY, Category.SOCIETY });
		String text = "军阀混战时期,\"督军\"是个很重要的职位.一般督军能够控制一个省,是割据一方的土皇帝";
		segData.analyze(text);
		// Write 
		ByteArrayOutputStream out0 = new ByteArrayOutputStream();
		DataOutputStream out = new DataOutputStream(out0);
		segData.write(out);
		// Read content
		ByteArrayInputStream in0 = new ByteArrayInputStream(out0.toByteArray());
		DataInputStream in = new DataInputStream(in0);		
		SegmentedData segData2 = new SegmentedData();
		segData2.readFields(in);
		// Check equality
		Assert.assertEquals(segData, segData2);
	}
}
