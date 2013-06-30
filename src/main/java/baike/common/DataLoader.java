package baike.common;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

public class DataLoader {
	public static InputStream getInputStream(String file) throws FileNotFoundException {
		File path = getAbsolutePath(file);
		InputStream fin = new FileInputStream(path);
		return new BufferedInputStream(fin);
	}
	
	public static OutputStream getOutputStream(String file) throws FileNotFoundException {
		File path = getAbsolutePath(file);
		OutputStream fout = new FileOutputStream(path);
		return new BufferedOutputStream(fout);
	}
	
	public static FileInputStream getFileInputStream(String file) throws FileNotFoundException {		
		File path = getAbsolutePath(file);
		return new FileInputStream(path);
	}
	
	public static File getAbsolutePath(String file) {
		if(dataDir == null) init();
		return new File(dataDir, file);
	}
	
	// TODO:
	public static String[] findFiles(String regrex) {
		return null;
	}
	
	public static void removeFiles(String[] files) {
	}
	
	
	private static final String DATA_DIR_NAME = "data.basedir";
	private static File dataDir = null;
	private static void init() {
		dataDir = new File( Configuration.get(DATA_DIR_NAME, "") );
	}
}
