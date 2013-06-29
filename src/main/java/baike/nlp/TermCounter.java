package baike.nlp;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.ansj.domain.Nature;
import org.ansj.domain.Term;
import org.ansj.splitWord.analysis.ToAnalysis;
import org.ansj.util.recognition.NatureRecognition;

public class TermCounter {
	private static final boolean ONLY_NOUNS = true;
	
	private Map<String, Counter> counter;
	private int total;
	
	public TermCounter() {
		// order preserving hash map
		counter = new LinkedHashMap<String, Counter>();
		total = 0;
	}

	public void reset() {
		counter.clear();
		total = 0;
	}
	
	public void update(String text) {
		// do word segmentation
		List<Term> paser = ToAnalysis.paser(text);
		// decide part of speech
		if(ONLY_NOUNS) new NatureRecognition(paser).recognition() ;
		
		total += paser.size();
		
		Iterator<Term> it = paser.iterator();
		while(it.hasNext()) {
			Term term = it.next();
			// ignore non-nouns
			if( ONLY_NOUNS && !isNoun(term) ) continue;
			String strTerm = term.getName().trim();
			// ignore stop word
			if(StopWords.contains(strTerm)) 
				continue;
			
			// increase count
			Counter count = counter.get(strTerm);
			if(count != null) 
				count.increase();
			else
				counter.put(strTerm, new Counter(1));
		}
	}
	
	private static final String[] NOUNS = { "n", "vn" };
	private static boolean isNoun(Term term) {
		Nature nature = term.getNatrue();
		String partOfSpeech = nature.natureStr;
		for(String noun : NOUNS) 
			if(partOfSpeech.equals(noun)) return true;
		return false;
	}
	
	public int getCount(String term) {
		Counter count = counter.get(term);
		return count != null ? count.get() : 0;
	}
	
	public int getTotalTerms() {
		return total;
	}
	
	public Set<String> getTerms() {
		return counter.keySet();
	}
	
	@Override
	public String toString() {
		return counter.toString();
	}
}
