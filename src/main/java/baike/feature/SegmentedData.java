package baike.feature;

import java.io.Closeable;
import java.io.DataInput;
import java.io.DataInputStream;
import java.io.DataOutput;
import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.channels.FileChannel;
import java.util.Arrays;
import java.util.Formatter;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import baike.common.Category;
import baike.common.DataLoader;
import baike.common.Writable;
import baike.nlp.TermCounter;

public class SegmentedData implements Writable {
	private static final byte OK = 0, EOF = 1;
	
	public static void build(String segmentedFile, String rawFile, int numParts) throws IOException {
		String[] oldSegFiles = DataLoader.findFiles(segmentedFile+".*");
		DataLoader.removeFiles(oldSegFiles);
		String[] segFiles = getSegmentedFiles(segmentedFile, numParts);
		long[] rawFileOffsets = new long[numParts];
		int[] maxIds = new int[numParts];
		splitRawFiles(rawFile, numParts, rawFileOffsets, maxIds);
		for(int pi = 0; pi < numParts; pi++) {
			new Thread(new Builder(segFiles[pi], rawFile, rawFileOffsets[pi], maxIds[pi]).start();
		}
	}

	private static final int NL = '\n';
	private static void splitRawFiles(String rawFile, int numParts, 
														 long[] rawFileOffsets, int[] maxIds) throws IOException 
	{
		FileInputStream in 	= DataLoader.getFileInputStream(rawFile);
		FileChannel channel = in.getChannel();
		long size = channel.size();
		long partSize = size / numParts + 1;
		long pos = 0; int b;
		rawFileOffsets[0] = pos;
		for(int pi = 1; pi < numParts; pi ++) {
			pos = size * pi / partSize;
			channel.position(pos);
			// find the next blank line
			while(true) {
				b = in.read(); pos++; 
				if(b == NL) {
					b = in.read(); pos ++;
					if(b == NL) break;
				}
			}
			rawFileOffsets[pi] = pos;
			// the next three bytes should be "ID:"
			
		}
	}
	
	private static String[] getSegmentedFiles(String segmentedFile, int parts) {
		String[] segFiles = new String[parts];
		for(int pi = 0; pi < parts; pi++) {
			// TODO: better format the number suffix
			segFiles[pi] = segmentedFile + "." + pi;
		}
		return segFiles;
	}
	
	private static class Builder implements Runnable {
		private int maxId;
		private String segFile;
		private String rawFile;
		private long rawFileOffset;
		
		public Builder(String segFile, String rawFile, long rawFileOffset, int maxId) throws IOException {
			this.segFile = segFile;
			this.rawFile = rawFile;
			this.rawFileOffset = rawFileOffset;
			this.maxId = maxId;		
		}
		
		@Override
		public void run() {
			try {
				InputStream in = DataLoader.getInputStream(rawFile);
				in.skip(rawFileOffset);
				DataOutputStream out = new DataOutputStream(
																DataLoader.getOutputStream(segFile));
				RawData.Loader loader = new RawData.Loader(in);
				RawData rawData = new RawData();
				SegmentedData segData = new SegmentedData();
				while(loader.next(rawData)) {
					int id = rawData.getID();
					if(id >= maxId) break; 
					
					Category[] categories = rawData.getCategories(); 
					String fulltext = rawData.getFulltext();
					
					if(categories == null || categories.length == 0 || fulltext == null)
						continue;
					
					segData.reset();
					segData.setId(id);
					segData.setCategories( categories );
					segData.analyze(fulltext);
			
					out.writeByte(OK);
					segData.write(out);
				}
				out.writeByte(EOF);
			} catch (FileNotFoundException e) {
				throw new RuntimeException(e);
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		}
	}
	
	public static class Loader implements Closeable {
		private DataInputStream in;
		private boolean end = false;
		private int segFileIndex;
		private String[] segmentedFiles;
		
		public Loader(String segmentedFile) throws IOException {
			segmentedFiles = DataLoader.findFiles(segmentedFile + ".*");
			segFileIndex = -1;
			nextSegFile();
		}
		
		public boolean next(SegmentedData segData) throws IOException {
			if(end) return false;
			
			byte flag = in.readByte();
			if(flag == EOF && ! nextSegFile()) {
				end = true;
				return false;
			}
			assert(flag == OK);
			
			segData.readFields(in);
			return true;
		}
		
		private boolean nextSegFile() throws IOException {
			segFileIndex ++;
			if(segFileIndex >= segmentedFiles.length) return false;
			
			if(in != null) in.close();
			in = new DataInputStream(
						DataLoader.getInputStream(segmentedFiles[segFileIndex]) );
			return true;
		}
		
		@Override
		public void close() throws IOException {
			end = true;
			in.close();
		}
	}
	
	private int id;
	private Category[] categories;
	private int totalTerms;
	private Map<String, Integer> termCounts;
	
	private TermCounter counter; 
	
	public SegmentedData() {
		termCounts  = new HashMap<String, Integer>();
		counter = new TermCounter();
		
		reset();
	}
	
	public void reset() {
		id = -1;
		categories = null;
		totalTerms = 0;
		termCounts.clear();
		counter.reset();
	}
	
	public int  getId() 				{ return id; }
	public int 	getTotalTerms() { return totalTerms; }
	public Category[]	getCategories() { return categories; }
	
	public void setId(int id) { this.id = id; }
	public void setCategories(Category[] categories) { this.categories = categories; }
	
	public Set<String> getTerms() { return termCounts.keySet(); }
	public int getTermCount(String term) { 
		Integer count = termCounts.get(term);
		return count != null ? count.intValue() : 0;
	}
	public Map<String, Integer> getTermCounts() { return termCounts; }
	
	public void analyze(String fulltext) {
		counter.reset();
		termCounts.clear();
		counter.update(fulltext);
		Set<String> terms = counter.getTerms();
		for(String term : terms) {
			termCounts.put(term, counter.getCount(term));
		}
		totalTerms = counter.getTotalTerms();
	}
	
	@Override
	public void write(DataOutput out) throws IOException {
		out.writeInt(id);
		out.writeInt(totalTerms);
		out.writeByte(categories != null ? categories.length : 0);
		if(categories != null) {
			for(Category c : categories) 
				out.writeByte(c.ordinal());
		}
		Set<String> terms = getTerms();
		int numTerms = terms.size();
		out.writeInt(numTerms);
		for(String term : terms) {
			out.writeUTF(term);
			out.writeInt(getTermCount(term));
		}
	}

	@Override
	public void readFields(DataInput in) throws IOException {
		id = in.readInt();
		totalTerms = in.readInt();
		int numCategories = in.readByte();
		if(categories == null || numCategories != categories.length)
			categories = new Category[numCategories];
		for(int ci = 0; ci < numCategories; ci ++) {
			byte categoryIndex = in.readByte();
			categories[ci] = Category.values()[categoryIndex];
		}
		termCounts.clear();
		int numTerms = in.readInt();
		for(int ti = 0; ti < numTerms; ti ++) {
			String term  = in.readUTF();
			int 	 count = in.readInt();
			termCounts.put(term, count);
		}
	}

	@Override
	public boolean equals(Object o) {
		if (! (o instanceof SegmentedData) ) return false;
		
		SegmentedData another = (SegmentedData) o;
		return id == another.id &&
					 Arrays.equals(categories, another.categories) &&
					 totalTerms == another.totalTerms &&
					 termCounts.equals(another.termCounts);
	}
}
