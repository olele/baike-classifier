package baike.feature;

import java.io.Closeable;
import java.io.DataInput;
import java.io.DataInputStream;
import java.io.DataOutput;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Set;

import baike.common.Category;
import baike.common.Vector;
import baike.common.Writable;
import baike.nlp.TermCounter;

public class ArticleStatistics implements Writable {
	private static final byte EOF = 1;
	private static final byte OK  = 0;
	
	public static void build(OutputStream artStatsOut, InputStream rawDataIn, 
													 BaikeStatistics stats) throws IOException 
	{
		DataOutputStream out = new DataOutputStream(artStatsOut);
		RawData.Loader rawDataLoader = new RawData.Loader(rawDataIn);
		RawData rawData = new RawData();
		ArticleStatistics artStats = new ArticleStatistics(stats);
		TermCounter counter = new TermCounter();
		while(rawDataLoader.next(rawData)) {
			Category[] categories = rawData.getCategories(); 
			String fulltext = rawData.getFulltext();
			
			if(categories == null || categories.length == 0 || fulltext == null)
				continue;
			
			artStats.reset();
			artStats.setID( rawData.getID() );
			artStats.setCategories( categories );
			counter.reset(); counter.update(fulltext);
			artStats.updateFeature(counter);
			
			out.writeByte(OK);
			artStats.write(out);
		}
		out.writeByte(EOF);
		
		rawDataLoader.close();
		out.close();
	}
	
	public static class Loader implements Closeable {
		private DataInputStream in;
		private boolean end = false;
		
		public Loader(InputStream in) {
			this.in = new DataInputStream(in);
		}
		
		public boolean next(ArticleStatistics artStats) throws IOException {
			if(end) return false;
			
			byte flag = in.readByte();
			if(flag == EOF) {
				end = true;
				return false;
			}
			assert(flag == OK);
			
			artStats.readFields(in);
			return true;
		}
		
		@Override
		public void close() throws IOException {
			in.close();
		}
	}
	
	private BaikeStatistics baikeStats;
	private int ID;
	private Category[] categories; 
	private Vector feature;
	private int total;
	
	public ArticleStatistics(BaikeStatistics baikeStats) {
		this.baikeStats = baikeStats;
		int numTerms = baikeStats.getNumTerms();
		this.categories = null;
		this.feature = new Vector(numTerms);
		this.total = 0;
	}
	
	public void reset() {
		ID = -1;
		categories = null;
		feature.reset();
		total = 0;
	}

	public int getID() {
		return ID;
	}
	
	public int getTotal() {
		return total;
	}
	
	public Category[] getCategories() {
		return categories;
	}
	
	public Vector getFeature() {
		return feature;
	}
	
	public void setID(int ID) {
		this.ID = ID;
	}
	
	public void setCategories(Category[] categories) {
		this.categories = categories;
	}
	
	public void updateFeature(TermCounter counter) {		
		Set<String> terms = counter.getTerms();
		Iterator<String> it = terms.iterator();
		while(it.hasNext()) {
			String term = it.next();
			int termIndex = baikeStats.getTermIndex(term);
			feature.set( termIndex, counter.getCount(term) );
		}
		total += counter.getTotalTerms();
	}
	
	@Override
	public boolean equals(Object o) {
		if(! (o instanceof ArticleStatistics) ) return false;
		
		ArticleStatistics another = (ArticleStatistics) o;
		return ID == another.ID &&
					 Arrays.equals(categories, another.categories) &&
					 feature.equals(another.feature);
	}
	
	@Override
	public String toString() {
		return "{" + ID + ", " + Arrays.toString(categories) + ", " + feature + "}";
	}

	@Override
	public void write(DataOutput out) throws IOException {
		out.writeInt(ID);
		out.writeByte(categories != null ? categories.length : 0);
		if(categories != null) {
			for(Category c : categories) 
				out.writeByte(c.ordinal());
		}
		feature.write(out);
	}

	@Override
	public void readFields(DataInput in) throws IOException {
		ID = in.readInt();
		int numCategories = in.readByte();
		if(categories == null || numCategories != categories.length)
			categories = new Category[numCategories];
		for(int ci = 0; ci < numCategories; ci ++) {
			byte categoryIndex = in.readByte();
			categories[ci] = Category.values()[categoryIndex];
		}
		feature.readFields(in);
	}
}
