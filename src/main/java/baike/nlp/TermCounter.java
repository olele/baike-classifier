package baike.nlp;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.ansj.domain.Term;
import org.ansj.splitWord.analysis.ToAnalysis;

public class TermCounter {
	private Map<String, Counter> counter;
	
	public TermCounter() {
		// order preserving hash map
		counter = new LinkedHashMap<String, Counter>();
	}

	public void reset() {
		counter.clear();
	}
	
	public void update(String text) {
		List<Term> paser = ToAnalysis.paser(text);
		Iterator<Term> it = paser.iterator();
		while(it.hasNext()) {
			String term = it.next().getName().trim();
			// ignore stop word
			if(StopWords.contains(term)) 
				continue;	
			// increase count
			Counter count = counter.get(term);
			if(count != null) 
				count.increase();
			else
				counter.put(term, new Counter(1));
		}
	}
	
	public int getCount(String term) {
		Counter count = counter.get(term);
		return count != null ? count.get() : 0;
	}
	
	public Set<String> getTerms() {
		return counter.keySet();
	}
	
	@Override
	public String toString() {
		return counter.toString();
	}
}
