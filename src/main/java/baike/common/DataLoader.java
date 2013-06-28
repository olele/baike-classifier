package baike.common;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

public class DataLoader {
	public static InputStream getInputStream(String path) throws FileNotFoundException {
		if(dataDir == null) init();
		return new FileInputStream(new File(dataDir, path));
	}
	
	public static OutputStream getOutputStream(String path) throws FileNotFoundException {
		if(dataDir == null) init(); 
		return new FileOutputStream(new File(dataDir, path));
	}
	
	private static final String DATA_DIR_NAME = "data.basedir";
	private static File dataDir = null;
	private static void init() {
		dataDir = new File( Configuration.get(DATA_DIR_NAME, "") );
	}
}
