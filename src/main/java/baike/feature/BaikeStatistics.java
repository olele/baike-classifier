package baike.feature;

import java.io.DataInput;
import java.io.DataInputStream;
import java.io.DataOutput;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import baike.common.Writable;
import baike.nlp.TermCounter;

public class BaikeStatistics implements Writable {
	public static BaikeStatistics build(InputStream rawDataIn) throws IOException {
		RawData.Loader loader = new RawData.Loader(rawDataIn);
		return build(loader);
	}
	
	public static BaikeStatistics build(RawData.Loader loader) throws IOException {
		RawData raw = new RawData();
		TermCounter counter = new TermCounter();
		BaikeStatistics stats = new BaikeStatistics();
		while(loader.next(raw)) {
			String fulltext = raw.get("FullText");
			if(fulltext != null) {
				counter.reset();
				counter.update(fulltext);
				stats.accumulate(counter);
			}
		}
		return stats;
	}
	
	public static BaikeStatistics load(InputStream in) throws IOException {
		BaikeStatistics stats 	= new BaikeStatistics();
		DataInputStream dataIn 	= new DataInputStream(in);
		stats.readFields(dataIn);
		dataIn.close();
		return stats;
	}
	
	private BaikeStatistics() {
		// a hash map that preserves insertion order
		term2Index = new LinkedHashMap<String, Integer>();
		numTerms 	= 0;
		capacity  = 1024;
		terms			= new String[capacity];
		termFreqs = new int[capacity];
		docFreqs  = new int[capacity];
		total 		= 0;
	}
	
	private Map<String, Integer> term2Index;
	private int numTerms;
	private int capacity;
	private String[] terms;
	private int[] termFreqs;
	private int[] docFreqs;
	private long total;

	private void accumulate(TermCounter counter) {
		// add new terms
		Set<String> newTerms = counter.getTerms();
		for(String term : newTerms) {
			if(!term2Index.containsKey(term)) {
				term2Index.put(term, Integer.valueOf(numTerms));
				terms[numTerms] = term;
				numTerms ++;
				if(numTerms == capacity) {
					capacity *= 2;
					terms			= Arrays.copyOf(terms, capacity);
					termFreqs = Arrays.copyOf(termFreqs, capacity);
					docFreqs  = Arrays.copyOf(termFreqs, capacity);
				}
			}
		}
		// increase the count for all terms
		total += counter.getTotalTerms();
		// accumulate frequency
		for(String term : newTerms) {
			int count = counter.getCount(term);
			Integer termIndex = term2Index.get(term);
			// add term frequency
			termFreqs[termIndex] += count;
			// add doc frequency
			docFreqs[termIndex] += 1;
		}
	}
	
	public long getTotal() {
		return total;
	}
	
	public int getNumTerms() {
		return numTerms;
	}
	
	public String getTerm(int index) {
		return terms[index];
	}
	
	public int getTermIndex(String term) {
		Integer index = term2Index.get(term);
		return index != null ? index.intValue() : -1;
	}
	
	public int getTermFrequency(int termIndex) {
		return termFreqs[termIndex];
	}
	
	public int getDocumentFrequency(int termIndex) {
		return docFreqs[termIndex];
	}
	
	public void dump(OutputStream out) throws IOException {
		DataOutputStream dataOut = new DataOutputStream(out);
		write(dataOut);
		dataOut.flush();
		dataOut.close();
	}
	
	@Override
	public String toString() {
		final int ONLY_FIRST_N_ITEMS = 100;
		StringBuilder sb = new StringBuilder();
		Iterator<Entry<String, Integer>> it = term2Index.entrySet().iterator();
		int nextIndex = 0;
		sb.append('<').append(numTerms).append('|');
		while(it.hasNext() && nextIndex < ONLY_FIRST_N_ITEMS) {			
			Entry<String, Integer> entry = it.next();
			String term 	= entry.getKey();
			Integer index = entry.getValue(); 
			assert(index == nextIndex);
			if(index != 0) sb.append(';');
			sb.append(term).append(':')
				.append(termFreqs[index]).append(',')
				.append(docFreqs[index]);
			nextIndex ++;
		}
		if(numTerms > ONLY_FIRST_N_ITEMS) sb.append("...");
		sb.append('>');
		return sb.toString();
	}
	
	@Override
	public void write(DataOutput out) throws IOException {
		out.writeInt(numTerms);
		out.writeLong(total);
		Iterator<Entry<String, Integer>> it = term2Index.entrySet().iterator();
		int seq = 0;
		while(it.hasNext()) {
			Entry<String, Integer> entry = it.next();
			String term 	= entry.getKey();
			Integer index = entry.getValue(); 
			out.writeUTF(term);
			assert(index == seq++);
		}
		for(int index = 0; index < numTerms; index++) 
			out.writeInt(termFreqs[index]);
		for(int index = 0; index < numTerms; index++) 
			out.writeInt(docFreqs[index]);
	}

	@Override
	public void readFields(DataInput in) throws IOException {
		term2Index.clear();
		numTerms = capacity = in.readInt();
		terms = new String[capacity];
		total = in.readLong();
		for(int index = 0; index < numTerms; index++) {
			String term = in.readUTF();
			term2Index.put(term, index);
			terms[index] = term;
		}
		termFreqs = new int[numTerms];
		for(int index = 0; index < numTerms; index++) 
			termFreqs[index] = in.readInt();
		docFreqs = new int[numTerms];
		for(int index = 0; index < numTerms; index++) 
			docFreqs[index] = in.readInt();
	}
	
	@Override
	public boolean equals(Object o) {
		if(! (o instanceof BaikeStatistics) ) return false;
		
		BaikeStatistics another = (BaikeStatistics) o;
		if( numTerms != another.numTerms ||
				total	!= another.total || 
				!term2Index.equals(another.term2Index) ) return false;
		for(int index = 0; index < numTerms; index++) 
			if( termFreqs[index] != another.termFreqs[index] ||
			 		docFreqs[index] != another.docFreqs[index] )
				return false;
		return true;
	}
}
