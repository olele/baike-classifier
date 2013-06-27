package baike.feature;

import java.io.BufferedReader;
import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;

public class RawData {
	public static class Loader implements Closeable {
		private BufferedReader reader;
		private String line;
		
		public Loader(InputStream in) {
			reader = new BufferedReader(new InputStreamReader(in));
		}

		public boolean next(RawData raw) throws IOException {
			// find the first non-blank line
			do {
				line = reader.readLine();
				if(line == null) return false;
			
				line = line.trim();
			} while(line.length() == 0);
			// read the following lines until a blank line
			raw.reset();
			do {
				int posSemicolon = line.indexOf(':');
				if(posSemicolon < 0) throw new IOException("Invalid line: " + line);
				String key = line.substring(0, posSemicolon),
							 val = line.substring(posSemicolon + 1, line.length()); 
				raw.set(key, val);
				
				line = reader.readLine();
				if(line == null) break;
				line = line.trim();
			} while(line.length() > 0);
			
			return true;
		}
		
		@Override
		public void close() throws IOException {
			reader.close();
		}
	}
	
	private Map<String, String> keyVals;
	
	public RawData() {
		keyVals = new LinkedHashMap<String, String>();
	}
	
	public void reset() {
		keyVals.clear();
	}
	
	public void set(String key, String val) {
		keyVals.put(key, val);
	}
	
	public String get(String key) {
		return keyVals.get(key);
	}
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		Iterator<Entry<String,String>> it = keyVals.entrySet().iterator();
		while(it.hasNext()) {
			Entry<String, String> entry = it.next();
			String key = entry.getKey(), val = entry.getValue();
			sb.append(key).append(':').append(val).append('\n');
		}
		return sb.toString();
	} 
}
